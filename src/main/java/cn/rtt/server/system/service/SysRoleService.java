package cn.rtt.server.system.service;

import cn.rtt.server.system.domain.entity.SysRole;
import cn.rtt.server.system.domain.request.role.AuthUserRequest;
import cn.rtt.server.system.domain.request.role.RoleSearchRequest;
import cn.rtt.server.system.domain.response.SysPage;

import java.util.Set;

public interface SysRoleService {

    /**
     * 分页搜素角色
     */
    SysPage<SysRole> search(RoleSearchRequest request);

    /**
     * 根据角色ID查询当前角色分配的菜单ID
     */
    Set<Long> menu(Long roleId);

    /**
     * 根据角色ID查询当前角色分配数据权限部门
     */
    Set<Long> dept(Long roleId);

    /**
     * 新增保存角色信息
     */
    void createRole(SysRole role);

    /**
     * 更新角色
     */
    void updateRole(SysRole role);

    /**
     * 授权用户
     */
    void authUser(AuthUserRequest request);

    /**
     * 取消授权
     */
    void unAuthUser(AuthUserRequest request);

    /**
     * 停用角色
     */
    void updateRoleStatus(SysRole role);

    /**
     * 删除角色
     */
    void deleteById(Long roleId);

}
