package cn.rtt.server.system.constant;

import cn.rtt.server.system.domain.response.Option;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据权限枚举
 * 对应数据范围选项：全部、自定、本部门、本部门及以下、仅本人
 */
@Getter
public enum DataRoleEnum {

    ALL("1", "全部数据权限"),
    CUSTOM("2", "自定数据权限"),
    DEPT("3", "本部门数据权限"),
    DEPT_AND_SUB("4", "本部门及以下数据权限"),
    SELF("5", "仅本人数据权限");

    /**
     * 权限值（对应数据库存储的值）
     */
    private final String value;

    /**
     * 权限描述（对应前端显示的标签）
     */
    private final String label;

    /**
     * 构造方法
     */
    DataRoleEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 根据value获取枚举
     */
    public static DataRoleEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (DataRoleEnum dataRole : DataRoleEnum.values()) {
            if (dataRole.getValue().equals(value)) {
                return dataRole;
            }
        }
        return null;
    }

    /**
     * 根据value获取label
     */
    public static String getLabelByValue(String value) {
        DataRoleEnum dataRole = getByValue(value);
        return dataRole != null ? dataRole.getLabel() : null;
    }

    /**
     * 判断是否为指定权限
     */
    public boolean equals(String value) {
        return this.value.equals(value);
    }


    public static List<Option> options() {
        return Arrays.stream(DataRoleEnum.values())
                .map(d -> new Option(d.getLabel(), d.getValue()))
                .collect(Collectors.toList());
    }
}
