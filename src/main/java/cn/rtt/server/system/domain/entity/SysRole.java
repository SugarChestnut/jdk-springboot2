package cn.rtt.server.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色信息表
 * </p>
 *
 * @author xql
 * @since 2024-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_role")
@ToString(callSuper = true)
public class SysRole extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色权限字符串
     */
    private String roleKey;
    /**
     * 角色状态（0正常 1停用）
     */
    private Boolean status;
    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private List<SysMenu> menus;
}
