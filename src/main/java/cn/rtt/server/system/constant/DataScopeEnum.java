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
public enum DataScopeEnum {

    ALL("1", "全部数据权限"),
    CUSTOM("2", "自定数据权限"),
    DEPT("3", "本部门数据权限"),
    DEPT_AND_SUB("4", "本部门及以下数据权限"),
    SELF("5", "仅本人数据权限");

    /**
     * 权限值（对应数据库存储的值）
     */
    private final String code;

    /**
     * 权限描述（对应前端显示的标签）
     */
    private final String desc;

    /**
     * 构造方法
     */
    DataScopeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据value获取枚举
     */
    public static DataScopeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (DataScopeEnum dataRole : DataScopeEnum.values()) {
            if (dataRole.getCode().equals(code)) {
                return dataRole;
            }
        }
        return null;
    }

    /**
     * 根据value获取label
     */
    public static String getDesc(String code) {
        DataScopeEnum dataRole = getByCode(code);
        return dataRole != null ? dataRole.getDesc() : null;
    }

    /**
     * 判断是否为指定权限
     */
    public boolean equals(String code) {
        return this.code.equals(code);
    }


    public static List<Option> options() {
        return Arrays.stream(DataScopeEnum.values())
                .map(d -> new Option(d.getDesc(), d.getCode()))
                .collect(Collectors.toList());
    }
}
