package cn.rtt.server.system.utils;


import cn.rtt.server.system.constant.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.regex.Pattern;

public class StringProUtils {

    // 简单正则：1开头，11位数字
    private static final Pattern SIMPLE_PATTERN = Pattern.compile("^1\\d{10}$");

    // 严格正则：验证具体号段
    private static final Pattern STRICT_PATTERN = Pattern.compile(
            "^1(3[0-9]|4[0-9]|5[0-9]|6[0-9]|7[0-9]|8[0-9]|9[0-9])\\d{8}$"
    );

    /**
     * 是否为手机号
     */
    public static Boolean isMobile(String phone) {
        return StringUtils.isNotBlank(phone) && SIMPLE_PATTERN.matcher(phone).matches();
    }

    /**
     * 是否为http(s)://开头
     */
    public static boolean isHttp(String link) {
        return Strings.CS.startsWithAny(link, Constants.HTTP, Constants.HTTPS);
    }
}
