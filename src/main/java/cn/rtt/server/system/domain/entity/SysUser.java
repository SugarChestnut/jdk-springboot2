package cn.rtt.server.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xql
 * @since 2024-11-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user")
@ToString(callSuper = true)
public class SysUser extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String password;
    /**
     * 性别0女1男
     */
    private Integer gender;
    /**
     * 头像地址
     */
    private String avatarUrl;
    /**
     * 手机
     */
    private String mobile;
    /**
     * 0正常1封禁2删除
     */
    private Integer status;

    private LocalDateTime loginTime;

    private String loginIp;

    @TableField(exist = false)
    @JsonIgnore
    private List<SysRole> roles;
}
