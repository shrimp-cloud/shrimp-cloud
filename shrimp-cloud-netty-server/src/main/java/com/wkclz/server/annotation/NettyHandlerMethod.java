package com.wkclz.server.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * netty数据处理方法注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface NettyHandlerMethod {

    /**
     * 输入数据转换，参照ConvertUtils类方法配置
     * @return
     */
    String inputConvert() default "";

    /**
     * 输出数据转换，参照ConvertUtils类方法配置
     * @return
     */
    String outputConvert() default "";

}
