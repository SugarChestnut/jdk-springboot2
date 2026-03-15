package cn.rtt.server.system.mapper;

import cn.rtt.server.system.domain.entity.SysDept;
import cn.rtt.server.system.domain.request.dept.DeptSearchRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    List<SysDept> search(@Param("request") DeptSearchRequest request);

    IPage<SysDept> search(@Param("page") IPage<SysDept> page,
                         @Param("request") DeptSearchRequest request);
}
