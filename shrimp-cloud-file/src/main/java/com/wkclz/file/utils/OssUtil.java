package com.wkclz.file.utils;

import cn.hutool.core.date.DateUtil;
import com.wkclz.file.domain.ContentTypeEnum;
import com.wkclz.spring.config.Sys;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Description:
 * Created: wangkaicun @ 2017-12-23 下午2:48
 */
public class OssUtil {

    /**
     * 获取文件的媒体类型
     */
    public static String getContentType(String originalFilename) {
        if (originalFilename == null) {
            return ContentTypeEnum.DEFAULT.getContentType();
        }
        int i = originalFilename.lastIndexOf(".");
        String subName = i > 0 ? originalFilename.substring(i) : null;
        return ContentTypeEnum.getContentTypeBySubName(subName);
    }

    /**
     * 生成文件的全路径
     */
    public static String getFullName(String businessType, String name) {

        if (StringUtils.isBlank(businessType)) {
            businessType = "default";
        }
        String appGroup = Sys.APPLICATION_GROUP.toLowerCase();
        String env = Sys.CURRENT_ENV.toString().toLowerCase();
        businessType = businessType.toLowerCase();
        String day = DateUtil.format(new Date(), "yyyyMMdd");
        String path = appGroup + "/" + env + "/" + businessType + "/" + day;

        String datetime = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
        if (name == null) {
            return datetime;
        }
        name = name.replace("(", "_");
        name = name.replace(")", "_");
        name = name.replace("+", "_");
        name = name.replace(";", "_");
        name = name.replace("&", "_");
        name = datetime + "_" + name;

        return path + "/" + name;
    }


}
