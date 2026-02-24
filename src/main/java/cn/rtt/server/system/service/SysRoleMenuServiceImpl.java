package cn.rtt.server.system.service;

import cn.rtt.server.system.dao.SysRoleMenuRepository;
import cn.rtt.server.system.domain.entity.SysRoleMenu;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色和菜单关联表 服务实现类
 */
@Service
@Slf4j
@AllArgsConstructor
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    private final SysRoleMenuRepository roleMenuRepository;

    @Override
    public List<SysRoleMenu> getByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        return roleMenuRepository.list(wrapper);
    }

    @Override
    public void removeByRoleId(Long roleId) {
        roleMenuRepository.removeById(roleId);
    }
}
