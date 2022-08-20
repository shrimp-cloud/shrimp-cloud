package com.wkclz.auth;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 在SpringBootApplication上使用@ServletComponentScan注解后，
 * Servlet、Filter、Listener可以直接通过@WebServlet、@WebFilter、@WebListener注解自动注册，
 * 无需其他代码。
 */
@Configuration
@ComponentScan(basePackages = {"com.wkclz.auth"})
@ServletComponentScan(basePackages = {"com.wkclz.auth"})
public class ShrimpAuthAutoConfig {
}
