package com.wkclz.mybatis.plugins;

import com.wkclz.common.emuns.ResultStatus;
import com.wkclz.common.entity.BaseEntity;
import com.wkclz.common.exception.BizException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class UpdateInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(UpdateInterceptor.class);
    private final static String MDC_USER_NAME_KEY = "username";
    private final static String DEFAULT_USER_NAME = "guest";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        String username = MDC.get(MDC_USER_NAME_KEY);
        if (StringUtils.isBlank(username)) {
            username = DEFAULT_USER_NAME;
        }

        // 参数处理
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        String id = mappedStatement.getId();
        SqlCommandType commandType = MybatisConfiguration.getCommandType(id, sqlCommandType);

        // 需要检查乐观锁, 使用前要去除，保证线程安全
        boolean chechVersion = MDC.get(MybatisConfiguration.CHECK_VERSION) != null;
        boolean checkId = MDC.get(MybatisConfiguration.CHECK_ID) != null;

        // 参数为对象
        if (parameter instanceof BaseEntity) {
            checkEntity(parameter, commandType, username, chechVersion, checkId);
        }

        // 参数为 List 【在 Map 里面】
        if (parameter instanceof Map){
            Map parameterMap = (Map)parameter;
            Collection values = parameterMap.values();
            for (Object parameterObj : values) {
                if (parameterObj instanceof Collection){
                    Collection parameters = (Collection)parameterObj;
                    for (Object p:parameters) {
                        boolean isBaseEntity = checkEntity(p, commandType, username, chechVersion, checkId);
                        if (!isBaseEntity) {
                            break;
                        }
                    }
                }
            }
        }
        logger.debug("mybatis.update.interceptor: operate user: {}", username);

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

    private static boolean checkEntity(Object paramter, SqlCommandType commandType, String username, boolean chechVersion, boolean checkId){
        if (!(paramter instanceof BaseEntity)) {
            return false;
        }
        BaseEntity clearPatameter = (BaseEntity) paramter;
        // insert, upadte, delete 修改人/时间
        clearPatameter.setUpdateBy(username);
        clearPatameter.setStatus(1);
        if (clearPatameter.getUpdateTime() != null) {
            clearPatameter.setUpdateTime(new Date());
        }
        // insert 时需要附加创建人
        if (commandType == SqlCommandType.INSERT) {
            clearPatameter.setId(null);
            clearPatameter.setVersion(null);
            clearPatameter.setCreateBy(username);
            if (clearPatameter.getSort() == null){
                clearPatameter.setSort(0);
            }
        }
        // update 时 id 不能为空
        if (commandType == SqlCommandType.UPDATE) {
            if (checkId && clearPatameter.getId() == null) {
                throw BizException.error(ResultStatus.PARAM_NO_ID);
            }
            // 批量更新不处理 version
            if (chechVersion && clearPatameter.getVersion() == null) {
                throw BizException.error(ResultStatus.UPDATE_NO_VERSION);
            }
        }
        // delete 时 id, ids 不能同时为空, 删除不校验 version
        if (commandType == SqlCommandType.DELETE) {
            if (clearPatameter.getId() == null && CollectionUtils.isEmpty(clearPatameter.getIds())) {
                throw BizException.error(ResultStatus.PARAM_NO_ID);
            }
        }
        return true;
    }
}
