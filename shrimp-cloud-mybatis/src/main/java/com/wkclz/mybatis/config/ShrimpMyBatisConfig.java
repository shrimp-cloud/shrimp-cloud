package com.wkclz.mybatis.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ShrimpMyBatisConfig {

  @Value("${shrimp.mybatis.datasource.cache-second:60}")
  private Integer datasourceCacheSecond;

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    public String getTableSchema() {
        if (StringUtils.isBlank(datasourceUrl)) {
            return null;
        }
        String schema = datasourceUrl;
        int i = schema.indexOf("//");
        if (i > -1) {
            schema = schema.substring(i+2);
        }
        i = schema.indexOf("/");
        if (i > -1) {
            schema = schema.substring(i+1);
        }
        i = schema.indexOf("?");
        if (i > -1) {
            schema = schema.substring(0, i);
        }
        return schema;
    }


}
