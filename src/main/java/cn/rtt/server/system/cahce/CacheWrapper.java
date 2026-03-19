package cn.rtt.server.system.cahce;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;

@Data
@AllArgsConstructor
public class CacheWrapper {

    private Duration duration;

    private Object data;

}
