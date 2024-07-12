package com.wkclz.mybatis.plugins;

import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.violation.SyntaxErrorViolation;
import com.wkclz.common.exception.BizException;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class SqlInjInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(SqlInjInterceptor.class);

    private static final Pattern PATTERN = Pattern.compile("[\t\r\n]");
    private static final WallProvider PROVIDER = new MySqlWallProvider(new WallConfig("META-INF/druid/wall/mysql"));

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

        WallCheckResult checkResult = PROVIDER.check(sql);
        List<Violation> violations = checkResult.getViolations();
        if (violations.isEmpty()) {
            return;
        }
        Violation firstViolation = violations.get(0);

        // 指定类型错误
        if (firstViolation instanceof SyntaxErrorViolation) {
            SyntaxErrorViolation violation = (SyntaxErrorViolation) firstViolation;
            throw new BizException("sql injection violation: " + violation.getMessage());
        }

        // 兜底的错误
        throw new BizException("sql injection violation: " + firstViolation.getMessage());
    }

}
