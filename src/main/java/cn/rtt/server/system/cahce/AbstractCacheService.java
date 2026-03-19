package cn.rtt.server.system.cahce;

import cn.rtt.server.system.constant.CacheConstants;

import java.time.Duration;

public abstract class AbstractCacheService implements CacheService{

    Duration getDuration(String key) {
        for (CacheConstants value : CacheConstants.values()) {
            if (key.startsWith(value.getPrefix())) return value.getDuration();
        }
        return Duration.ZERO;
    }
}
