package com.wkclz.common.annotation;

import java.lang.annotation.*;

/**
 * Created by wangkc on 2018/06/07.
 * 标识路由，提取注释
 */
@Documented
@Inherited
@Target({ElementType.TYPE})//作用域是类或者接口,或者方法 // 不限制使用位置
@Retention(RetentionPolicy.RUNTIME)//注解类型：VM将在运行期间保留注解，因此可以通过反射机制读取注解的信息
public @interface Router {

    //注解只有一个变量时 变量名必须为value
    String value();

}
