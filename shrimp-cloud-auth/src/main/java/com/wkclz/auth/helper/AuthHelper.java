package com.wkclz.auth.helper;

import com.alibaba.fastjson2.JSON;
import com.wkclz.auth.entity.Token;
import com.wkclz.auth.entity.User;
import com.wkclz.common.emuns.ResultStatus;
import com.wkclz.common.emuns.SysConfigEnum;
import com.wkclz.common.entity.Result;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.utils.RegularUtil;
import com.wkclz.spring.constant.LogTraceConstant;
import com.wkclz.spring.helper.IpHelper;
import com.wkclz.spring.helper.RequestHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
@Component
public class AuthHelper {

    private final static Logger logger = LoggerFactory.getLogger(AuthHelper.class);
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 设置session
     * @return
     */
    public Map<String, String> setUser(User user) {
        return setUser(null, user);
    }
    public Map<String, String> setUser(HttpServletRequest req, User user) {

        if (user.getAuthId() == null) {
            throw BizException.error("authId can not be null to setSession");
        }

        Map<String, String> tokenMap = new HashMap<>();

        // 已经登录的情况
        /*
        if (session != null) {
            user = session;
            Token token = new Token(user.getAuthId(), user.getUserId(), user.getToken());
            // addCookie(req, rep, "token", token.base64(), getSessionLiveTime());
            tokenMap.put("token", token.base64());
            return tokenMap;
        }
        */
        // session 对象
        Token token = new Token(user.getAuthId(), user.getUserId(), user.getToken());
        user.setToken(token.getToken());

        // 设置线程变更
        MDC.put("user", JSON.toJSONString(user));

        // 设置 redis
        String redisKey = token.contactRedisKey();
        String userStr = JSON.toJSONString(user);
        Integer sessionLiveTime = AliveTimeHelper.getSessionLiveTime();
        if ("wechat".equals(user.getTerminal())) {
            sessionLiveTime = AliveTimeHelper.getWechatSessionLiveTime();
        }
        stringRedisTemplate.opsForValue().set(redisKey, userStr, sessionLiveTime, TimeUnit.SECONDS);

        // 登录成功日志【仅在登录的时候需要，漫游时不需要】
        if (req != null){
            User session = getUserIfLogin();
            if (session == null) {
                // 登录请求，漫游
                logger.info("漫游请求，重建，uri: {}", req.getRequestURI());
            } else {
                // 登录请求，漫游
                logger.info("正常登录请求，建立，uri: {}", req.getRequestURI());
            }
        }

        // addCookie(req, rep, "token", token.base64(), getSessionLiveTime());

        tokenMap.put("token", token.base64());
        return tokenMap;
    }


    public User getUser() {
        User user = getUserIfLogin();
        if (user == null){
            throw BizException.error(ResultStatus.LOGIN_TIMEOUT);
        }
        return user;
    }


    /**
     * 获取 session
     *
     * @return
     */
    public User getUserIfLogin() {
        String userStr = MDC.get("user");
        if (userStr != null){
            return JSON.parseObject(userStr, User.class);
        }
        HttpServletRequest req = RequestHelper.getRequest();
        if (req == null) {
            return null;
        }
        // user
        userStr = req.getHeader("user");
        if (StringUtils.isNotBlank(userStr)){
            MDC.put("user", userStr);
            User user = JSON.parseObject(userStr, User.class);
            return user;
        }
        // 在使用 token 之前，校验是否要登录, 若无需登录，不取用户， 【也要去尝试取用户，能取到就要】
        /*
        boolean checkAccessUriResult = AccessHelper.checkAccessUri(req);
        if (checkAccessUriResult){
            return null;
        }
        */
        String tokenStr = AliveTimeHelper.getToken(req);
        return getUser(tokenStr);
    }

