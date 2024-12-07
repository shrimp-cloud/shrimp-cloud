package com.wkclz.spring.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 解决 LocalDateTime 时间格式化的问题
 */
@Configuration
@ConditionalOnClass(JavaTimeModule.class)
public class LocalDateTimeSerializer {

    @Value("${spring.jackson.date-format}")
    private String dateFormat;

    @Bean
    @ConditionalOnProperty("spring.jackson.date-format")
    public Jackson2ObjectMapperBuilderCustomizer customizeLocalDateTimeFormat(){
        return builder -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            builder.serializerByType(LocalDateTime.class, new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer(formatter));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        };
    }
}

