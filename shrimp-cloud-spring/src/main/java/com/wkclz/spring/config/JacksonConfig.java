//package com.wkclz.spring.config;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.databind.ser.std.DateSerializer;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//
///**
// * @author shrimp
// */
//@Configuration
//public class JacksonConfig {
//
//    private static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
//    @Value("${spring.jackson.date-format:}")
//    private String dateFormat;
//
//    @Bean
//    public ObjectMapper objectMapper() {
//        String df = StringUtils.isBlank(dateFormat) ? DEFAULT_TIME_FORMAT : dateFormat;
//        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern(df);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setDateFormat(new SimpleDateFormat(df));
//        mapper.setTimeZone(java.util.TimeZone.getDefault());
//
//        // JSON 格式化不缩进不换行
//        mapper.disable(SerializationFeature.INDENT_OUTPUT);
//        // 忽略值为 null 的字段
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//        SimpleModule module = new SimpleModule();
//        // 设置 LocalDateTime 的序列化和反序列化格式
//        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
//        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
//
//        // 设置 Date 的序列化和反序列化格式
//        module.addDeserializer(Date.class, new DateDeserializers.DateDeserializer());
//        module.addSerializer(Date.class, new DateSerializer());
//
//        // 字符串移除两端空格
//        module.addDeserializer(String.class, new TrimStringDeserializer());
//        mapper.registerModule(module);
//
//        // 注册 JavaTimeModule 模块以支持 java.time 包中的类型 【只注册一个】
//        // mapper.registerModule(new JavaTimeModule());
//
//        // 设置日期格式为 ISO 8601 标准
//        // Jackson 会扫描你的应用程序的类路径，寻找所有继承自 com.fasterxml.jackson.databind.Module 的类，并将它们注册到 ObjectMapper 中。这通常包括对 Java 8 时间 API (java.time) 的支持模块 (JavaTimeModule)、Joda-Time 模块等
//        // mapper.findAndRegisterModules();
//
//        return mapper;
//    }
//
//    public static class TrimStringDeserializer extends JsonDeserializer<String> {
//        @Override
//        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//            String value = p.getValueAsString();
//            return value == null ? null : value.trim();
//        }
//    }
//}