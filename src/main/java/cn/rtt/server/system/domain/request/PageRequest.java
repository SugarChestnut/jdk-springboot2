package cn.rtt.server.system.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class PageRequest {
    @NotNull(message = "页号不能为空")
    @Min(value = 1, message = "页号不能小于1")
    private Integer pageNum;
    @NotNull(message = "页大小不能为空")
    @Min(value = 1, message = "页大小不能小于1")
    @Max(value = 100, message = "页大小不能大于100")
    private Integer pageSize;
}
