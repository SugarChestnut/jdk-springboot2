package cn.rtt.server.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    @NotEmpty(message = "角色名称不能为空")
    private String roleName;
    /**
     * 角色权限字符串
     */
    @NotEmpty(message = "角色权限不能为空")
    private String roleKey;
    /**
     * 角色状态（0正常 1停用）
     */
    private Boolean status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 数据权限
     * {@link cn.rtt.server.system.constant.DataScopeEnum}
     */
    @NotEmpty(message = "角色数据权限不能为空")
    private String dataScope;

    @TableField(exist = false)
    private Set<Long> menuIds;

    @TableField(exist = false)
    private Set<Long> deptIds;
}
