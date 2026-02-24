package cn.rtt.server.system.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rtt
 * @date 2026/2/12 09:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Select {

    private String label;

    private Object value;
}