    /**
     * 获取 session
     *
     * @param tokenStr
     * @return
     */
    private User getUser(String tokenStr) {
        if (StringUtils.isBlank(tokenStr)) {
            return null;
        }
        Token token = Token.decodeToken(tokenStr);

        String redisKey = token.contactRedisKey();
        String userStr = stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(userStr)) {
            logger.error("can find nothing to get user info, please login at first!");
            return null;
        }
        MDC.put("user", userStr);
        User user = JSON.parseObject(userStr, User.class);
        // 延期 redis
        Integer sessionLiveTime = AliveTimeHelper.getSessionLiveTime();
        if ("wechat".equals(user.getTerminal())) {
            sessionLiveTime = AliveTimeHelper.getWechatSessionLiveTime();
        }
        stringRedisTemplate.expire(redisKey, sessionLiveTime, TimeUnit.SECONDS);
        return user;
    }


    /**
     * 退出登录
     *
     * @param req
     */
    public void invalidateSession(HttpServletRequest req, HttpServletResponse rep) {

        String tokenStr = AliveTimeHelper.getToken(req);
        if (StringUtils.isBlank(tokenStr)) {
            req.getSession().invalidate();
            return;
        }
        MDC.remove("user");
        Token token = Token.decodeToken(tokenStr);
        stringRedisTemplate.expireAt(token.contactRedisKey(), new Date());
        expireCookie(req, rep, "token");
    }


    public Long getTenantId() {
        return TenantDomainHelper.getTenantId();
    }
    public Long forceGetTenantId() {
        Long tenantId = TenantDomainHelper.getTenantId();
        if (tenantId > 0) {
            return tenantId;
        }
        HttpServletRequest req = RequestHelper.getRequest();
        String domain = RequestHelper.getFrontDomain(req);
        throw BizException.error("can not map tenamtId with domain: {}", domain);
    }



    /**
     * 添加 cookie
     *
     * @param rep
     * @param name
     * @param value
     * @param maxAge
     */
    private void addCookie(HttpServletRequest req, HttpServletResponse rep, String name, String value, Integer maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        String cookieDomain = getCookieDomain(req);
        if (!StringUtils.isBlank(cookieDomain) && !RegularUtil.isIp(cookieDomain) && !"localhost".equals(cookieDomain) ){
            cookie.setDomain(cookieDomain);
        }

        if (maxAge != null && maxAge > 0) {
            cookie.setMaxAge(maxAge);
        }
        rep.addCookie(cookie);
    }

    /**
     * 根据名字获取cookie
     *
     * @param req
     * @param name
     * @return
     */
    private static Cookie getCookieByName(HttpServletRequest req, String name) {
        Map<String, Cookie> cookieMap = readCookieMap(req);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = cookieMap.get(name);
            return cookie;
        } else {
            return null;
        }
    }

    /**
     * 将cookie封装到Map里面
     *
     * @param req
     * @return
     */
    private static Map<String, Cookie> readCookieMap(HttpServletRequest req) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = req.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }


    private void expireCookie(HttpServletRequest req, HttpServletResponse rep, String name) {

        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        String cookieDomain = getCookieDomain(req);
        cookie.setDomain(cookieDomain);
        cookie.setMaxAge(0);
        rep.addCookie(cookie);
    }


    /**
     * 用户 token 检测。未登录将会报错
     *
     * @param req
     * @param rep
     * @return
     */
    public Result checkUserToken(HttpServletRequest req, HttpServletResponse rep) {

        String uri = req.getRequestURI();

        // 严格限制 token 传递的位置
        /*
        String heaterToken = req.getHeader("token");
        String parameterToken = req.getParameter("token");

        if (StringUtils.isNotBlank(heaterToken) && heaterToken.length() > 63){
            logger.info("heaterToken is not allow, heaterToken: {}", heaterToken);
            Result result = new Result();
            result.setMoreError(Result.TOKEN_ILLEGAL_LENGTH);
            return result;
        }

        if (StringUtils.isNotBlank(parameterToken) && parameterToken.length() > 63){
            logger.info("parameterToken is not allow, parameterToken: {}", parameterToken);
            Result result = new Result();
            result.setMoreError(Result.TOKEN_ILLEGAL_LENGTH);
            return result;
        }

        if ( (!StringUtils.isBlank(heaterToken) && heaterToken.startsWith("temp_")) || !StringUtils.isBlank(parameterToken) && !parameterToken.startsWith("temp_") ){
            logger.info("token illegal transfer, uri : {}, ip: {}", uri, IpHelper.getIpAddr(req));
            Result result = new Result();
            result.setMoreError(Result.TOKEN_ILLEGAL_TRANSFER);
            return result;
        }
        */

        // token 检测，需要检测权限的uri，没有 token均不放过
        String tokenStr = AliveTimeHelper.getToken(req);

        if (StringUtils.isBlank(tokenStr)) {
            logger.warn("token is null, uri : {}", uri, IpHelper.getOriginIp(req));
            Result result = new Result();
            result.setMoreError(ResultStatus.TOKEN_UNLL);
            return result;
        }

        Token token = Token.decodeToken(tokenStr);
        // token 签名
        String sign = token.makeSign();
        if (!sign.equals(token.getSign())) {
            Result result = new Result();
            invalidateSession(req, rep);
            logger.warn("token sign faild, uri : {}, ip: {}, tokrn: {}", uri, IpHelper.getOriginIp(req), tokenStr);
            result.setMoreError(ResultStatus.TOKEN_SIGN_FAILD);
            return result;
        }

        // token 三天超时强制过期
        if (token.getExpireTime().getTime() < System.currentTimeMillis()) {
            Result result = new Result();
            invalidateSession(req, rep);
            logger.warn("token is force timeout, uri : {}, ip: {}", uri, IpHelper.getOriginIp(req));
            result.setMoreError(ResultStatus.LOGIN_FORCE_TIMEOUT);
            return result;
        }


        // 到 redis 去查找，找不到，不放过
        User user = getUserIfLogin();
        if (user == null) {
            Result result = new Result();
            invalidateSession(req, rep);
            logger.warn("token is error, uri : {}, ip: {}", uri, IpHelper.getOriginIp(req));
            result.setMoreError(ResultStatus.TOKEN_ERROR);
            return result;
            /*
            // 对开发环境直接赋值登录
            if (EnvType.DEV == EnvType.CURRENT_ENV){
                userDto = new User();
                userDto.setAuthId(1);
                logger.info("token不正确，启用开发者模式，将拟用用户 "+userDto.getAuthId()+" 进行自动登录。如需以其他用户进行测试，请先请求登录接口拿到正确 token");
            } else {
                authHelper.invalidateSession(req);
                logger.info("token is error, uri : {}",uri);
                result.setMoreError(Result.TOKEN_ERROR);
                return result;
            }
            */
        }


        // IP有变化的，不放过
        /*
        String ip = IpHelper.getIpAddr(req);
        if ( userDto != null && !StringUtils.isBlank(userDto.getIp()) && !ip.equalsIgnoreCase(userDto.getIp()) ){
            logger.info("client is changed, uri : {}",uri);
            Result result = new Result();
            result.setError(ErrorBase.CLIENT_CHANGE);
            return result;
        }
        */

        // 管理后台
        /*
        String origin = req.getHeader("Origin");
        Long tenantId = getTenantId();
        if (StringUtils.isNotBlank(origin) && tenantId != null && origin.contains("admin.")) {
            List<Long> adminIds = user.getAdminIds();
            if (adminIds == null || adminIds.size() == 0 || !adminIds.contains(tenantId)) {
                logger.info("user is not admin, uri : {}", uri);
                Result result = new Result();
                result.setError("非法访问管理后台");
                return result;
            }
        }
        */

        // session检测，已经有session的， token 对的，放过
        if (user != null && !StringUtils.isBlank(user.getToken()) && token.getToken().equalsIgnoreCase(user.getToken())) {
            // session 没有的情况，重新赋值
            String userStr = MDC.get("user");
            if (userStr == null) {
                if (user != null) {
                    MDC.put("user", JSON.toJSONString(user));
                }
            }
            return null;
        }

        // 只有返回true才会继续向下执行，返回false取消当前请求
        return Result.error("鉴权不通过，未知原因");
    }

    /**
     * 如果 session 不存在于内存，需要创建
     *
     * @param req
     */
    public User checkUserSession(HttpServletRequest req) {
        User user = getUserIfLogin();
        if (user != null) {
            if (user.getTenantId() != null){
                MDC.put(LogTraceConstant.TENANT_ID, user.getTenantId()+"");
            }
            if (user.getUserId() != null){
                MDC.put(LogTraceConstant.USER_ID, user.getUserId()+"");
            }
            if (user.getAuthId() != null){
                MDC.put(LogTraceConstant.AUTH_ID, user.getAuthId()+"");
            }
        }
        return user;
    }

    private String getCookieDomain(HttpServletRequest req) {

        String cookieDomain = RequestHelper.getFrontDomain(req);

        // 测试环境优先
        if ("127.0.0.1".equals(cookieDomain) || "localhost".equals(cookieDomain)) {
            logger.info("=========> 开发环境，cookie domain: {}", cookieDomain);
            return cookieDomain;
        }

        // db 配置其二
        cookieDomain = SystemConfigHelper.getSystemConfig(SysConfigEnum.COOKIE_DOMAIN.getKey());

        if (StringUtils.isBlank(cookieDomain)) {
            cookieDomain = RequestHelper.getFrontDomain(req);
            logger.info("=========> 无cookie域，domain 截取 cookie: {}", cookieDomain);
        }

        int indexOf = cookieDomain.indexOf(".");
        cookieDomain = cookieDomain.substring(indexOf + 1);

        return cookieDomain;
    }
}
