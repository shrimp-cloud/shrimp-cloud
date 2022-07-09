package com.wkclz.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wangkc on 2018/06/07.
 * 辅助java提取类详情
 */
@Documented
@Inherited
// @Target({ElementType.TYPE, ElementType.METHOD})//作用域是类或者接口,或者方法 // 不限制使用位置
@Retention(RetentionPolicy.RUNTIME)//注解类型：VM将在运行期间保留注解，因此可以通过反射机制读取注解的信息
public @interface Desc {

    //注解只有一个变量时 变量名必须为value
    String value();

}
