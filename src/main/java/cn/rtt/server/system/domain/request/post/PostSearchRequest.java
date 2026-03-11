package cn.rtt.server.system.domain.request.post;

import cn.rtt.server.system.domain.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author rtt
 * @date 2026/3/11 16:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class PostSearchRequest extends PageRequest {

    private String postCode;
    private String postName;
}
