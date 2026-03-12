package cn.rtt.server.system.service;


import cn.rtt.server.system.domain.entity.SysRole;
import cn.rtt.server.system.domain.entity.SysUserRole;
import cn.rtt.server.system.domain.request.role.RoleSearchRequest;
import cn.rtt.server.system.domain.response.SysPage;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色信息表 服务类
 * </p>
 *
 * @author author
 * @since 2024-07-18
 */
public interface SysRoleService {

    /**
     * 分页搜素角色
     */
    SysPage<SysRole> pageSearch(RoleSearchRequest request);
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

    List<SysRole> selectRoleList(SysRole role);

    SysRole selectRoleById(Long roleId);


    void updateRoleStatus(SysRole role);

    void deleteById(Long roleId);

    List<SysRole> selectRoleAll();

    Boolean deleteAuthUser(SysUserRole userRole);

    Boolean addAuthUser(SysUserRole userRole);


}
