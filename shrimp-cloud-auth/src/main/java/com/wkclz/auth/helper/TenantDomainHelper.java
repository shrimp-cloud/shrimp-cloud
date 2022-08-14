package com.wkclz.auth.helper;

import com.alibaba.fastjson2.JSON;
import com.wkclz.auth.config.AuthConfig;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.utils.RegularUtil;
import com.wkclz.redis.entity.RedisMsgBody;
import com.wkclz.redis.topic.RedisTopicConfig;
import com.wkclz.spring.config.SpringContextHolder;
import com.wkclz.spring.config.SystemConfig;
import com.wkclz.spring.constant.LogTraceConstant;
import com.wkclz.spring.helper.RequestHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Description: 获取我租户信息。不公开使用。统一 AuthHelper 公开使用
 * @date : wangkaicun @ 2019-02-13 20:55:11
 */
public class TenantDomainHelper {

    private static Map<String, Object> TENANT_DOMAINS = null;

    /**
     * 初始化 TENANT_DOMAINS
     */
    public static boolean reflash() {
        return reflash(TENANT_DOMAINS);
    }
    public static boolean reflash(Map<String, Object> tenantDomains) {
        if (tenantDomains == null || tenantDomains.size() == 0) {
            throw new BizException("tenantDomains can not be null or empty!");
        }

        if (!SpringContextHolder.getBean(SystemConfig.class).isCloud()){
            TENANT_DOMAINS = tenantDomains;
            return true;
        }

        RedisMsgBody body = new RedisMsgBody();
        body.setTag(TenantDomainHelper.class.getName());
        body.setMsg(tenantDomains);

        String msg = JSON.toJSONString(body);
        StringRedisTemplate stringRedisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        stringRedisTemplate.convertAndSend(SpringContextHolder.getBean(RedisTopicConfig.class).getCacheTopic(), msg);
        return true;

    }
    public static boolean setLocal(Object msg) {
        if (msg == null) {
            throw BizException.error("tenantDomains can not be null or empty!");
        }
        Map<String, Object> tenantDomains = JSON.parseObject(JSON.toJSONString(msg), Map.class);
        return setLocal(tenantDomains);
    }
    public static boolean setLocal(Map<String, Object> tenantDomains) {
        if (CollectionUtils.isEmpty(tenantDomains)) {
            throw BizException.error("tenantDomains can not be null or empty!");
        }
        TENANT_DOMAINS = tenantDomains;
        return true;
    }

    public static Map<String, Object> getLocal() {
        return TENANT_DOMAINS;
    }


    /**
     * 获取 tenantId。成功获取之后，MDC 一定会有值
     * @return
     */
    public static Long getTenantId() {

        AuthConfig authConfig = SpringContextHolder.getBean(AuthConfig.class);
        if (!authConfig.getSecurityDomainTenant()) {
            return -1L;
        }

        String tenantIdStr = MDC.get(LogTraceConstant.TENANT_ID);
        if (tenantIdStr != null){
            return Long.valueOf(tenantIdStr);
        }

        HttpServletRequest req = RequestHelper.getRequest();
        if (req == null) {
            MDC.put(LogTraceConstant.TENANT_ID, "-1");
            return -1L;
        }

        tenantIdStr = req.getHeader(LogTraceConstant.TENANT_ID);
        if (tenantIdStr != null){
            MDC.put(LogTraceConstant.TENANT_ID, tenantIdStr);
            return Long.valueOf(tenantIdStr);
        }

        String domain = RequestHelper.getFrontDomain(req);

        if (StringUtils.isBlank(domain)) {
            throw BizException.error("can not get domain from the request: {}", RequestHelper.getRequestUrl());
        }

        Map<String, Object> tenantDomains = getLocal();
        if (tenantDomains == null || tenantDomains.size() == 0) {
            throw BizException.error("tenantDomains must be init after system start up: {}", RequestHelper.getRequestUrl());
        }

        Object tenantId = tenantDomains.get(domain);
        if (tenantId != null) {
            MDC.put(LogTraceConstant.TENANT_ID, tenantId.toString() + "");
            return Long.valueOf(tenantId.toString());
        }

        if (RegularUtil.isIp(domain)){
            tenantId = -1L;
            MDC.put(LogTraceConstant.TENANT_ID, tenantId + "");
            return -1L;
        }

        throw BizException.error("domain {} is undefined, can not get tenantId!", domain);
    }

}
