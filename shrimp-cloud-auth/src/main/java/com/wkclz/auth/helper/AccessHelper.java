package com.wkclz.auth.helper;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson2.JSON;
import com.wkclz.auth.entity.AccessLog;
import com.wkclz.auth.entity.User;
import com.wkclz.common.exception.BizException;
import com.wkclz.redis.entity.RedisMsgBody;
import com.wkclz.redis.topic.RedisTopicConfig;
import com.wkclz.spring.config.SpringContextHolder;
import com.wkclz.spring.config.SystemConfig;
import com.wkclz.spring.helper.IpHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
public class AccessHelper {

    private static final PathMatcher MATCHER = new AntPathMatcher();
    private static List<String> ACCESS_URI = null;

    /**
     * 更新全部缓存
     */
    public static boolean reflash() {
        return reflash(ACCESS_URI);
    }
    public static boolean reflash(List<String> accessUris) {
        if (CollectionUtils.isEmpty(accessUris)) {
            throw BizException.error("accessUris can not be null or empty!");
        }

        if (!SpringContextHolder.getBean(SystemConfig.class).isCloud()){
            ACCESS_URI = accessUris;
            return true;
        }

        RedisMsgBody body = new RedisMsgBody();
        body.setTag(AccessHelper.class.getName());
        body.setMsg(accessUris);

        String msg = JSON.toJSONString(body);
        StringRedisTemplate stringRedisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        stringRedisTemplate.convertAndSend(SpringContextHolder.getBean(RedisTopicConfig.class).getCacheTopic(), msg);
        return true;
    }


    /**
     * 初始化 accessUris 【仅给队列调用，不允许直接调用】
     */
    public static boolean setLocal(Object msg) {
        if (msg == null) {
            throw BizException.error("accessUris can not be null or empty!");
        }
        List<String> accessUris = JSON.parseArray(msg.toString(), String.class);
        return setLocal(accessUris);
    }

    public static boolean setLocal(List<String> accessUris) {
        if (CollectionUtils.isEmpty(accessUris)) {
            throw BizException.error("accessUris can not be null or empty!");
        }
        ACCESS_URI = accessUris;
        return true;
    }


    public static List<String> getLocal() {
        if (ACCESS_URI == null) {
            return Arrays.asList("/*/public/**", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs/**");
        }
        return ACCESS_URI;
    }

    public static boolean checkAccessUri(HttpServletRequest req) {
        // 防止重复检测
        boolean access = isAccess(req);
        if (access){
            return true;
        }

        List<String> accessUris = getLocal();

        if (CollectionUtils.isEmpty(accessUris)) {
            throw BizException.error("accessUris must be init after system start up!");
        }

        String uri = req.getRequestURI();
        if (accessUris.contains(uri)) {
            req.setAttribute("access","true");
            return true;
        }
        // PathMatcher 匹配
        for (String accessUri : accessUris) {
            if (accessUri.contains("**")) {
                boolean match = MATCHER.match(accessUri, uri);
                if (match) {
                    req.setAttribute("access","true");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测通过标识
     * @param req
     * @return
     */
    public static boolean isAccess(HttpServletRequest req){
        String access = req.getHeader("access");
        if ("true".equals(access)){
            return true;
        }
        Object accessObj = req.getAttribute("access");
        if ("true".equals(accessObj)){
            return true;
        }
        return false;
    }

    public static AccessLog getAccessLog(HttpServletRequest req) {

        // 此处为全部检测跳过标识。
        String uri = req.getRequestURI();
        if (uri.endsWith(".html") || uri.endsWith(".js") || uri.endsWith(".jpg") || uri.endsWith(".png") || uri.endsWith(".css") || uri.endsWith(".ico")) {
            return null;
        }
        if ("/error".equals(uri) || uri.startsWith("/websocket")) {
            return null;
        }

        AccessLog log = new AccessLog();

        String userAgentHeader = req.getHeader("User-Agent");

        if (userAgentHeader != null) {
            UserAgent ua = UserAgentUtil.parse(userAgentHeader);
            log.setUserAgent(userAgentHeader);
            log.setBrowserName(ua.getBrowser() == null? null:ua.getBrowser().toString());
            log.setBrowserVersion(ua.getVersion());
            log.setEngineName(ua.getEngine() == null ? null:ua.getEngine().toString());
            log.setEngineVersion(ua.getEngineVersion());
            log.setUserOs(ua.getOs() == null ? null:ua.getOs().toString());
            log.setPlatform(ua.getPlatform() == null ? null:ua.getPlatform().toString());
        }

        log.setOsName(System.getProperty("os.name"));
        log.setOsVersion(System.getProperty("os.version"));
        log.setOsArch(System.getProperty("os.arch"));

        log.setHttpProtocol(req.getProtocol());
        log.setCharacterEncoding(req.getCharacterEncoding());
        log.setAccept(req.getHeader("Accept"));
        log.setAcceptLanguage(req.getHeader("Accept-Language"));
        log.setAcceptEncoding(req.getHeader("Accept-Encoding"));
        log.setConnection(req.getHeader("Connection"));
        log.setCookie(req.getHeader("Cookie"));
        log.setOrigin(req.getHeader("Origin"));
        log.setReferer(req.getHeader("Referer"));

        // 在前端使用了 hash 模式的情况下，Referer 只会是主机名，此时增加了参数 path 来重新找回 Referer 应该有的样子
        String path = req.getHeader("path");
        if (log.getOrigin() != null && StringUtils.isNotBlank(path) && (log.getOrigin() + "/").equals(log.getReferer())) {
            log.setReferer(log.getOrigin() + path);
        }

        log.setRequestUrl(req.getRequestURL().toString());
        log.setRequestUri(req.getRequestURI());
        log.setQueryString(req.getQueryString());
        log.setRemoteAddr(IpHelper.getOriginIp(req));
        log.setRemotePort(req.getRemotePort());
        log.setLocalAddr(req.getLocalAddr());
        log.setLocalName(req.getLocalName());
        log.setMethod(req.getMethod());
        log.setServerName(req.getServerName());
        log.setToken(AliveTimeHelper.getToken(req));

        AuthHelper authHelper = SpringContextHolder.getBean(AuthHelper.class);
        if (!StringUtils.isBlank(log.getToken())) {
            User user = authHelper.getUserIfLogin();
            if (user != null) {
                log.setAuthId(user.getAuthId());
                log.setUserId(user.getUserId());
                log.setNickName(user.getUsername());
            }
        }

        // 访问的租户
        Long tenantId = authHelper.getTenantId();
        log.setTenantId(tenantId);

        // 防止太长
        if (log.getUserAgent() != null && log.getUserAgent().length() > 1000) {
            log.setUserAgent(log.getUserAgent().substring(0, 1000));
        }
        if (log.getCookie() != null && log.getCookie().length() > 4000) {
            log.setCookie(log.getCookie().substring(0, 4000));
        }
        if (log.getQueryString() != null && log.getQueryString().length() > 1000) {
            log.setQueryString(log.getQueryString().substring(0, 1000));
        }

        return log;
    }


}











