package com.wkclz.spring.aop;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wkclz.common.entity.Result;
import com.wkclz.spring.config.Sys;
import com.wkclz.spring.config.SystemConfig;
import com.wkclz.spring.enums.EnvType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * RestAop
 * wangkc @ 2019-07-28 23:56:25
 */
@Aspect
@Component
public class RestAop {

    private final static String GW_FILTER_LOG_KEY = "GW_FILTER_LOG";
    private final static AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private final static List<String> NO_LOGS = Arrays.asList("/public/status", "/sign/**");
    private final static Map<String, Boolean> LOGS_SET = new HashMap<>();

    /**
     * : @Around环绕通知
     * : @Before通知执行
     * : @Before通知执行结束
     * : @Around环绕通知执行结束
     * : @After后置通知执行了!
     * : @AfterReturning第一个后置返回通知的返回值：18
     */

    private static final Logger logger = LoggerFactory.getLogger(RestAop.class);
    private static ObjectMapper objectMapper;

    @Autowired
    private SystemConfig systemConfig;

    private final String POINT_CUT = "(" +
        "@within(org.springframework.stereotype.Controller) " +
        "|| @within(org.springframework.web.bind.annotation.RestController)" +
        ") && !execution(* org.springframework..*.*(..))";

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }


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
        return servletRequestHandle(point);
    }

    private static Object servletRequestHandle(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = requestAttributes.getRequest();
        // HttpServletResponse rep = requestAttributes.getResponse();

        String method = req.getMethod();
        String uri = req.getRequestURI();
        String ua = req.getHeader("User-Agent");
        Date requestTime = new Date();
        Date responseTime;
        Long costTime;

        // 请求具体方法
        Object obj = null;
        Throwable tb = null;

        /*
        if (!uri.startsWith("/swagger-") && !uri.startsWith("/v3/api-docs") && !uri.startsWith("/doc.html") && !uri.startsWith("/webjars/")) {
            // 非微服务场景，使用 module 标记模块
            Object module = req.getAttribute("module");

            if (!systemConfig.isCloud() && module == null) {
                throw BizException.error(ResultStatus.ERROR_ROUTER);
            }
            if (module != null) {
                String typeName = point.getSignature().getDeclaringTypeName();
                if (!typeName.contains("." + module + ".")) {
                    if (!typeName.startsWith("com.wkclz.core") && !typeName.startsWith("com.wkclz.starter")) {
                        throw BizException.error(ResultStatus.ERROR_ROUTER);
                    }
                }
            }
        }
        */

        try {
            obj = point.proceed();
        } catch (Throwable throwable) {
            tb = throwable;
        }

        // 返回参数处理
        responseTime = new Date();
        costTime = responseTime.getTime() - requestTime.getTime();
        if (obj instanceof Result && Sys.CURRENT_ENV != EnvType.PROD) {
            Result result = (Result) obj;
            result.setRequestTime(requestTime);
            result.setResponseTime(responseTime);
            result.setCostTime(costTime);
        }

        // 日志
        String debug = req.getParameter("debug");
        boolean isDebug = logger.isDebugEnabled() || ("1".equals(debug));

        String gwLog = MDC.get(GW_FILTER_LOG_KEY);

        String args = getArgs(point);
        if (isDebug) {
            String value = null;
            try {
                value = obj == null ? null : objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("{}ms|{}|{}|{}|{}|{}|{}", costTime, gwLog, method, uri, ua, args, value);
            } else {
                logger.info("{}ms|{}|{}|{}|{}|{}|{}", costTime, gwLog, method, uri, ua, args, value);
            }
        } else {
            if (isLog(uri)) {
                logger.info("{}ms|{}|{}|{}|{}|{}", costTime, gwLog, method, uri, ua, args);
            }
        }

        if (tb != null) {
            throw tb;
        }

        return obj;
    }

    private static String getArgs(ProceedingJoinPoint point) {
        String value = null;
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            List<Object> baseModelArgs = new ArrayList<>();
            for (Object arg : args) {
                if (arg instanceof HttpServletRequest) {
                    continue;
                }
                if (arg instanceof HttpServletResponse) {
                    continue;
                }
                if (arg instanceof MultipartFile) {
                    continue;
                }
                baseModelArgs.add(arg);
            }
            if (!baseModelArgs.isEmpty()) {
                try {
                    value = objectMapper.writeValueAsString(baseModelArgs);
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return value;
    }

    private static boolean isLog(String uri) {
        if (StringUtils.isBlank(uri)) {
            return false;
        }

        Boolean a = LOGS_SET.get(uri);
        if (a != null) {
            return a;
        }

        synchronized (uri.intern()) {
            a = LOGS_SET.get(uri);
            if (a != null) {
                return a;
            }

            for (String noLog : NO_LOGS) {
                boolean match = ANT_PATH_MATCHER.match(noLog, uri);
                if (match) {
                    LOGS_SET.put(uri, false);
                    return false;
                }
            }
            LOGS_SET.put(uri, true);
            return true;
        }

    }

}
