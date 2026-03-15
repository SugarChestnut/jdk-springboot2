package cn.rtt.server.system.service;

import cn.rtt.server.system.constant.DataScopeEnum;
import cn.rtt.server.system.constant.RoleEnum;
import cn.rtt.server.system.constant.UserStatus;
import cn.rtt.server.system.dao.*;
import cn.rtt.server.system.domain.entity.*;
import cn.rtt.server.system.domain.request.role.AuthUserRequest;
import cn.rtt.server.system.domain.request.role.RoleSearchRequest;
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
    private final SysUserRepository userRepository;

    @Override
    public SysPage<SysRole> search(RoleSearchRequest request) {
        IPage<SysRole> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<SysRole> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.isNotBlank(request.getRoleName()), SysRole::getRoleName, request.getRoleName());
        w.like(StringUtils.isNotBlank(request.getRoleKey()), SysRole::getRoleKey, request.getRoleKey());
        w.ne(SysRole::getRoleKey, RoleEnum.SUPER_ADMIN.getCode());
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

    @Override
    @Transactional
    public void authUser(AuthUserRequest request) {
        SysRole role = roleRepository.getById(request.getRoleId());
        if (role == null || role.getStatus()) throw new IllegalArgumentException("角色不存在或已停用");

        if (RoleEnum.SUPER_ADMIN.getCode().equals(role.getRoleKey())) throw new IllegalArgumentException("不能分配超级管理员角色");

        if (!SecurityUtils.isSupAdmin() && RoleEnum.ADMIN_ROLE.getCode().equals(role.getRoleKey()))
            throw new IllegalArgumentException("当前用户不能分配管理员角色");

        Long[] userIds = request.getUserIds();
        if (userIds == null || userIds.length == 0) throw new IllegalArgumentException("未选择用户");

        List<SysUser> users = userRepository.listByIds(List.of(userIds));
        List<SysUser> collect = users.stream().filter(u -> UserStatus.OK.getCode() == u.getStatus()).collect(Collectors.toList());
        if (collect.size() != userIds.length) throw new IllegalArgumentException("非正常用户");

        LambdaQueryWrapper<SysUserRole> w = new LambdaQueryWrapper<>();
        w.eq(SysUserRole::getRoleId, request.getRoleId());
        w.in(SysUserRole::getUserId, List.of(userIds));
        List<SysUserRole> list = userRoleRepository.list(w);
        Set<Long> exist = list.stream().map(SysUserRole::getUserId).collect(Collectors.toSet());
        List<SysUserRole> entities = new ArrayList<>();
        Arrays.stream(userIds).filter(i -> !exist.contains(i)).forEach(i -> entities.add(new SysUserRole(i, request.getRoleId())));
        userRoleRepository.saveBatch(entities, 100);
    }

    @Override
    @Transactional
    public void unAuthUser(AuthUserRequest request) {
        if (request.getRoleId() == null) throw new IllegalArgumentException("未指定角色");

        Long[] userIds = request.getUserIds();
        if (userIds == null || userIds.length == 0) throw new IllegalArgumentException("未选择用户");

        SysRole role = roleRepository.getById(request.getRoleId());
        if (role != null) {
            if (RoleEnum.SUPER_ADMIN.getCode().equals(role.getRoleKey())) throw new IllegalArgumentException("不能删除超级管理员角色");

            if (!SecurityUtils.isSupAdmin() && RoleEnum.ADMIN_ROLE.getCode().equals(role.getRoleKey()))
                throw new IllegalArgumentException("当前用户不能取消管理员角色");
        }

        LambdaQueryWrapper<SysUserRole> w = new LambdaQueryWrapper<>();
        w.eq(SysUserRole::getRoleId, request.getRoleId());
        w.in(SysUserRole::getUserId, List.of(userIds));
        userRoleRepository.remove(w);
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
        if (userRoleRepository.getBaseMapper().countUserRoleByRoleId(roleId) > 0) {
            throw new SystemException("请先取消该角色的用户授权");
        }
        // 删除角色与菜单关联
        LambdaUpdateWrapper<SysRoleMenu> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuRepository.getBaseMapper().delete(wrapper);
        roleRepository.removeById(roleId);
    }
}
