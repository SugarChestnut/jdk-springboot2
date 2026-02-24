package cn.rtt.server.system.service;


import cn.rtt.server.system.domain.entity.SysRole;
import cn.rtt.server.system.domain.entity.SysUserRole;
import com.baomidou.mybatisplus.core.metadata.IPage;

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
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectRolePermissionByUserId(Long userId);

    IPage<SysRole> selectRolePage(SysRole role);

    List<SysRole> selectRoleList(SysRole role);

    SysRole selectRoleById(Long roleId);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean checkRoleKeyUnique(SysRole role);

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    int insertRole(SysRole role);

    int insertRoleMenu(SysRole role);

    /**
     * 校验角色是否允许操作
     */
    void checkRoleAllowed(Long roleId);

    /**
     * 校验角色是否有数据权限
     *
     * @param roleIds 角色id
     */
    void checkRoleDataScope(List<Long> roleIds);

    void updateRoleStatus(SysRole role);

    void deleteById(Long roleId);

    List<SysRole> selectRoleAll();

    Boolean deleteAuthUser(SysUserRole userRole);

    Boolean addAuthUser(SysUserRole userRole);

    void updateRole(SysRole role);
}
