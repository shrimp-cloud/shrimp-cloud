package com.wkclz.mybatis.dynamicdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ConditionalOnBean({DynamicDataSourceFactory.class})
public class DynamicDataSourceAutoConfig {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAutoConfig.class);

    @Bean
    public DataSource dynamicDataSource(DynamicDataSourceFactory dynamicDataSourceFactory) {
        logger.info("dynamicData Source, load default dataSource...");
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        DataSource defaultDataSource = dynamicDataSourceFactory.createDefaultDataSource();
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
        dynamicDataSource.setTargetDataSources(new ConcurrentHashMap<>());
        return dynamicDataSource;
    }
}
