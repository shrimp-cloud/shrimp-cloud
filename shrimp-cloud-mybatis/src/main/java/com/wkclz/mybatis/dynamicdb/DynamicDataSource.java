package com.wkclz.mybatis.dynamicdb;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.utils.MapUtil;
import com.wkclz.mybatis.bean.DataSourceInfo;
import com.wkclz.mybatis.config.DefaultDataSourceConfig;
import com.wkclz.mybatis.config.ShrimpMyBatisConfig;
import com.wkclz.spring.config.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

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
        if (latest != null && ((now - latest) < cacheTime * 1_000)) {
            return key;
        }

        synchronized (this) {
            latest = hasCreateDataSource.get(key);
            if (latest != null && ((now - latest) < cacheTime * 1_000)) {
                return key;
            }

            if (latest != null) {
                DataSource dataSource = getDataSource(key);
                if (dataSource instanceof DruidDataSource dds) {
                    dds.close();
                }
            }

            // 使用异步线程。否则使用默认数据源管理三方数据的场景下，会进入死循环
            CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(1);
            ThreadUtil.newExecutor().execute(() -> {
                // 若想用多数据源，必需注入此工厂
                DynamicDataSourceFactory dynamicDataSourceFactory = SpringContextHolder.getBean(DynamicDataSourceFactory.class);
                if (dynamicDataSourceFactory == null) {
                    throw BizException.error("please init dynamicDataSourceFactory before use dynamic dataSource");
                }

                // 只返回基础数据
                DataSourceInfo ds = dynamicDataSourceFactory.createDataSource(key);
                if (ds == null) {
                    throw BizException.error("can not find dataSource by key: {}", key);
                }

                // 使用当前数据库连接池参数，仅是换了地址，用户名，密码的方案
                DefaultDataSourceConfig config = new DefaultDataSourceConfig();
                config.setUrl(ds.getUrl());
                config.setUsername(ds.getUsername());
                config.setPassword(ds.getPassword());
                Map<String, Object> map = MapUtil.obj2Map(config);
                DataSource dataSource = null;
                try {
                    dataSource = DruidDataSourceFactory.createDataSource(map);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (dataSource == null) {
                    throw BizException.error("can not create dataSource by key: {}", key);
                }

                /*
                使用默认参数的方案
                DruidDataSource druidDataSource = DataSourceInfo.getDruidDataSource(ds);
                */

                addDataSource(key, dataSource);
                hasCreateDataSource.put(key, now);
                countDownLatch.countDown();
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return key;
        }
    }
}
