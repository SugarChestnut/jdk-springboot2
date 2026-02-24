package cn.rtt.server.system.controller;

import cn.rtt.server.system.cahce.CacheService;
import cn.rtt.server.system.domain.response.Result;
import cn.rtt.server.system.exception.SystemException;
import cn.rtt.server.system.security.verify.SlideEntity;
import cn.rtt.server.system.security.verify.SlideVerifyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author rtt
 * @date 2025/12/25 13:24
 */
@RestController
@RequestMapping("/captcha")
@AllArgsConstructor
public class CaptchaController {

    private final SlideVerifyService slideVerifyService;

    private final CacheService cacheService;

    @RequestMapping("/slide")
    public Result<SlideEntity> getSlideImg() {
        try {
            SlideEntity entity = slideVerifyService.imageResult();
            System.out.println(entity.getOffset());
            String uuid = UUID.randomUUID().toString();
            entity.setUuid(uuid);
            return Result.success(entity);
        } catch (Exception e) {
            throw new SystemException("滑动验证码生成失败");
        }
    }
}
