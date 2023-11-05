package com.wkclz.auth.helper;

import com.wkclz.spring.config.Sys;
import com.wkclz.spring.enums.EnvType;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Description:
 * Created: wangkaicun @ 2018-03-07 下午10:17
 */
public class AliveTimeHelper {

    private static final Integer SESSION_LIVE_TIME_DEV = 24 * 60 * 60;
    private static final Integer SESSION_LIVE_TIME_SIT = 24 * 60 * 60;
    private static final Integer SESSION_LIVE_TIME_UAT = 1800;
    private static final Integer SESSION_LIVE_TIME_PROD = 1800;
    private static final Integer SESSION_LIVE_TIME_WECHAT = 8 * 60 * 60;

    private static final Integer JAVA_CACHE_LIVE_TIME_DEV = 30;
    private static final Integer JAVA_CACHE_LIVE_TIME_SIT = 30;
    private static final Integer JAVA_CACHE_LIVE_TIME_UAT = 1800;
    private static final Integer JAVA_CACHE_LIVE_TIME_PROD = 1800;

    public static String getToken(HttpServletRequest req) {
        String token = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if ("token".equals(name)) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) {
            token = req.getHeader("token");
        }
        if (token == null) {
            token = req.getParameter("token");
        }
        return token;
    }



    public static Integer getJavaCacheLiveTime() {
        Integer liveTime = 1800;
        if (EnvType.PROD == Sys.CURRENT_ENV) {
            liveTime = AliveTimeHelper.JAVA_CACHE_LIVE_TIME_PROD;
        }
        if (EnvType.UAT == Sys.CURRENT_ENV) {
            liveTime = AliveTimeHelper.JAVA_CACHE_LIVE_TIME_UAT;
        }
        if (EnvType.SIT == Sys.CURRENT_ENV) {
            liveTime = AliveTimeHelper.JAVA_CACHE_LIVE_TIME_SIT;
        }
        if (EnvType.DEV == Sys.CURRENT_ENV) {
            liveTime = AliveTimeHelper.JAVA_CACHE_LIVE_TIME_DEV;
        }
        return liveTime;
    }


    public static Integer getSessionLiveTime() {
        Integer liveTime = 1800;
        if (EnvType.PROD == Sys.CURRENT_ENV) {
            liveTime = AliveTimeHelper.SESSION_LIVE_TIME_PROD;
        }
        if (EnvType.UAT == Sys.CURRENT_ENV) {
            liveTime = AliveTimeHelper.SESSION_LIVE_TIME_UAT;
        }
        if (EnvType.SIT == Sys.CURRENT_ENV) {
            liveTime = AliveTimeHelper.SESSION_LIVE_TIME_SIT;
        }
        if (EnvType.DEV == Sys.CURRENT_ENV) {
            liveTime = AliveTimeHelper.SESSION_LIVE_TIME_DEV;
        }
        return liveTime;
    }

    public static Integer getWechatSessionLiveTime() {
        return AliveTimeHelper.SESSION_LIVE_TIME_WECHAT;
    }

}
