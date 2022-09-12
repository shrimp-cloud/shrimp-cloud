package com.wkclz.cas.sdk;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * sdk 模块给其他应用使用
 */
@Configuration
@ComponentScan(basePackages = {"com.wkclz.cas.sdk"})
public class SdkAutoConfig {
}
