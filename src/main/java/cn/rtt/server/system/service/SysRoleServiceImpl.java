package cn.rtt.server.system.service;

import cn.rtt.server.system.constant.DataScopeEnum;
import cn.rtt.server.system.constant.ResultCode;
import cn.rtt.server.system.constant.RoleEnum;
import cn.rtt.server.system.constant.UserConstants;
import cn.rtt.server.system.dao.SysRoleDeptRepository;
import cn.rtt.server.system.dao.SysRoleMenuRepository;
import cn.rtt.server.system.dao.SysRoleRepository;
import cn.rtt.server.system.dao.SysUserRoleRepository;
import cn.rtt.server.system.domain.LoginUser;
import cn.rtt.server.system.domain.entity.SysRole;
import cn.rtt.server.system.domain.entity.SysRoleDept;
import cn.rtt.server.system.domain.entity.SysRoleMenu;
import cn.rtt.server.system.domain.entity.SysUserRole;
import cn.rtt.server.system.domain.request.role.RoleSearchRequest;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.domain.response.SysPage;
import cn.rtt.server.system.exception.SystemException;
import cn.rtt.server.system.utils.CollectionUtils;
import cn.rtt.server.system.utils.SecurityUtils;
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
import java.util.stream.Collectors;

/**
 * 角色信息表 服务实现类
 */
@Service
@Slf4j
@AllArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleRepository roleRepository;
    private final SysRoleMenuRepository roleMenuRepository;
    private final SysRoleDeptRepository roleDeptRepository;
    private final SysUserRoleRepository userRoleRepository;
    private final SysRoleMenuService roleMenuService;

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
    public Set<Long> menu(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> w = new LambdaQueryWrapper<>();
        w.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = roleMenuRepository.list(w);
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toSet());
    }

    @Override
    public Set<Long> dept(Long roleId) {
        LambdaQueryWrapper<SysRoleDept> w = new LambdaQueryWrapper<>();
        w.eq(SysRoleDept::getRoleId, roleId);
        List<SysRoleDept> roleMenus = roleDeptRepository.list(w);
        return roleMenus.stream().map(SysRoleDept::getDeptId).collect(Collectors.toSet());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(SysRole role) {
        checkRole(role);
        roleRepository.save(role);
        saveRoleMenu(role, false);
        saveRoleDept(role, false);
    }

    @Override
    @Transactional
    public void updateRole(SysRole role) {
        checkRole(role);
        saveRoleMenu(role, true);
        saveRoleDept(role, true);
        roleRepository.updateById(role);
    }

    private void checkRole(SysRole role) {
        if (RoleEnum.SUPER_ADMIN.getCode().equals(role.getRoleKey())
                || RoleEnum.SUPER_ADMIN.getDesc().equals(role.getRoleName())) {
            throw new IllegalArgumentException("不允许创建修改超级管理员");
        }
        LambdaQueryWrapper<SysRole> w = new LambdaQueryWrapper<>();
        w.eq(SysRole::getRoleName, role.getRoleName()).or().eq(SysRole::getRoleKey, role.getRoleKey());
        List<SysRole> roles = roleRepository.list(w);

        if (role.getRoleId() != null) {
            SysRole oldRole = roleRepository.getById(role.getRoleId());
            if (oldRole == null) throw new IllegalArgumentException("角色不存在");
            if (RoleEnum.ADMIN_ROLE.getCode().equals(oldRole.getRoleKey()) && !SecurityUtils.isSupAdmin()) {
                throw new IllegalArgumentException("不允许修改管理员");
            }
            Optional<SysRole> any = roles.stream().filter(r -> !Objects.equals(r.getRoleId(), role.getRoleId())).findAny();
            if (any.isPresent()) throw new IllegalArgumentException("角色名称或角色权限已存在");
        } else if (!roles.isEmpty()) {
            throw new IllegalArgumentException("角色名称或角色权限已存在");
        }
    }

    private void saveRoleMenu(SysRole role, boolean update) {
        if (update) {
            LambdaQueryWrapper<SysRoleMenu> w = new LambdaQueryWrapper<>();
            w.eq(SysRoleMenu::getRoleId, role.getRoleId());
            List<SysRoleMenu> roleMenus = roleMenuRepository.list(w);
            Set<Long> oldMenuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toSet());
            if (CollectionUtils.equals(role.getMenuIds(), oldMenuIds)) return;

            roleMenuRepository.remove(w);
        }
        if (CollectionUtils.isNotEmpty(role.getMenuIds())) {
            List<SysRoleMenu> roleMenus = role.getMenuIds().stream()
                    .map(r -> new SysRoleMenu(role.getRoleId(), r))
                    .collect(Collectors.toList());
            roleMenuRepository.saveBatch(roleMenus, 100);
        }
    }

    private void saveRoleDept(SysRole role, boolean update) {
        if (update) {
            LambdaQueryWrapper<SysRoleDept> w = new LambdaQueryWrapper<>();
            w.eq(SysRoleDept::getRoleId, role.getRoleId());
            List<SysRoleDept> roleDepts = roleDeptRepository.list(w);
            Set<Long> oldDeptIds = roleDepts.stream().map(SysRoleDept::getDeptId).collect(Collectors.toSet());
            if (CollectionUtils.equals(role.getDeptIds(), oldDeptIds)) return;

            roleDeptRepository.remove(w);
        }
        if (DataScopeEnum.CUSTOM.getCode().equals(role.getDataScope())
                && role.getDeptIds() != null
                && !role.getDeptIds().isEmpty()) {
            List<SysRoleDept> roleDepts = role.getDeptIds().stream()
                    .map(r -> new SysRoleDept(role.getRoleId(), r))
                    .collect(Collectors.toList());
            roleDeptRepository.saveBatch(roleDepts, 100);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleStatus(SysRole role) {
        LambdaUpdateWrapper<SysRole> w = new LambdaUpdateWrapper<>();
        w.set(SysRole::getStatus, role.getStatus());
        w.eq(SysRole::getRoleId, role.getRoleId());
        roleRepository.update(role, w);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long roleId) {
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

    /**
     * ???
     *
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
}
