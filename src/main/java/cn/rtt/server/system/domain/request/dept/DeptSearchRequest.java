package cn.rtt.server.system.domain.request.dept;

import lombok.Data;

/**
 * @author rtt
 * @date 2026/3/9 10:36
 */
@Data
public class DeptSearchRequest {
    private String deptName;
    private Integer status;
}
