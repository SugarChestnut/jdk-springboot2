package cn.rtt.server.system.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.TreeMap;

/**
 * CopyRight : <company domain>
 * Project :  cjj_mall_book
 * Comments : <对此类的描述，可以引用系统设计中的描述>
 * JDK version : JDK1.8
 * Create Date : 2025-05-20 09:28
 *
 * @author : xql
 */
public class Apiutils {

    public static Boolean compare(String token, TreeMap<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        map.forEach((k, v) -> {
            stringBuilder.append(k).append("=").append(v).append("&");
        });
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        String result = DigestUtils.md5DigestAsHex((stringBuilder.toString()).getBytes());
        return StringUtils.equals(token, StringUtils.reverse(result));
    }

    public static void main(String[] args) {
        TreeMap<String, Object> map = new TreeMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        map.put("companyName", "寻甸羊街平安小汽车修理厂");
        map.put("uscc", "92530129MA6KM9CX79");
        map.put("page",1);
        map.forEach((k, v) -> {
            stringBuilder.append(k).append("=").append(v).append("&");
        });
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        String s = DigestUtils.md5DigestAsHex((stringBuilder.toString()).getBytes());
        System.out.println(s);
        System.out.println(StringUtils.reverse(s));
    }
}
