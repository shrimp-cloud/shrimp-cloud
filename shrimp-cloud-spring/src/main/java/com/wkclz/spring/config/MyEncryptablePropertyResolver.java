package com.wkclz.spring.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.wkclz.common.utils.SecretUtil;
import com.wkclz.spring.constant.BaseConstant;

public class MyEncryptablePropertyResolver implements EncryptablePropertyResolver {
    //自定义解密方法
    @Override
    public String resolvePropertyValue(String s) {
        if (null != s && s.startsWith(BaseConstant.CONFIG_ENCRYPTED_PREFIX)) {
            return SecretUtil.getDecryptPassword(s.substring(BaseConstant.CONFIG_ENCRYPTED_PREFIX.length()));
        }
        return s;
    }
}

