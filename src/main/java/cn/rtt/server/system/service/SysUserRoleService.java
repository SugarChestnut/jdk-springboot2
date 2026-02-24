package cn.rtt.server.system.service;

/**
 * <p>
 * 用户和角色关联表 服务类
 * </p>
 *
 * @author author
 * @since 2024-07-18
 */
public interface SysUserRoleService {

    void deleteUserRoleByUserId(Long userId);

    void deleteUserRole(Long[] userIds);
}
