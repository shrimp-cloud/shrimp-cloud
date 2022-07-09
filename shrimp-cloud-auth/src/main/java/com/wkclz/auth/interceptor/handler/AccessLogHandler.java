package com.wkclz.auth.interceptor.handler;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 访问拦截，已经在网关实现，此处不再需要
 * Created: wangkaicun @ 2017-10-18 下午11:41
 */
// @Component
public class AccessLogHandler implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rep, Object handler) {
        /*
        AccessLog accessLog = accessHelper.getAccessLog(req);
        if (accessLog!=null){
            CmaAccessLog casAccessLog = new CmaAccessLog();
            BeanUtils.copyProperties(accessLog, casAccessLog);
            cmsAccessLogRepo.insert(casAccessLog, req);
        }
        */
        return true;
    }

}
