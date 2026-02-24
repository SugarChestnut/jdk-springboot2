package cn.rtt.server.system.domain.dto;

import lombok.Data;

/**
 * CopyRight : <company domain>
 * Project :  zcbf
 * Comments : <对此类的描述，可以引用系统设计中的描述>
 * JDK version : JDK1.8
 * Create Date : 2024-07-26 09:20
 *
 * @author : xql
 */
@Data
public class ImageParam {

    private Integer width;

    private Integer height;

    private Double quality=0.2D;

    public ImageParam() {
    }

    public ImageParam(Integer width, Integer height) {
        this.width=width;
        this.height=height;
    }
}
