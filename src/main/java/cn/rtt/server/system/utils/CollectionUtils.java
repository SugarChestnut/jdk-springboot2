package cn.rtt.server.system.utils;

import java.util.Collection;

/**
 * @author rtt
 * @date 2025/11/24 13:20
 */
public class CollectionUtils {

    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
