package com.wkclz.spring.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.wkclz.common.utils.SecretUtil;
import com.wkclz.spring.constant.BaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class MyEncryptablePropertyResolver implements EncryptablePropertyResolver {


    private final SystemConfig systemConfig;

    public MyEncryptablePropertyResolver(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
    }

    //自定义解密方法
    @Override
    public String resolvePropertyValue(String s) {

        if (null != s && s.startsWith(BaseConstant.CONFIG_ENCRYPTED_PREFIX)) {

            String aesKey = systemConfig.getConfigDecryptAesKey();
            if (StringUtils.isBlank(aesKey)) {
                // TODO 需要考虑此配置项需要放在何处，用户同时知道密文，加密 key, 及算法，将可以轻易解密原文
                log.warn("请自定义配置项 (shrimp.config.decrypt-aes-key) 以确保加密内容安全");
            }

            String encryptTxt = s.substring(BaseConstant.CONFIG_ENCRYPTED_PREFIX.length());

            return StringUtils.isBlank(aesKey) ?
                SecretUtil.getDecryptPassword(encryptTxt)
                :SecretUtil.getDecryptPassword(encryptTxt, aesKey);
        }
        return s;
    }
}

