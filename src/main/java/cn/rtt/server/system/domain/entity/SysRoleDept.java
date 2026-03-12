package cn.rtt.server.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_role_dept")
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SysRoleDept extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long deptId;

    public SysRoleDept(Long roleId, Long deptId) {
        this.roleId = roleId;
        this.deptId = deptId;
    }
}
