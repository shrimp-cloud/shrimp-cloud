package com.wkclz.mybatis.aop;


import com.wkclz.common.entity.BaseEntity;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.utils.BeanUtil;
import com.wkclz.common.utils.StringUtil;
import com.wkclz.mybatis.base.BaseMapper;
import com.wkclz.mybatis.dynamicdb.DynamicDataSourceHolder;
import com.wkclz.mybatis.util.JdbcUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * RestAop
 * wangkc @ 2019-07-28 23:56:25
 */
@Aspect
@Component
public class DaoAop {

    @Autowired(required = false)
    private SqlSession sqlSession;
    private static Map<String, SqlCommandType> MAPPED_STATEMENTS = null;

    /**
     * : @Around环绕通知
     * : @Before通知执行
     * : @Before通知执行结束
     * : @Around环绕通知执行结束
     * : @After后置通知执行了!
     * : @AfterReturning第一个后置返回通知的返回值：18
     */

    private static final Logger logger = LoggerFactory.getLogger(DaoAop.class);
    private final String POINT_CUT = "@within(org.apache.ibatis.annotations.Mapper)";

    @Pointcut(POINT_CUT)
    public void pointCut() {
    }

    /**
     * 环绕通知：
     * 注意:Spring AOP的环绕通知会影响到AfterThrowing通知的运行,不要同时使用
     * <p>
     * 环绕通知非常强大，可以决定目标方法是否执行，什么时候执行，执行时是否需要替换方法参数，执行完毕是否需要替换返回值。
     * 环绕通知第一个参数必须是org.aspectj.lang.ProceedingJoinPoint类型
     */
    @Around(value = POINT_CUT)
    public Object doAroundAdvice(ProceedingJoinPoint point) throws Throwable {

        SqlCommandType sqlCommandType = getSqlCommandType(point);

        if (SqlCommandType.SELECT == sqlCommandType ){
            Object[] args = point.getArgs();
            if (args != null){
                for (Object arg : args) {
                    check(arg);
                }
            }
        }

        // 请求具体方法
        try {
            Object obj = point.proceed();
            return obj;
        } finally {
            DynamicDataSourceHolder.clear();
        }
    }

    private static void check(Object arg){
        if (arg == null){
            return;
        }
        if (!(arg instanceof BaseEntity)){
            return;
        }

        BaseEntity entity = (BaseEntity) arg;

        BeanUtil.removeBlank(entity);
        String orderBy = entity.getOrderBy();
        // 注入风险检测
        if (orderBy != null && !orderBy.equals(BaseEntity.DEFAULE_ORDER_BY) && JdbcUtil.sqlInj(orderBy)) {
            throw BizException.error("orderBy 有注入风险，请谨慎操作！");
        }

        if (StringUtils.isBlank(orderBy)){
            orderBy = BaseEntity.DEFAULE_ORDER_BY;
        }
        // 大小写处理
        orderBy = StringUtil.check2LowerCase(orderBy, "DESC");
        orderBy = StringUtil.check2LowerCase(orderBy, "ASC");
        // 驼峰处理
        orderBy = StringUtil.camelToUnderline(orderBy);

        entity.setOrderBy(orderBy);
        // keyword 查询处理
        if (StringUtils.isNotBlank(entity.getKeyword())) {
            entity.setKeyword("%" + entity.getKeyword() + "%");
        }
    }


    private SqlCommandType getSqlCommandType(ProceedingJoinPoint point){
        Signature signature = point.getSignature();
        String declaringTypeName = signature.getDeclaringTypeName();
        String name = signature.getName();
        String pointId = declaringTypeName + "." + name;
        if (MAPPED_STATEMENTS == null){
            MAPPED_STATEMENTS = new HashMap<>();

            // 非父类
            Configuration configuration = sqlSession.getConfiguration();
            Collection mappedStatements = configuration.getMappedStatements();
            for (Object mappedStatementObj : mappedStatements) {
                if (mappedStatementObj instanceof  MappedStatement){
                    MappedStatement mappedStatement = (MappedStatement)mappedStatementObj;
                    String id = mappedStatement.getId();
                    SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
                    MAPPED_STATEMENTS.put(id, sqlCommandType);
                }
            }

            // 父类【在父类里面，MappedStatement 是 Configuration 的私类的私类，无法直接获取，只能用另外的方式】
            Class<BaseMapper> baseMapperClass = BaseMapper.class;
            Method[] methods = baseMapperClass.getDeclaredMethods();
            String clazzName = baseMapperClass.getName() + ".";
            for (Method method : methods) {
                String methodName = method.getName();
                if ("insert".equals(methodName) || "insertBatch".equals(methodName)) {
                    MAPPED_STATEMENTS.put(clazzName + methodName, SqlCommandType.INSERT);
                    continue;
                }
                if ("updateAll".equals(methodName) || "updateSelective".equals(methodName)) {
                    MAPPED_STATEMENTS.put(clazzName + methodName, SqlCommandType.UPDATE);
                    continue;
                }
                if ("updateBatch".equals(methodName)) {
                    MAPPED_STATEMENTS.put(clazzName + methodName, SqlCommandType.UPDATE);
                    continue;
                }
                if ("delete".equals(methodName)) {
                    MAPPED_STATEMENTS.put(clazzName + methodName, SqlCommandType.UPDATE);
                    continue;
                }
                MAPPED_STATEMENTS.put(clazzName + methodName, SqlCommandType.SELECT);
            }

        }
        SqlCommandType sqlCommandType = MAPPED_STATEMENTS.get(pointId);
        if (sqlCommandType != null){
            return sqlCommandType;
        }

        throw BizException.error("unknown dao operation: {}, please check the config: mybatis.mapper-locations", pointId);
    }


}
