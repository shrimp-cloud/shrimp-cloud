package com.wkclz.spring.helper;

import com.alibaba.fastjson2.JSON;
import com.wkclz.common.entity.Result;
import com.wkclz.common.exception.BizException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ResponseHelper {

    private static final Logger logger = LoggerFactory.getLogger(ResponseHelper.class);

    public static boolean responseError(HttpServletResponse rep, Result result) {
        try {
            result.setRequestTime(null);
            result.setResponseTime(null);
            result.setCostTime(null);
            String string = JSON.toJSONString(result);
            rep.setHeader("Content-Type", "application/json;charset=UTF-8");
            rep.getWriter().print(string);
            rep.getWriter().close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return false;
    }


    public static void resopnseExcel(HttpServletResponse response, String file) {
        resopnseExcel(response, new File(file));
    }
    public static void resopnseExcel(HttpServletResponse response, File file) {
        if (response == null || file == null) {
            throw BizException.error("response and file can not be null!");
        }

        String fileName = file.getName();
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        logger.info("the excel file is in {}", file.getPath());

        response.setContentType("application/x-excel");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "." + suffix);
        response.setHeader("Content-Length", String.valueOf(file.length()));

        try (
            InputStream in = new FileInputStream(file);
            OutputStream fops = response.getOutputStream();
        ) {
            byte[] bytes = new byte[1024];
            int len;
            while ((len = in.read(bytes)) != -1) {
                fops.write(bytes, 0, len);
            }
            fops.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
