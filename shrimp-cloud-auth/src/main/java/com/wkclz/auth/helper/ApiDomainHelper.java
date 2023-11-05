package com.wkclz.auth.helper;

import com.alibaba.fastjson2.JSON;
import com.wkclz.common.emuns.ResultStatus;
import com.wkclz.common.entity.Result;
import com.wkclz.common.exception.BizException;
import com.wkclz.redis.entity.RedisMsgBody;
import com.wkclz.redis.topic.RedisTopicConfig;
import com.wkclz.spring.config.SpringContextHolder;
import com.wkclz.spring.config.SystemConfig;
import com.wkclz.spring.helper.IpHelper;
import com.wkclz.spring.helper.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
public class ApiDomainHelper {

    private final static Logger logger = LoggerFactory.getLogger(ApiDomainHelper.class);


    /**
     * redis 的缓存主动更新，java 的缓存被动更新
     */
    private static List<String> API_DOMAINS = null;

    /**
     * 初始化 API_DOMAINS
     */
    public static boolean reflash() {
        return reflash(API_DOMAINS);
    }
    public static boolean reflash(List<String> apiDomains) {
        if (CollectionUtils.isEmpty(apiDomains)) {
            throw BizException.error("apiDomains can not be null or empty!");
        }

        if (!SpringContextHolder.getBean(SystemConfig.class).isCloud()){
            API_DOMAINS = apiDomains;
            return true;
        }

        RedisMsgBody body = new RedisMsgBody();
        body.setTag(ApiDomainHelper.class.getName());
        body.setMsg(apiDomains);

        String msg = JSON.toJSONString(body);
        StringRedisTemplate stringRedisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        stringRedisTemplate.convertAndSend(SpringContextHolder.getBean(RedisTopicConfig.class).getCacheTopic(), msg);
        return true;
    }

    /**
     * 初始化 apiDomains 【仅给队列调用，不允许直接调用】
     */
    public static boolean setLocal(Object msg) {
        if (msg == null) {
            throw new BizException("apiDomains can not be null or empty!");
        }
        List<String> apiDomains = JSON.parseArray(msg.toString(), String.class);
        return setLocal(apiDomains);
    }
    public static boolean setLocal(List<String> apiDomains) {
        if (CollectionUtils.isEmpty(apiDomains)) {
            throw new BizException("apiDomains can not be null or empty!");
        }
        API_DOMAINS = apiDomains;
        return true;
    }

    public static List<String> getLocal() {
        return API_DOMAINS;
    }

    public static Result checkApiDomains(HttpServletRequest req, HttpServletResponse rep) {
        List<String> apiDomains = getLocal();
        if (CollectionUtils.isEmpty(apiDomains)) {
            throw new BizException("apiDomains must be init after system start up!");
        }

        String url = req.getRequestURL().toString();
        url = RequestHelper.getDomainFronUrl(url);

        if (apiDomains.contains(url)) {
            return null;
        }

        logger.error("api url can not be cors, url : {}, ip: {}", url, IpHelper.getOriginIp(req));
        Result result = new Result();
        result.setMoreError(ResultStatus.API_CORS);
        return result;
    }

}
