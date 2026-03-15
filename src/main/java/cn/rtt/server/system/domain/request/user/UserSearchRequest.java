package cn.rtt.server.system.domain.request.user;

import cn.rtt.server.system.domain.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class UserSearchRequest extends PageRequest {

    private String username;
    private String mobile;
    private Long roleId;
    private Long deptId;
    private Long noRoleId;
    private String superAdminKey;
    private Integer status;
}
