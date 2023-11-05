package com.wkclz.spring.helper;

import com.wkclz.util.excel.Excel;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ExcelHelper {

    private static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class);

    public static void excelStreamResopnse(HttpServletResponse response, Excel excel) {

        OutputStream fops = null;
        InputStream in = null;
        int len = 0;
        byte[] bytes = new byte[1024];
        try {
            File file = excel.createXlsxByFile();
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            logger.info("the excel file is in {}", file.getPath());

            in = new FileInputStream(file);

            response.setContentType("application/x-excel");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(excel.getTitle().getBytes("utf-8"), "ISO8859-1") + "." + suffix);
            response.setHeader("Content-Length", String.valueOf(file.length()));

            fops = response.getOutputStream();
            while ((len = in.read(bytes)) != -1) {
                fops.write(bytes, 0, len);
            }
            fops.flush();
            fops.close();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (fops != null) {
                try {
                    fops.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
