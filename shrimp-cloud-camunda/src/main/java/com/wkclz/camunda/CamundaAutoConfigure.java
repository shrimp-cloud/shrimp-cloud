package com.wkclz.camunda;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@EnableFeignClients
@ComponentScan(basePackages = {"com.wkclz.camunda"})
public class CamundaAutoConfigure {
}


