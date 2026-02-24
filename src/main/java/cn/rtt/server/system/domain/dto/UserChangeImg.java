package cn.rtt.server.system.domain.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserChangeImg {
    private Long userId;
    private MultipartFile file;

    private ImageParam imageParam;
    private Integer width;

    private Integer height;
}
