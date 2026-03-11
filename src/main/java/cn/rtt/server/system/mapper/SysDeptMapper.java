package cn.rtt.server.system.mapper;

import cn.rtt.server.system.domain.entity.SysDept;
import cn.rtt.server.system.domain.entity.SysMenu;
import cn.rtt.server.system.domain.request.menu.MenuSearchRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author xql
 * @since 2024-11-13
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

}
