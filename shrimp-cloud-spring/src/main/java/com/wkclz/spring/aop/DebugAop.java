package com.wkclz.spring.aop;

import com.wkclz.common.annotation.Debug;
import com.wkclz.spring.helper.DebugHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * FeignAop
 * wangkc @ 2019-08-25 20:33:55
 */
@Aspect
@Component
public class DebugAop {

    /**
     * : @Around环绕通知
     * : @Before通知执行
     * : @Before通知执行结束
     * : @Around环绕通知执行结束
     * : @After后置通知执行了!
     * : @AfterReturning第一个后置返回通知的返回值：18
     */

    private static final Logger logger = LoggerFactory.getLogger(DebugAop.class);
    private final String POINT_CUT = "@annotation(com.wkclz.core.base.annotation.Debug)";


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

        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        String debugInfo = null;
        for (Annotation declaredAnnotation : declaredAnnotations) {
            if (declaredAnnotation.annotationType() == Debug.class){
                Debug debug = (Debug)declaredAnnotation;
                debugInfo = debug.value();
                break;
            }
        }
        if (debugInfo == null || "".equals(debugInfo.trim())){
            debugInfo = method.getDeclaringClass().getName() + "." + method.getName();
        }

        Method debugStart = DebugHelper.class.getDeclaredMethod("debugStart", String.class);
        Method debugEnd = DebugHelper.class.getDeclaredMethod("debugEnd", null);
        debugStart.setAccessible(true);
        debugEnd.setAccessible(true);
        debugStart.invoke(null,debugInfo);

        Object obj;
        try {
            obj = point.proceed();
        } catch (Throwable throwable) {
            debugEnd.invoke(null, null);
            throw throwable;
        }
        debugEnd.invoke(null, null);
        return obj;
    }

}
