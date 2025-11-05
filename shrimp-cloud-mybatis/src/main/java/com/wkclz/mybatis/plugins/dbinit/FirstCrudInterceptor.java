package com.wkclz.mybatis.plugins.dbinit;

import com.wkclz.spring.config.SpringContextHolder;
import jakarta.annotation.Resource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author shrimp
 */

@Component
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class FirstCrudInterceptor implements Interceptor {

    private static final AtomicBoolean FIRST_CRUD_EXECUTED = new AtomicBoolean(true);
    private static final ThreadLocal<Boolean> SELF_EXECUTED = new ThreadLocal<>();

    // 没啥用，只为保证初始化顺序
    @Resource
    private SpringContextHolder springContextHolder;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 非首次执行，以及自身的 mapper 调用，不能再进入 initProcessor 逻辑
        if (FIRST_CRUD_EXECUTED.get() && SELF_EXECUTED.get() == null) {
            doOnFirstCrud();
            FIRST_CRUD_EXECUTED.set(false);
        }
        return invocation.proceed();
    }

    private synchronized void doOnFirstCrud() {
        // 防止重复执行
        if (!FIRST_CRUD_EXECUTED.get()) {
            return;
        }
        // 防止自身发起的 CRUD 再次经过后续逻辑
        SELF_EXECUTED.set(true);

        DatabaseInitProcessor initProcessor = SpringContextHolder.getBean(DatabaseInitProcessor.class);
        initProcessor.dbinit();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可选
    }
}