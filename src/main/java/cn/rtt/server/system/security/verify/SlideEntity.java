package cn.rtt.server.system.security.verify;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author rtt
 * @date 2025/12/25 14:08
 */
@Data
public class SlideEntity {

    private String uuid;

    private String bgImg;

    private String puzzleImg;

    private int bgWidth;

    private int bgHeight;

    private int puzzleWidth;

    private int puzzleHeight;

    @JsonIgnore
    private int offset;
}
