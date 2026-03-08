package cn.rtt.server.system.constant;

import cn.rtt.server.system.domain.entity.SysRole;
import lombok.Getter;

import java.util.List;

@Getter
public enum RoleEnum {
    SUPER_ADMIN("super_admin", "超级管理员"),
    ADMIN_ROLE("admin", "管理员")
    ;

    private final String key;
    private final String info;

    RoleEnum(String key, String info) {
        this.key = key;
        this.info = info;
    }

    public boolean isContain(String key) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.key.equals(key)) return true;
        }
        return false;
    }

    public static boolean isAdmin(List<SysRole> roles) {
       return hasRole(roles, ADMIN_ROLE);
    }

    public static boolean isSuperAdmin(List<SysRole> roles) {
        return hasRole(roles, SUPER_ADMIN);
    }

    private static boolean hasRole(List<SysRole> roles, RoleEnum roleEnum) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        for (SysRole role : roles) {
            if (roleEnum.key.equals(role.getRoleKey())) {
                return true;
            }
        }
        return false;
    }
}
