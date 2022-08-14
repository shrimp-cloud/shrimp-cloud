package com.wkclz.swagger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${shrimp.cloud.swagger.base-package:com.wkclz}")
    private String basePackage;
    @Value("${shrimp.cloud.swagger.title:无标题}")
    private String title;
    @Value("${shrimp.cloud.swagger.description:无项目说明}")
    private String description;
    @Value("${shrimp.cloud.swagger.service-url:https://api.example.com}")
    private String serviceUrl;
    @Value("${shrimp.cloud.swagger.version:1.0.0}")
    private String version;


    @Bean
    public Docket createRestApi() {
        // DocumentationType.SWAGGER_2 固定的，代表swagger2
        return new Docket(DocumentationType.SWAGGER_2)
            // 如果配置多个文档的时候，那么需要配置groupName来分组标识
            // .groupName("分布式任务系统")
            // 用于生成API信息
            .apiInfo(apiInfo())
            // select()函数返回一个ApiSelectorBuilder实例,用来控制接口被swagger做成文档
            .select()
            // 用于指定扫描哪个包下的接口
            .apis(RequestHandlerSelectors.basePackage(basePackage))
            // 选择所有的API,如果你想只为部分API生成文档，可以配置这里
            .paths(PathSelectors.any())
            .build();
    }

    /**
     * 用于定义API主界面的信息，比如可以声明所有的API的总标题、描述、版本
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            //  可以用来自定义API的主标题
            .title(title)
            // 可以用来描述整体的API
            .description(description)
            // 用于定义服务的域名
            .termsOfServiceUrl(serviceUrl)
            // 可以用来定义版本。
            .version(version)
            .build();
    }
}
