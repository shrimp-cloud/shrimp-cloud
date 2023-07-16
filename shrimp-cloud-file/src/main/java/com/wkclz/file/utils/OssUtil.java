package com.wkclz.file.utils;

import cn.hutool.core.date.DateUtil;
import com.wkclz.spring.config.Sys;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Description:
 * Created: wangkaicun @ 2017-12-23 下午2:48
 */
public class OssUtil {

    public static String getFullName(String businessType, String name) {
        return getPath(businessType) + "/" + getName(name);
    }

    /**
     * 生成文件的全路径
     *
     * @param businessType
     * @return
     */
    public static String getPath(String businessType) {
        if (StringUtils.isBlank(businessType)) {
            businessType = "default";
        }
        String appGroup = Sys.APPLICATION_GROUP.toLowerCase();
        String env = Sys.CURRENT_ENV.toString().toLowerCase();
        businessType = businessType.toLowerCase();
        String day = DateUtil.format(new Date(), "yyyyMMdd");
        String path = appGroup + "/" + env + "/" + businessType + "/" + day;
        return path;
    }

    /**
     * 生产上传文件名
     */
    public static String getName(String name) {
        String datetime = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
        if (name == null) {
            return datetime;
        }
        name = name.replace("(", "_");
        name = name.replace(")", "_");
        name = name.replace("+", "_");
        name = name.replace(";", "_");
        name = name.replace("&", "_");
        return datetime + "_" + name;
    }


}
