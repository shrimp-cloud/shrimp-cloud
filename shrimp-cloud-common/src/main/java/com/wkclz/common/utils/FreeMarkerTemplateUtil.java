package com.wkclz.common.utils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2018-03-20 下午11:47
 */
public class FreeMarkerTemplateUtil {

    private static final Logger logger = LoggerFactory.getLogger(FreeMarkerTemplateUtil.class);

    private FreeMarkerTemplateUtil() {
    }

    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_22);

    static {
        //这里比较重要，用来指定加载模板所在的路径
        CONFIGURATION.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTemplateUtil.class, "/templates"));
        CONFIGURATION.setDefaultEncoding("UTF-8");
        CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CONFIGURATION.setCacheStorage(NullCacheStorage.INSTANCE);
    }

    public static Template getTemplate(String templateName) throws IOException {
        try {
            CONFIGURATION.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTemplateUtil.class, "/templates"));
            return CONFIGURATION.getTemplate(templateName);
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 自定义路径
     *
     * @param templateName
     * @param templatesDir
     * @return
     * @throws IOException
     */
    public static Template getTemplate(String templateName, String templatesDir) throws IOException {
        if (StringUtils.isNotBlank(templatesDir)) {
            CONFIGURATION.setDirectoryForTemplateLoading(new File(templatesDir));
        }
        try {
            return CONFIGURATION.getTemplate(templateName);
        } catch (IOException e) {
            throw e;
        }
    }

    public static void clearCache() {
        CONFIGURATION.clearTemplateCache();
    }


    public static String parseString(String content, Map<String, Object> params) {
        try {
            Configuration stringConfig = new Configuration(Configuration.VERSION_2_3_23);
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            stringLoader.putTemplate("_template_", content);
            stringConfig.setTemplateLoader(stringLoader);
            Template tpl = stringConfig.getTemplate("_template_", "utf-8");
            return org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString(tpl, params);
        } catch (TemplateNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (TemplateException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
