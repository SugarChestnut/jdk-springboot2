package cn.rtt.server.system.config;

import org.apache.ibatis.type.LocalDateTimeTypeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author rtt
 * @date 2025/11/20 14:52
 */
@Configuration
public class MybatisConfig {

    @Bean
    public LocalDateTimeTypeHandler localDateTimeTypeHandler() {
        return new LocalDateTimeTypeHandler() {
            @Override
            public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
                Object object = rs.getObject(columnName);
                if (object instanceof Timestamp) {//在这里强行转换，将sql的时间转换为LocalDateTime
                    return LocalDateTime//可以根据自己的需要进行转化
                            .ofInstant(((Timestamp) object).toInstant(), ZoneOffset.ofHours(8));
                }
                return super.getResult(rs, columnName);
            }
        };
    }
}
