package cn.rtt.server.system.security.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author rtt
 * @date 2026/3/20 15:23
 */
@Setter
@Getter
@AllArgsConstructor
public class JwtValidateResult {

    private boolean valid;

    private boolean expired;

    private String tokenId;

}
