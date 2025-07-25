package com.wkclz.spring.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 解决 LocalDateTime 时间格式化的问题
 * @author shrimp
 */
@Configuration
public class CustomWebSerializer {

    private static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Value("${spring.jackson.date-format:}")
    private String dateFormat;

    @Bean
    @ConditionalOnProperty("spring.jackson.date-format")
    public Jackson2ObjectMapperBuilderCustomizer customizeLocalDateTimeFormat(){
        String df = StringUtils.isBlank(dateFormat) ? DEFAULT_TIME_FORMAT : dateFormat;
        return builder -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(df);
            // LocalDateTime 时间处理
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            // 字符串移除两端空格
            builder.deserializerByType(String.class, new TrimStringDeserializer());
            // JSON 格式化不缩进不换行
            builder.indentOutput(false);
        };
    }

    public static class TrimStringDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            return value == null ? null : value.trim();
        }
    }
}

