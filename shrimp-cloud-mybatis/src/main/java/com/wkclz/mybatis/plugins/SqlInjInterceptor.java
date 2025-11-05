package com.wkclz.mybatis.plugins;

import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.violation.SyntaxErrorViolation;
import com.wkclz.common.exception.SysException;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

/**
 * @author shrimp
 */
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class SqlInjInterceptor implements Interceptor {

    private static WallProvider provider = null;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        BoundSql boundSql;
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        if (args.length == 4) {
            boundSql = ms.getBoundSql(parameter);
        } else {
            boundSql = (BoundSql) args[5];
        }
        sqlInjCheck(boundSql.getSql());

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private void sqlInjCheck(String sql) {
        if (provider == null) {
            provider = new MySqlWallProvider(new WallConfig("META-INF/druid/wall/mysql"));
            provider.getConfig().setCommentAllow(true);
        }

        WallCheckResult checkResult = provider.check(sql);
        List<Violation> violations = checkResult.getViolations();
        if (violations.isEmpty()) {
            return;
        }
        Violation firstViolation = violations.get(0);

        // 指定类型错误
        if (firstViolation instanceof SyntaxErrorViolation violation) {
            throw SysException.error("sql injection violation: " + violation.getMessage());
        }

        // 兜底的错误
        throw SysException.error("sql injection violation: " + firstViolation.getMessage());
    }

}
