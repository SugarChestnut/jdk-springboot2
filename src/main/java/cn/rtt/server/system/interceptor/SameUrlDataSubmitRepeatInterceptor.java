package cn.rtt.server.system.interceptor;


import cn.rtt.server.system.annotation.RepeatSubmit;
import cn.rtt.server.system.cahce.CacheService;
import cn.rtt.server.system.config.property.SystemAuthProperties;
import cn.rtt.server.system.constant.CacheMetaEnum;
import cn.rtt.server.system.utils.ServletUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 判断请求url和数据是否和上一次相同，
 * 如果和上次相同，则是重复提交表单。 有效时间为10秒内。
 *
 * @author ruoyi
 */
@Component
@AllArgsConstructor
public class SameUrlDataSubmitRepeatInterceptor extends RepeatInterceptor {

    public static final String REPEAT_PARAMS = "repeatParams";

    public static final String REPEAT_TIME = "repeatTime";

    private final SystemAuthProperties systemConfig;

    private final CacheService cacheService;

    private final ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    @Override
    public boolean isRepeat(HttpServletRequest request, RepeatSubmit annotation) throws JsonProcessingException {

        String data = "";
        if (request instanceof RepeatableReadRequestWrapper) {
            RepeatableReadRequestWrapper repeatedlyRequest = (RepeatableReadRequestWrapper) request;
            data = ServletUtils.getBodyString(repeatedlyRequest);
        }

        // body参数为空，获取Parameter的数据
        if (StringUtils.isEmpty(data)) {
            data = objectMapper.writeValueAsString(request.getParameterMap());
        }
        Map<String, Object> nowDataMap = new HashMap<>();
        nowDataMap.put(REPEAT_PARAMS, data);
        nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());

        // 请求地址（作为存放cache的key值）
        String url = request.getRequestURI();

        // 唯一值（没有消息头则使用请求地址）
        String submitKey = StringUtils.trimToEmpty(request.getHeader(systemConfig.getJwt().getHeader()));

        // 唯一标识（指定key + url + 消息头）
        String cacheRepeatKey = url + submitKey;

        Object sessionObj = cacheService.get(CacheMetaEnum.REPEAT_SUBMIT_KEY, cacheRepeatKey);
        if (sessionObj != null) {
            Map<String, Object> sessionMap = (Map<String, Object>) sessionObj;
            if (sessionMap.containsKey(url)) {
                Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(url);
                if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap, annotation.interval())) {
                    return true;
                }
            }
        }
        Map<String, Object> cacheMap = new HashMap<>();
        cacheMap.put(url, nowDataMap);
        cacheService.put(CacheMetaEnum.REPEAT_SUBMIT_KEY, cacheRepeatKey, cacheMap);
        return false;
    }

    /**
     * 判断参数是否相同
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS);
        String preParams = (String) preMap.get(REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 判断两次间隔时间
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, int interval) {
        long time1 = (Long) nowMap.get(REPEAT_TIME);
        long time2 = (Long) preMap.get(REPEAT_TIME);
        return (time1 - time2) < interval;
    }
}
