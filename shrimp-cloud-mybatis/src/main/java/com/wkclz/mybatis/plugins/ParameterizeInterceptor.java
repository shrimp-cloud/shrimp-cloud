package com.wkclz.mybatis.plugins;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "parameterize", args = Statement.class)})
public class ParameterizeInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(ParameterizeInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        logger.info("mybatis.parameterize.interceptor");

        Object[] args = invocation.getArgs();
        Object target = invocation.getTarget();
        Method method = invocation.getMethod();
        Object proceedReslut = invocation.proceed();

        return proceedReslut;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
