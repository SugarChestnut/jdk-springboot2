package cn.rtt.server.system;

import cn.rtt.server.system.cahce.CacheService;
import cn.rtt.server.system.constant.CacheMetaEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CacheTest {

    @Autowired
    CacheService cacheService;

    @Test
    public void testCache() throws InterruptedException {
        cacheService.put(CacheMetaEnum.REPEAT_SUBMIT_KEY, 1, "ss");
        Thread.sleep(2000);
        Object o = cacheService.get(CacheMetaEnum.REPEAT_SUBMIT_KEY, 1);
        System.out.println(o);
        Thread.sleep(2000);
    }
}
