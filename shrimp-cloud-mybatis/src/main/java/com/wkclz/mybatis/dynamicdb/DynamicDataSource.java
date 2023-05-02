package com.wkclz.mybatis.dynamicdb;

import com.wkclz.common.exception.BizException;
import com.wkclz.mybatis.config.ShrimpMyBatisConfig;
import com.wkclz.spring.config.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 重写 determineCurrentLookupKey() 方法来实现数据源切换功能
 * 若数据源不存在，需要到 DataSourceFactory 获取
 */
public class DynamicDataSource extends AbstractShrimpRoutingDataSource {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    // 已经初始化的，不再初始化了
    private Map<String, Long> hasCreateDataSource = new ConcurrentHashMap<>();

    protected Object determineCurrentLookupKey() {
        String key = DynamicDataSourceHolder.get();
        if (key == null) {
            return null;
        }
        logger.info("determineCurrentLookupKey: {}", key);

        // 存在，并在有效期内
        Long latest = hasCreateDataSource.get(key);
        long now = System.currentTimeMillis();
        ShrimpMyBatisConfig shrimpMyBatisConfig = SpringContextHolder.getBean(ShrimpMyBatisConfig.class);
        Integer cacheTime = shrimpMyBatisConfig.getDatasourceCacheSecond();
        if (latest != null && ((now - latest) < cacheTime * 60)) {
            return key;
        }

        synchronized (this) {
            latest = hasCreateDataSource.get(key);
            if (latest != null) {
                return key;
            }
            // 若想用多数据源，必需注入此工厂
            DynamicDataSourceFactory dynamicDataSourceFactory = SpringContextHolder.getBean(DynamicDataSourceFactory.class);
            if (dynamicDataSourceFactory == null) {
                throw BizException.error("please init dynamicDataSourceFactory before use dynamic dataSource");
            }
            DataSource dataSource = dynamicDataSourceFactory.createDataSource(key);
            addDataSource(key, dataSource);
            hasCreateDataSource.put(key, now);
            return key;
        }
    }
}
