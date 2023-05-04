package com.wkclz.mybatis.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ShrimpMyBatisConfig {

  @Value("${shrimp.mybatis.datasource.cache-second:60}")
  private Integer datasourceCacheSecond;

}
