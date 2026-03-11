package cn.rtt.server.system.service;

import cn.rtt.server.system.constant.ResultCode;
import cn.rtt.server.system.constant.UserConstants;
import cn.rtt.server.system.dao.SysRoleMenuRepository;
import cn.rtt.server.system.dao.SysRoleRepository;
import cn.rtt.server.system.dao.SysUserRoleRepository;
import cn.rtt.server.system.domain.entity.SysRole;
import cn.rtt.server.system.domain.entity.SysRoleMenu;
import cn.rtt.server.system.domain.entity.SysUserRole;
import cn.rtt.server.system.domain.request.role.RoleSearchRequest;
import cn.rtt.server.system.domain.response.SysPage;
import cn.rtt.server.system.exception.SystemException;
import cn.rtt.server.system.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 角色信息表 服务实现类
 */
@Service
@Slf4j
@AllArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {
    private final SysRoleRepository roleRepository;
    private final SysRoleMenuRepository roleMenuRepository;
    private final SysUserRoleRepository userRoleRepository;
    private final SysRoleMenuService roleMenuService;

    /**
     * ???
     * @param userId 用户ID
     * @return
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRole> perms = roleRepository.getBaseMapper().selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (perm != null) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public SysPage<SysRole> pageSearch(RoleSearchRequest request) {
        IPage<SysRole> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<SysRole> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.isNotBlank(request.getRoleName()), SysRole::getRoleName, request.getRoleName());
        w.like(StringUtils.isNotBlank(request.getRoleKey()), SysRole::getRoleKey, request.getRoleKey());
        w.orderByDesc(SysRole::getRoleId);
        roleRepository.page(page, w);
        return SysPage.transform(page);
    }

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(role.getRoleKey()), SysRole::getRoleKey, role.getRoleKey())
                .eq(SysRole::getStatus, 0)
                .like(StringUtils.isNotEmpty(role.getRoleName()), SysRole::getRoleName, role.getRoleName())
                .orderByDesc(SysRole::getGmtCreate);
        return roleRepository.list(wrapper);
    }

    /**
     * ???
     * @param roleId
     * @return
     */
    @Override
    public SysRole selectRoleById(Long roleId) {
        SysRole role = roleRepository.getById(roleId);
//        if (role == null) {
//            throw new SystemException(ResultCode.CODE_ERROR);
//        }
//
//        List<SysRoleMenu> roleMenus = roleMenuService.getByRoleId(roleId);
//        if (CollectionUtils.isNotEmpty(roleMenus)) {
//            role.setMenuIds(roleMenus.stream().map(SysRoleMenu::getMenuId).distinct().toArray(Long[]::new));
//        }
        return role;
    }

    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        long roleId = role.getRoleId() == null ? -1L : role.getRoleId();
        SysRole info = roleRepository.getBaseMapper().checkRoleNameUnique(role.getRoleName());
        if (info != null && info.getRoleId() != roleId) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        long roleId = role.getRoleId() == null ? -1L : role.getRoleId();
        SysRole info = roleRepository.getBaseMapper().checkRoleKeyUnique(role.getRoleKey());
        if (info != null && info.getRoleId() != roleId) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole role) {
        // 新增角色信息
        roleRepository.getBaseMapper().insertRole(role);
        return insertRoleMenu(role);
    }

    @Override
    public void checkRoleAllowed(Long roleId) {
//        SysRole byId = roleRepository.getById(role.getId());
//        if (RoleEnum.isSup(role.getId())) {
//
//            if (StatusConstant.enble.intValue() == byId.getStatus().intValue()) {
//                throw new BusinessException("不允许操作超级管理员角色");
//            }
//
//        }
//        if (!SecurityUtils.isSup() && !RoleEnum.isAdmin(SecurityUtils.getLoginUser().getUser().getRoleIds()) && byId.getCreateById().longValue() != SecurityUtils.getUserId()) {
//            throw new SystemException("不允许修改别人创建的角色");
//        }

    }

    @Override
    public void checkRoleDataScope(List<Long> roleIds) {
        for (Long roleId : roleIds) {
            if (roleId < 0) {
                continue;
            }
            SysRole role = roleRepository.getById(roleId);
            if (role == null) {
                throw new SystemException(ResultCode.TOKEN_INVALID_OR_EXPIRED);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleStatus(SysRole role) {
        LambdaUpdateWrapper<SysRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysRole::getStatus, role.getStatus()).eq(SysRole::getRoleId, role.getRoleId());
        roleRepository.update(role, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long roleId) {
//        if (RoleEnum.isContains(roleId)) {
//            throw new SystemException("管理员不能删除!");
//        }
        checkRoleAllowed(roleId);
        checkRoleDataScope(List.of(roleId));
        SysRole role = selectRoleById(roleId);
        if (userRoleRepository.getBaseMapper().countUserRoleByRoleId(roleId) > 0) {
            throw new SystemException(String.format("%1$s已分配,不能删除", role.getRoleName()));
        }
        // 删除角色与菜单关联
        LambdaUpdateWrapper<SysRoleMenu> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuRepository.getBaseMapper().delete(wrapper);
        roleRepository.removeById(roleId);
    }

    @Override
    public List<SysRole> selectRoleAll() {
        List<SysRole> sysRoles = this.selectRoleList(new SysRole());
//        if (!SecurityUtils.isSup()) {
//
//            sysRoles = sysRoles.stream().filter(filter ->  filter.getCompanyId()==null &&
//                            !Lists.newArrayList(RoleEnum.ADMIN_ROLE.getCode(), RoleEnum.SUP_ROLE.getCode(), RoleEnum.COM_ROLE.getCode()).contains(filter.getRoleId())).
//                    collect(Collectors.toList());
//        } else{
//
//        }

        return sysRoles;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAuthUser(SysUserRole userRole) {
        return userRoleRepository.getBaseMapper().deleteData(userRole) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addAuthUser(SysUserRole userRole) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userRole.getUserId()).eq(SysUserRole::getRoleId, userRole.getRoleId());
        if (CollectionUtils.isNotEmpty(userRoleRepository.list(wrapper))) {
            throw new SystemException(ResultCode.USER_ALREADY);
        }
        return userRoleRepository.save(userRole);
    }

    @Override
    public void updateRole(SysRole role) {
        roleRepository.updateById(role);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    @Transactional(rollbackFor = Exception.class)
    public int insertRoleMenu(SysRole role) {
        if (role.getRoleId() != null) {
            roleMenuService.removeByRoleId(role.getRoleId());
        }
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<>();
//        for (Long menuId : role.getMenuIds()) {
//            SysRoleMenu rm = new SysRoleMenu();
//            rm.setRoleId(role.getRoleId());
//            rm.setMenuId(menuId);
//            list.add(rm);
//        }
        if (!list.isEmpty()) {
            rows = roleMenuRepository.getBaseMapper().batchRoleMenu(list);
        }
        return rows;
    }
}
