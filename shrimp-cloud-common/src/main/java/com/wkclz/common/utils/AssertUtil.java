package com.wkclz.common.utils;

import com.wkclz.common.exception.UserException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

public class AssertUtil {

    public static void notNull(Object obj){
        if (obj == null){
            throw UserException.error("object can not be null;");
        }
    }

    public static void notNull(String obj, String errMsg){
        if (StringUtils.isBlank(obj)){
            throw UserException.error(errMsg);
        }
    }
    public static void notNull(Integer obj, String errMsg){
        if (obj == null){
            throw UserException.error(errMsg);
        }
    }
    public static void notNull(Long obj, String errMsg){
        if (obj == null){
            throw UserException.error(errMsg);
        }
    }
    public static void notNull(BigDecimal obj, String errMsg){
        if (obj == null){
            throw UserException.error(errMsg);
        }
    }
    public static void notNull(Date obj, String errMsg){
        if (obj == null){
            throw UserException.error(errMsg);
        }
    }

}
