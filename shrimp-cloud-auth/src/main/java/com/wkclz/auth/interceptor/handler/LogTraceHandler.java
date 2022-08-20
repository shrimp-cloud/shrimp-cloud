package com.wkclz.auth.interceptor.handler;

import com.wkclz.auth.helper.LogTraceHelper;
import com.wkclz.common.entity.Result;
import com.wkclz.common.exception.BizException;
import com.wkclz.spring.helper.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 日志拦截，已经在网关实现，此处不再需要
 * Created: wangkaicun @ 2017-10-18 下午11:41
 */
// @Component
public class LogTraceHandler implements HandlerInterceptor {

    @Autowired
    private LogTraceHelper logTraceHelper;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rep, Object handler) {
        try {
            logTraceHelper.checkTraceInfo(req, rep);
        } catch (Exception e){
            Result error;
            BizException bizException = getBizException(e);
            if (bizException != null){
                error = Result.error(bizException);
            } else {
                error = Result.error(e.getMessage());
            }
            ResponseHelper.responseError(rep, error);
            return false;
        }
        return true;
    }


    /**
     * 获取真实的原因
     * @param exception
     * @return
     */
    private static BizException getBizException(Exception exception){
        if (exception == null){
            return null;
        }
        if (exception instanceof BizException){
            return (BizException) exception;
        }
        Throwable cause = exception.getCause();
        if (cause == null){
            return null;
        }
        if (cause instanceof BizException){
            return (BizException) cause;
        }
        cause = cause.getCause();
        if (cause == null){
            return null;
        }
        if (cause instanceof BizException){
            return (BizException) cause;
        }
        return null;
    }

}
