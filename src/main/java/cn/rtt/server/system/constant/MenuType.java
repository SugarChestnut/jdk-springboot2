package cn.rtt.server.system.constant;

import lombok.Getter;

/**
 * @author rtt
 * @date 2026/2/11 17:03
 */
@Getter
public enum MenuType {

    CATALOGUE("M"),
    MENU("C"),
    BUTTON("F");

    final String type;

    MenuType(String type) {
        this.type = type;
    }
}
