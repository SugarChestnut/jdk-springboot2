package cn.rtt.server.system.domain.response;

import cn.rtt.server.system.domain.entity.SysMenu;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TreeSelect树结构实体类
 *
 * @author ruoyi
 */
@Data
public class TreeMenuSelect implements Serializable {
    private static final long serialVersionUID = 1L;

    private SysMenu sysMenu;


    private String label;
    private Long menuId;

    private Map<String, Object> meta = new HashMap<>();

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeMenuSelect> children;

    public TreeMenuSelect() {

    }

    public TreeMenuSelect(SysMenu menu) {
        this.children = menu.getChildren().stream().map(TreeMenuSelect::new).collect(Collectors.toList());
        this.sysMenu = menu;
        this.label = menu.getTitle();
        this.menuId = menu.getMenuId();
        meta.put("title", menu.getTitle());
        meta.put("icon", menu.getIcon());
        meta.put("isIframe", menu.getIsFrame());
        meta.put("isLink", "");
        meta.put("role", List.of("admin", "nor_user", "common"));
    }


}
