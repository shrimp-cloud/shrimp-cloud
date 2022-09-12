package com.wkclz.cas.sdk.helper;

import com.alibaba.fastjson2.JSON;
import com.wkclz.common.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseHelper {

    private final static Logger logger = LoggerFactory.getLogger(ResponseHelper.class);

    public static boolean responseError(HttpServletResponse response, Result result) {
        try {
            result.setRequestTime(null);
            result.setResponseTime(null);
            result.setCostTime(null);
            String string = JSON.toJSONString(result);
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.getWriter().print(string);
            response.getWriter().close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return false;
    }

}
