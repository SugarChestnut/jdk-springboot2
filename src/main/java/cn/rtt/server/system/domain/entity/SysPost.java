package cn.rtt.server.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author rtt
 * @date 2026/3/11 16:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_post")
@ToString(callSuper = true)
public class SysPost extends BaseEntity{

    @TableId(value = "post_id", type = IdType.AUTO)
    private Long postId;

    @TableField("post_code")
    @NotNull(message = "岗位编码不能为空")
    private String postCode;

    @TableField("post_name")
    @NotNull(message = "岗位名称不能为空")
    private String postName;

    @TableField("order_num")
    private Integer orderNum;

    @TableField("status")
    private Boolean status;

    @TableField("remark")
    private String remark;

}
