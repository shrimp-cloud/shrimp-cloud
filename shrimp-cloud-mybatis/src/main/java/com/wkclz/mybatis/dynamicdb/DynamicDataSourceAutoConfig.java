package com.wkclz.mybatis.dynamicdb;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
@ConditionalOnBean({DynamicDataSourceFactory.class})
public class DynamicDataSourceAutoConfig {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAutoConfig.class);

    @Resource
    private DataSource dataSource;

    @Bean
    @Primary
    public DynamicDataSource dynamicDataSource() {
        logger.info("dynamicData Source, load default dataSource...");
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        /*
        此处不能再创建数据源，可以直接使用 Druid 的数据源，作为 DynamicDataSource 的默认数据源。
        即使没有 DynamicDataSourceFactory，也不会破坏项目的基本结构。
        Map<String, Object> map = MapUtil.obj2Map(defaultDataSourceConfig);
        DataSource dataSource = DruidDataSourceFactory.createDataSource(map);
        */
        dynamicDataSource.setDefaultTargetDataSource(dataSource);

        // 动态数据源，只放一个 Map, 后续在使用时动态添加
        dynamicDataSource.setTargetDataSources(new ConcurrentHashMap<>());
        dynamicDataSource.afterPropertiesSet();
        return dynamicDataSource;
    }
}
