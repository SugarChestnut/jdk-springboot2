package cn.rtt.server.system.service;

import cn.rtt.server.system.constant.StatusEnum;
import cn.rtt.server.system.dao.SysDeptRepository;
import cn.rtt.server.system.domain.entity.SysDept;
import cn.rtt.server.system.domain.request.dept.DeptSearchRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author rtt
 * @date 2026/3/11 13:12
 */
@Service
@AllArgsConstructor
public class SysDeptServiceImpl implements SysDeptService {

    private final SysDeptRepository deptRepository;

    private static final String COMMA_SEPARATOR = ",";

    @Override
    public List<SysDept> search(DeptSearchRequest request) {
        return buildDeptTree(deptRepository.getBaseMapper().search(request));
    }

    @Override
    public List<SysDept> searchForSelect(DeptSearchRequest request) {
        request.setStatus(StatusEnum.DELETED.getCode());
        Page<SysDept> page = new Page<>(1, 10);
        deptRepository.getBaseMapper().search(page, request);
        return page.getRecords();
    }

    @Override
    public void createDept(SysDept dept) {
        checkDept(dept);
        deptRepository.save(dept);
    }

    @Override
    @Transactional
    public void updateDept(SysDept dept) {
        if (dept.getDeptId() == null) throw new IllegalArgumentException("未指定部门");
        checkDept(dept);
        // TODO 更新员工权限缓存
        deptRepository.updateById(dept);
    }

    private void checkDept(SysDept dept) {
        if (dept.getOrderNum() == null) dept.setOrderNum(0);
        if (dept.getParentId() != null && dept.getParentId() != 0) {
            SysDept parentDept = deptRepository.getById(dept.getParentId());
            if (parentDept == null) throw new IllegalArgumentException("上级部门不存在");
            if (StatusEnum.NORMAL.getCode() != parentDept.getStatus())
                throw new IllegalArgumentException("上级部门已经停用");
            if (StringUtils.isBlank(parentDept.getAncestors())) {
                dept.setAncestors(dept.getParentId().toString());
            } else {
                dept.setAncestors(parentDept.getAncestors() + COMMA_SEPARATOR + dept.getParentId());
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
                if (!Objects.equals(d.getDeptId(), dept.getDeptId()))
                    throw new IllegalArgumentException("部门名称已存在");
            }
        } else {
            if (deptRepository.count(w1) > 0) throw new IllegalArgumentException("部门名称已存在");
        }
        if (dept.getRoleArray() != null && !dept.getRoleArray().isEmpty()) {
            dept.setRoleIds(dept.getRoleArray().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(COMMA_SEPARATOR)));
        }
    }

    @Override
    public void deleteDept(long sysDeptId) {

    }

    /**
     * 构建菜单树
     */
    private List<SysDept> buildDeptTree(List<SysDept> depts) {
        for (SysDept dept : depts) {
            if (StringUtils.isBlank(dept.getRoleIds())) {
                dept.setRoleArray(new ArrayList<>());
            } else {
                String[] split = dept.getRoleIds().split(COMMA_SEPARATOR);
                List<Long> collect = Arrays.stream(split).map(Long::valueOf).collect(Collectors.toList());
                dept.setRoleArray(collect);
            }
        }
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
