package com.wkclz.mybatis.dynamicdb;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.wkclz.common.utils.MapUtil;
import com.wkclz.mybatis.config.DefaultDataSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
@ConditionalOnBean({DynamicDataSourceFactory.class})
public class DynamicDataSourceAutoConfig {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAutoConfig.class);

    @Autowired
    private DefaultDataSourceConfig defaultDataSourceConfig;

    // 必需定义为 Primary， 以使得 com.alibaba.druid.spring.boot.autoconfigur.DruidDataSourceAutoConfigure.dataSource() 失效
    @Bean
    @Primary
    public DynamicDataSource dynamicDataSource() throws Exception {
        logger.info("dynamicData Source, load default dataSource...");
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        // 默认数据源
        Map<String, Object> map = MapUtil.obj2Map(defaultDataSourceConfig);
        DataSource dataSource = DruidDataSourceFactory.createDataSource(map);
        dynamicDataSource.setDefaultTargetDataSource(dataSource);

        // 动态数据源，只放一个 Map, 后续在使用时动态添加
        dynamicDataSource.setTargetDataSources(new ConcurrentHashMap<>());
        dynamicDataSource.afterPropertiesSet();
        return dynamicDataSource;
    }
}
