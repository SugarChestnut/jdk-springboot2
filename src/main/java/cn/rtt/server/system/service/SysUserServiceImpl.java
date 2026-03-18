package cn.rtt.server.system.service;

import cn.rtt.server.system.cahce.CacheService;
import cn.rtt.server.system.constant.*;
import cn.rtt.server.system.dao.SysUserRepository;
import cn.rtt.server.system.dao.SysUserRoleRepository;
import cn.rtt.server.system.domain.dto.ImageParam;
import cn.rtt.server.system.domain.dto.UpdatePassword;
import cn.rtt.server.system.domain.dto.UserBaseEdit;
import cn.rtt.server.system.domain.entity.SysRole;
import cn.rtt.server.system.domain.entity.SysUser;
import cn.rtt.server.system.domain.entity.SysUserRole;
import cn.rtt.server.system.domain.request.user.UserSearchRequest;
import cn.rtt.server.system.domain.response.SysPage;
import cn.rtt.server.system.exception.SystemException;
import cn.rtt.server.system.utils.CollectionUtils;
import cn.rtt.server.system.utils.IpUtils;
import cn.rtt.server.system.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.BindException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserRepository userRepository;

    private final SysUserRoleService userRoleService;

    private final SysUserRoleRepository userRoleRepository;

    private final CacheService cacheService;

    @Override
    public SysPage<SysUser> search(UserSearchRequest request) {
        IPage<SysUser> page = new Page<>(request.getPageNum(), request.getPageSize());
        if (!SecurityUtils.isSupAdmin()) {
            request.setSuperAdminKey(RoleEnum.SUPER_ADMIN.getCode());
        }
        userRepository.getBaseMapper().search(page, request);
        page.getRecords().forEach(u -> {
            u.setStatusDesc(UserStatus.getDescByCode(u.getStatus()));
            u.setPassword(null);
        });
        return SysPage.transform(page);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser getUser(String userName) {
        return userRepository.getBaseMapper().selectByUsername(userName);
    }

    @Override
    public void updateLoginIp(Long userId, String ip) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(ip);
        userRepository.updateById(sysUser);
    }

    // ============================

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return userRepository.getBaseMapper().selectLists(user);
    }




    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        return userRepository.getBaseMapper().selectUserById(userId);
    }


    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(SysUser user) {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        SysUser exist = userRepository.getBaseMapper().selectByUsername(user.getUsername());
        if (exist != null) return false;
        Boolean b = checkAllMobile(userId, user.getMobile());
        if (exist.getUserId().longValue() != userId.longValue() || !b) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     */
    @Override
    public boolean checkPhoneUnique(SysUser user) {
        long userId = user.getUserId() == null ? -1L : user.getUserId();
        SysUser info = checkPhoneUniqueMapper(user);
        if (info != null && info.getUserId() != userId) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    public SysUser checkPhoneUniqueMapper(SysUser user) {
        List<SysUser> users = userRepository.getBaseMapper().selectUserByPhone(user);
        return CollectionUtils.isNotEmpty(users) ? users.get(0) : null;
    }


    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId) {
        SysUser user = new SysUser();
        user.setStatus(0);
        user.setUserId(userId);
        List<SysUser> users = this.selectUserList(user);
        if (CollectionUtils .isEmpty(users)) {
            throw new SystemException(ResultCode.USER_NULL);
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUser(SysUser user) {
        userRepository.save(user);
        insertUserRole(user);
    }


    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerUser(SysUser user) {
        if (userRepository.save(user)) {
            SysUserRole sysUserRole = new SysUserRole();
            return userRoleRepository.save(sysUserRole);
        }
        return false;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean updateUser(SysUser user) {
        return userRepository.updateById(user);
    }


    /**
     * 重置用户密码
     *
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPwd(UpdatePassword data) {
        SysUser user = userRepository.getById(data.getUserId());
        if (user == null) throw new SystemException(ResultCode.USER_NULL);
//        String code = redisUtil.get(CacheConstants.UPDATE_PASSWORD + user.getUuid());
//        if (!StringUtils.equals(code, user.getCode())) {
//            throw new BusinessException("验证码错误！");
//        }
        if (StringUtils.isBlank(data.getOldPassword())) throw new SystemException("旧密码不能为空!");
        if (StringUtils.isBlank(data.getPassword())) throw new SystemException("新密码不能为空!");
        if (!SecurityUtils.matchesPassword(data.getOldPassword(), user.getPassword()))
            throw new SystemException("旧密码不正确!");

        user.setPassword(SecurityUtils.encryptPassword(data.getPassword()));
        return userRepository.updateById(user);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        this.insertUserRole(user.getUserId(), Collections.emptyList());
    }


    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<>(roleIds.size());
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleRepository.saveBatch(list, roleIds.size());
        }
    }


    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            List<SysRole> roles = this.selectUserById(userId).getRoles();
            // 判断是否为超级管理员
            if (!SecurityUtils.isAdmin()) {
                throw new SystemException("非超级管理员不允许删除管理员");
            }
        }
        // 删除用户与角色关联
        userRoleService.deleteUserRole(userIds);
        return userRepository.removeByIds(List.of(userIds));
    }


    @Override
    public IPage<SysUser> selectUserPage(SysUser user) {
        IPage<SysUser> buildPage = new Page<>(1, 10);
//        user.setId(SecurityUtils.getUserId());
        userRepository.getBaseMapper().selectPageData(buildPage, user);
        if (CollectionUtils.isNotEmpty(buildPage.getRecords())) {
            buildPage.getRecords().forEach(foreach -> foreach.setPassword(null));
        }
        return buildPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String changeAvatar(MultipartFile file, ImageParam imageParam) {
//        SysUser byId = userRepository.getById(SecurityUtils.getUserId());
//        if (!SecurityUtils.isSup() && byId.getId().longValue() != SecurityUtils.getUserId().longValue()) {
//            throw new SystemException(ErrorCodeEnum.USER_NULL.getResultCode(), "登录用户不一致，不能修改别人头像");
//        }
//        if (!fileService.checkFileType(file, "image")) {
//            throw new SystemException(ErrorCodeEnum.BODY_NOT_MATCH.getResultCode(), "上传的图片格式有误");
//        }
//        String save = fileService.saveImage(file, UUID.randomUUID() + ":" + file.getOriginalFilename(), PathConstants.avatarURL + "/" + SecurityUtils.getUserId(), imageParam);
//        fileService.deleteFile(byId.getAvatarUrl());
//        byId.setAvatarUrl(save);
//        this.updateById(byId);
//
//        return save;
        return "xxx";
    }

    @Override
    public Boolean checkAllow(Long userId) {
        List<SysRole> roles = selectUserById(userId).getRoles();
        if (SecurityUtils.isAdmin()) {
            if (RoleEnum.isAdmin(roles) && userId.longValue() != SecurityUtils.getUserId().longValue()) {
                throw new SystemException("不允许操作非本人超级管理员!");
            }
        } else {
            if (userId.longValue() != SecurityUtils.getUserId().longValue() && RoleEnum.isAdmin(roles)) {
                throw new SystemException("不允许操作非本人管理员数据!");
            }
        }
        return true;
    }

    @Override
    public void checkAllowToUpdate(SysUser user) {
    }


    @Override
    public Boolean checkAllMobile(Long id, String mobile) {
        return userRepository.getBaseMapper().checkAllMobile(id, mobile) < 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBaseInfo(UserBaseEdit userBaseEdit, HttpServletResponse response) throws FileUploadException, IOException {

        if (userBaseEdit.getUserId() == null) userBaseEdit.setUserId(SecurityUtils.getUserId());

        SysUser byId = userRepository.getById(userBaseEdit.getUserId());
        String oldMobile = byId.getMobile();

        BeanUtils.copyProperties(userBaseEdit, byId);

        if (StringUtils.isBlank(byId.getMobile())) {
            throw new BindException("手机不能为空!");
        }
        if (!Strings.CS.equals(userBaseEdit.getMobile(), oldMobile) && StringUtils.isNotEmpty(oldMobile)) {

            String code = (String) cacheService.get(CacheConstants.USER_CHANGE_PHONE_KEY + userBaseEdit.getUuid());
            if (!Strings.CS.equals(code, userBaseEdit.getCode())) {
                throw new SystemException("验证码错误!");
            }
        }

        return userRepository.updateById(byId);
    }


    @Override
    public List<SysUser> selectUserByIds(Set<Long> sysUsers) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysUser::getUserId, sysUsers);
        return userRepository.list(wrapper);
    }

    @Override
    public List<SysUser> listData() {
        return userRepository.getBaseMapper().listData();
    }

    @Override
    public List<SysUser> getAdminUser() {
        return userRepository.getBaseMapper().getAdminUser();
    }

    @Override
    public List<SysUser> getBmjl() {
        return userRepository.getBaseMapper().getBmjl();
    }

    @Override
    public void updateLoginTime(Long id, Long loginTime) {
        Instant instant = Instant.ofEpochMilli(loginTime);
        // 转换为LocalDateTime，默认时区是系统时区
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getUserId, id).set(SysUser::getLoginTime, localDateTime);
        userRepository.update(wrapper);
    }


}
