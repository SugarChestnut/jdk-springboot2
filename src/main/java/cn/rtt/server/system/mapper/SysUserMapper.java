package cn.rtt.server.system.mapper;

import cn.rtt.server.system.domain.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author xql
 * @since 2024-11-13
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    IPage<SysUser> selectPageData(@Param("page") IPage<SysUser> buildPage, @Param("user") SysUser user);

    List<SysUser> selectLists(@Param("user") SysUser user);

    SysUser selectUserById(Long userId);

    SysUser selectByUsername(String userName);

    List<SysUser> selectUserByPhone(@Param("user") SysUser user);

    Integer checkAllMobile(@Param("id") Long id, @Param("mobile") String mobile);

    List<SysUser> listData();

    List<SysUser> getAdminUser();

    List<SysUser> getBmjl();

    IPage<SysUser> forSelect(@Param("page") Page<SysUser> page, @Param("params") Map<String, Object> params);
}
