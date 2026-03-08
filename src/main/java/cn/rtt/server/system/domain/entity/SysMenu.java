package cn.rtt.server.system.domain.entity;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_menu")
@ToString(callSuper = true)
public class SysMenu extends BaseEntity {

    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    @NotEmpty(message = "菜单标题不能为空")
    private String title;

    private Long parentId;
    private Integer orderNum;
    private String path;
    private String component;
    private String query;

    @NotEmpty(message = "路由名称不能为空")
    private String routeName;

    private Boolean isFrame;
    private Boolean isCache;
    private String menuType;
    private Boolean hidden;
    private Boolean status;
    private String permission;
    private String icon;

    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();

    /**
     *  禁用自己
     */
    @TableField(exist = false)
    private Integer notNextAllNodeId;

}
