package com.wkclz.spring.utils;

import com.wkclz.common.exception.SysException;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

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

    public static Template getTemplate(String templateName) {
        try {
            CONFIGURATION.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTemplateUtil.class, "/templates"));
            return CONFIGURATION.getTemplate(templateName);
        } catch (IOException e) {
            throw SysException.error(e.getMessage());
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
    public static Template getTemplate(String templateName, String templatesDir) {
        try {
            if (StringUtils.isNotBlank(templatesDir)) {
                CONFIGURATION.setDirectoryForTemplateLoading(new File(templatesDir));
            }
            return CONFIGURATION.getTemplate(templateName);
        } catch (IOException e) {
            throw SysException.error(e.getMessage());
        }
    }

    public static void clearCache() {
        CONFIGURATION.clearTemplateCache();
    }


    public static String parseString(String content, Map<String, Object> params) throws IOException, TemplateException {
        Configuration stringConfig = new Configuration(Configuration.VERSION_2_3_23);
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("_template_", content);
        stringConfig.setTemplateLoader(stringLoader);
        Template tpl = stringConfig.getTemplate("_template_", "utf-8");
        return FreeMarkerTemplateUtils.processTemplateIntoString(tpl, params);
    }
}
