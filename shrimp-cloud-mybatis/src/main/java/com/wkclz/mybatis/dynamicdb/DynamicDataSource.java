package com.wkclz.mybatis.dynamicdb;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.wkclz.common.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.Set;

/**
 * 重写 determineCurrentLookupKey() 方法来实现数据源切换功能
 * 若数据源不存在，需要到 DataSourceFactory 获取
 */
public class DynamicDataSource extends AbstractShrimpRoutingDataSource {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    // 已经初始化的，不再初始化了
    private Set<String> hasCreateDataSource = new ConcurrentHashSet();

    @Autowired(required = false)
    private DynamicDataSourceFactory dynamicDataSourceFactory;

    protected Object determineCurrentLookupKey() {
        String key = DynamicDataSourceHolder.get();
        if (key == null) {
            return null;
        }
        logger.info("determineCurrentLookupKey: {}", key);
        if (hasCreateDataSource.contains(key)) {
            return key;
        }
        synchronized (this) {
            if (hasCreateDataSource.contains(key)) {
                return key;
            }
            // 若想用多数据源，必需注入此工厂
            if (dynamicDataSourceFactory == null) {
                throw BizException.error("please init dynamicDataSourceFactory before use dynamic dataSource");
            }
            DataSource dataSource = dynamicDataSourceFactory.createDataSource(key);
            addDataSource(key, dataSource);
            hasCreateDataSource.add(key);
            return key;
        }
    }
}
