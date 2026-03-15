package cn.rtt.server.system.service;


import cn.rtt.server.system.domain.entity.SysDept;
import cn.rtt.server.system.domain.request.dept.DeptSearchRequest;

import java.util.List;

public interface SysDeptService {
    /**
     * 获取部门树
     */
    List<SysDept> search(DeptSearchRequest request);

    List<SysDept> searchForSelect(DeptSearchRequest request);

    void createDept(SysDept sysDept);

    void updateDept(SysDept sysDept);

    void deleteDept(long sysDeptId);
}
