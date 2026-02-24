package cn.rtt.server.system.dao;

import cn.rtt.server.system.domain.entity.SysUser;
import cn.rtt.server.system.mapper.SysUserMapper;
import org.springframework.stereotype.Component;

/**
 * @author rtt
 * @date 2025/11/20 15:41
 */
@Component
public class SysUserRepository extends BaseRepository<SysUserMapper, SysUser>{
}
