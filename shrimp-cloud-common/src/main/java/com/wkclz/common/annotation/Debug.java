package com.wkclz.common.annotation;

import java.lang.annotation.*;

/**
 * Created by wangkc on 2018/06/07.
 * 辅助java提取类详情
 */
@Documented
@Inherited
@Target({ElementType.METHOD})//作用域是类或者接口,或者方法 // 不限制使用位置
@Retention(RetentionPolicy.RUNTIME)
public @interface Debug {

    String value() default "";

}
