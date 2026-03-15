package cn.rtt.server.system.domain.request.role;

import lombok.Data;

@Data
public class AuthUserRequest {

    private Long roleId;
    private Long[] userIds;
}
