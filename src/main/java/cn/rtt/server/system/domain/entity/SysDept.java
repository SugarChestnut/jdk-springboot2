package cn.rtt.server.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rtt
 * @date 2026/3/11 09:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dept")
@ToString(callSuper = true)
public class SysDept extends BaseEntity{

    /** 部门ID */
    @TableId(value = "dept_id", type = IdType.AUTO)
    private Long deptId;

    /** 父部门ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 部门名称 */
    @NotNull(message = "部门名称不能为空")
    private String deptName;

    /** 显示顺序 */
    private Integer orderNum;

    /** 负责人 */
    private Long leaderId;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    private Integer status;

    /** 备注 */
    private String remark;

    @JsonIgnore
    private String roleIds;

    @TableField(exist = false)
    private List<SysDept> children = new ArrayList<>();

    @TableField(exist = false)
    private String leader;

    private List<Long> roleArray;
}
