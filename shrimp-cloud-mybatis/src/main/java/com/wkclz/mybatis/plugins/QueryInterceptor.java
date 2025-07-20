package com.wkclz.mybatis.plugins;

import com.wkclz.common.entity.BaseEntity;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.utils.BeanUtil;
import com.wkclz.common.utils.StringUtil;
import com.wkclz.mybatis.util.JdbcUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

// 已在 MybatisConfiguration 配置不拦截,拦截了也没法在预编译前
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class QueryInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(QueryInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();
        Object parameter = args[1];

        // 参数为对象
        if (parameter instanceof BaseEntity baseEntity) {
            checkEntity(baseEntity);
        }

        // 参数为 List 【在 Map 里面】
        if (parameter instanceof Map map) {
            Collection values = map.values();
            for (Object parameterObj : values) {
                if (parameterObj instanceof Collection collection) {
                    for (Object p : collection) {
                        boolean isBaseEntity = checkEntity(p);
                        if (!isBaseEntity) {
                            break;
                        }
                    }
                }
            }
        }

        long startTimeMillis = System.currentTimeMillis();
        Object proceedReslut = invocation.proceed();
        long endTimeMillis = System.currentTimeMillis();
        logger.info("<< ==== sql execute runnung time：{} millisecond ==== >>", (endTimeMillis - startTimeMillis));
        return proceedReslut;
    }

    @Override
    public Object plugin(Object target) {
        // 返回代理类
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private static boolean checkEntity(Object paramter) {
        if (!(paramter instanceof BaseEntity entity)) {
            return false;
        }
        entity.init();

        BeanUtil.removeBlank(entity);
        String orderBy = entity.getOrderBy();
        // 注入风险检测
        if (orderBy != null && !orderBy.equals(BaseEntity.DEFAULE_ORDER_BY) && JdbcUtil.sqlInj(orderBy)) {
            throw BizException.error("orderBy 有注入风险，请谨慎操作！");
        }

        // 大小写处理
        entity.setOrderBy(StringUtil.check2LowerCase(orderBy, "DESC"));
        entity.setOrderBy(StringUtil.check2LowerCase(orderBy, "ASC"));
        // 驼峰处理
        entity.setOrderBy(StringUtil.camelToUnderline(orderBy));
        // keyword 查询处理
        if (StringUtils.isNotBlank(entity.getKeyword())) {
            entity.setKeyword("%" + entity.getKeyword() + "%");
        }
        return true;
    }

}
