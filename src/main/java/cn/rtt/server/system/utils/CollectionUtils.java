package cn.rtt.server.system.utils;

import java.util.Collection;
import java.util.Objects;

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

    public static <T> boolean equals(Collection<T> c1, Collection<T> c2) {
        if (isEmpty(c1)) return isEmpty(c2);
        if (isEmpty(c2)) return isEmpty(c1);
        if (c1.size() != c2.size()) return false;
        for (Object o1 : c1) {
            boolean find = false;
            for (Object o2 : c2) {
                if (Objects.equals(o1, o2)) {
                    find = true;
                    break;
                }
            }
            if (!find) return false;
        }
        return true;
    }
}
