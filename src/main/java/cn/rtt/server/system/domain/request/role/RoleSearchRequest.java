package cn.rtt.server.system.domain.request.role;

import cn.rtt.server.system.domain.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class RoleSearchRequest extends PageRequest {

    private String roleName;
    private String roleKey;
}
