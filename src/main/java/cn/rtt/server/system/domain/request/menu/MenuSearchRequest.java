package cn.rtt.server.system.domain.request.menu;

import lombok.Data;

/**
 * @author rtt
 * @date 2026/3/9 10:36
 */
@Data
public class MenuSearchRequest {
    private String title;
    private String menuType;
    private Long userId;
}
