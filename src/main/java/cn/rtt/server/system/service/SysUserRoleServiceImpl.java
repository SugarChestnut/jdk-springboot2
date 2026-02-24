package cn.rtt.server.system.service;


import cn.rtt.server.system.constant.ResultCode;
import cn.rtt.server.system.dao.SysUserRoleRepository;
import cn.rtt.server.system.domain.entity.SysUserRole;
import cn.rtt.server.system.exception.SystemException;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户和角色关联表 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-07-18
 */
@Service
@Slf4j
@AllArgsConstructor
public class SysUserRoleServiceImpl implements SysUserRoleService {

    private final SysUserRoleRepository userRoleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserRoleByUserId(Long userId) {
        if (userId == null) {
            throw new SystemException(ResultCode.PARAM_ERROR);
        }

        LambdaUpdateWrapper<SysUserRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        userRoleRepository.remove(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserRole(Long[] userIds) {
        LambdaUpdateWrapper<SysUserRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysUserRole::getUserId, List.of(userIds));
        userRoleRepository.remove(wrapper);
    }
}
