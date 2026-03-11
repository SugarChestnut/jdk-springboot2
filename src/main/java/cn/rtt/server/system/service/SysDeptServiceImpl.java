package cn.rtt.server.system.service;

import cn.rtt.server.system.dao.SysDeptRepository;
import cn.rtt.server.system.domain.entity.SysDept;
import cn.rtt.server.system.domain.request.dept.DeptSearchRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author rtt
 * @date 2026/3/11 13:12
 */
@Service
@AllArgsConstructor
public class SysDeptServiceImpl implements SysDeptService{

    private final SysDeptRepository deptRepository;

    private static final String ANCESTOR_SEPARATOR = ",";

    @Override
    public List<SysDept> treeSearch(DeptSearchRequest request) {
        LambdaQueryWrapper<SysDept> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.isNoneBlank(request.getDeptName()), SysDept::getDeptName, request.getDeptName());
        return buildDeptTree(deptRepository.list(w));
    }

    @Override
    public void createDept(SysDept dept) {
        checkDept(dept);
        deptRepository.save(dept);
    }

    @Override
    public void updateDept(SysDept dept) {
        if (dept.getDeptId() == null) throw new IllegalArgumentException("未指定部门");
        checkDept(dept);
        deptRepository.updateById(dept);
    }

    private void checkDept(SysDept dept) {
        if (dept.getOrderNum() == null) dept.setOrderNum(0);
        if (dept.getParentId() != null && dept.getParentId() != 0) {
            SysDept parentDept = deptRepository.getById(dept.getParentId());
            if (parentDept == null) throw new IllegalArgumentException("上级部门不存在");
            if (parentDept.getStatus()) throw new IllegalArgumentException("上级部门已经停用");
            if (StringUtils.isBlank(parentDept.getAncestors())) {
                dept.setAncestors(dept.getParentId().toString());
            } else {
                dept.setAncestors(parentDept.getAncestors() + ANCESTOR_SEPARATOR + dept.getParentId());
            }
        } else {
            dept.setAncestors("");
        }
        LambdaQueryWrapper<SysDept> w1 = new LambdaQueryWrapper<>();
        w1.eq(SysDept::getDeptName, dept.getDeptName());
        w1.eq(SysDept::getParentId, dept.getParentId() != null ? dept.getParentId() : 0);
        if (dept.getDeptId() != null) {
            List<SysDept> list = deptRepository.list(w1);
            for (SysDept d : list) {
                if (!Objects.equals(d.getDeptId(), dept.getDeptId())) throw new IllegalArgumentException("部门名称已存在");
            }
        } else {
            if (deptRepository.count(w1) > 0) throw new IllegalArgumentException("部门名称已存在");
        }
    }

    @Override
    public void deleteDept(long sysDeptId) {

    }

    /**
     * 构建菜单树
     */
    private List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> topDept = depts.stream()
                .filter(dept -> dept.getParentId() == null || dept.getParentId() == 0)
                .collect(Collectors.toList());

        for (SysDept dept : topDept) {
            dept.setChildren(buildChildMenu(depts, dept.getDeptId()));
        }
        return topDept;
    }

    private List<SysDept> buildChildMenu(List<SysDept> list, long parentId) {
        List<SysDept> childList = list.stream().filter(dept -> dept.getParentId() == parentId).collect(Collectors.toList());
        for (SysDept dept : childList) {
            dept.setChildren(buildChildMenu(list, dept.getDeptId()));
        }
        return childList;
    }
}
