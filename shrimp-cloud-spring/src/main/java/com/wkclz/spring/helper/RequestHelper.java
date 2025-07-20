package com.wkclz.spring.helper;

import com.wkclz.common.entity.BaseEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Description:
 * Created: wangkaicun @ 2018-03-07 下午10:17
 */
public class RequestHelper {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();


    private static final Logger logger = LoggerFactory.getLogger(RequestHelper.class);


    public static boolean match(String rule, String uri) {
        if (StringUtils.isBlank(rule) || StringUtils.isBlank(uri)) {
            return false;
        }
        return ANT_PATH_MATCHER.match(rule, uri);
    }


    public static List<Long> getIdsFromBaseModel(BaseEntity entity) {
        List<Long> ids = entity.getIds();
        if (entity.getIds() == null) {
            ids = new ArrayList<>();
        }
        if (entity.getId() != null) {
            ids.add(entity.getId());
        }
        return ids;
    }

    public static Map<String, String> getParamsFromRequest(HttpServletRequest req) {

        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = req.getParameterMap();

        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        return params;
    }

    public static String getRequestUrl(){
        HttpServletRequest request = getRequest();
        if (request == null){
            return "unknown";
        }
        return request.getRequestURL().toString();
    }

    /**
     * 获取当前请求
     * @return
     */
    public static HttpServletRequest getRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null){
            return null;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getRequest();
    }


    private static String getFrontUrl(HttpServletRequest req) {
        String domain = req.getHeader("Origin");
        if (StringUtils.isBlank(domain) || "null".equals(domain)) {
            domain = req.getHeader("Referer");
        }
        // 非前后分离的情况，为当前域名
        if (StringUtils.isBlank(domain)) {
            domain = req.getRequestURL().toString();
        }
        return domain;
    }

    /**
     * 获取请求域名
     *
     * @param req
     * @return
     */
    public static String getFrontDomain(HttpServletRequest req) {
        String frontUrl = getFrontUrl(req);
        String domain = getDomainFronUrl(frontUrl);
        return domain;
    }

    public static Integer getFrontPort(HttpServletRequest req) {
        String frontUrl = getFrontUrl(req);
        Integer port = getPortFronUrl(frontUrl);
        return port;
    }

    public static String getDomainFronUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return url;
        }
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        try {
            URL url1 = new URL(url);
            String host = url1.getHost();
            return host;
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Integer getPortFronUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            URL url1 = new URL(url);
            int port = url1.getPort();
            if (port < 1){
                return null;
            }
            return port;
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getFrontPortalDomainPort(HttpServletRequest req) {
        String domain = getFrontDomain(req);
        Integer port = getFrontPort(req);
        String protocol = req.getProtocol();
        String portalDomainPort = protocol + "://" + domain;
        if (port != null && !("http".equalsIgnoreCase(protocol) && port == 80) && !("https".equalsIgnoreCase(protocol) && port == 443)){
            portalDomainPort += ":" + port;
        }
        return portalDomainPort;
    }


}
