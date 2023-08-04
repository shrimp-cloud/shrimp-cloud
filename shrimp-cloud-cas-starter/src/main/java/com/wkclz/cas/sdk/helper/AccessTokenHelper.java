package com.wkclz.cas.sdk.helper;

import com.wkclz.cas.sdk.cache.BAppInfoCache;
import com.wkclz.cas.sdk.config.CasSdkConfig;
import com.wkclz.cas.sdk.pojo.appinfo.AccessToken;
import com.wkclz.cas.sdk.pojo.appinfo.AppInfo;
import com.wkclz.common.utils.SecretUtil;
import com.wkclz.redis.helper.RedisLockHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;


@Component
public class AccessTokenHelper {
    private final static Logger logger = LoggerFactory.getLogger(AccessTokenHelper.class);


    @Autowired
    private CasSdkConfig casSdkConfig;
    @Autowired
    private BAppInfoCache appInfoCache;
    @Autowired
    private RedisLockHelper redisLockHelper;

    /**
     * 获取签名
     */
    public String getSign() {
        String appId = casSdkConfig.getAppId();
        String appSecret = casSdkConfig.getAppSecret();
        long timestamp = System.currentTimeMillis();
        String nonce = SecretUtil.md5(UUID.randomUUID().toString() + timestamp);

        // 生成签名
        Map<String, String> data = new HashMap<>();
        data.put("appId", appId);
        data.put("nonce", nonce);
        data.put("timestamp", timestamp + "");
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        // 排序
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            // 参数值为空，则不参与签名
            if (data.get(k).trim().length() > 0) {
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
            }
        }
        return encrypt(appSecret, sb.substring(0, sb.length() - 1));
    }


    /**
     * 解析并校验签名
     */
    public boolean deSign(String appId, String sign) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(sign)) {
            return false;
        }

        AppInfo appInfo = appInfoCache.get(casSdkConfig.getAppCode());
        Map<String, AccessToken> accessTokens = appInfo.getAccessTokens();
        AccessToken accessToken = accessTokens.get(appId);
        if (accessToken == null) {
            logger.error("appId: {} 不存在, 无法解密", appId);
            return false;
        }

        // 签名验证
        String decrypt;
        try {
            decrypt = decrypt(accessToken.getAppPublicKey(), sign);
        } catch (Exception e) {
            logger.error("appId: {} 解密失败", appId);
            return false;
        }

        // 获取信息进行详情验证
        String[] split = decrypt.split("&");
        Map<String, String> data = new HashMap<>();
        for (String s : split) {
            String[] t = s.split("=");
            if (t.length == 2) {
                data.put(t[0], t[1]);
            }
        }

        String appId2 = data.get("appId");
        String timestamp = data.get("timestamp");
        String nonce = data.get("nonce");

        // 验证 appId1 是否被偷换
        if (appId2 == null) {
            logger.error("签名中缺少 appId: {}! ", appId);
            return false;
        }
        if (!appId2.equals(appId)) {
            logger.error("请求appId: {}, 验证appId: {} 不匹配，验证失败! ", appId, appId2);
            return false;
        }

        // 验证 timestamp 是否已过期
        if (timestamp == null) {
            logger.error("签名中缺少 timestamp: {}! ", appId);
            return false;
        }
        long l = Long.parseLong(timestamp);
        if (System.currentTimeMillis() - l > 5 * 60 * 1000) {
            logger.error("请求已过期，appId: {}, : timestamp: {}! ", appId, timestamp);
            return false;
        }

        // 验证 nonce 是否重复请求
        if (nonce == null) {
            logger.error("签名中缺少 nonce: {}! ", appId);
            return false;
        }
        String key = "cas:access:" + nonce;
        boolean lock = redisLockHelper.lock(key, 5 * 60);
        if (!lock) {
            logger.error("重复的请求 appId: {}, nonce: {} ", appId, nonce);
            return false;
        }

        return true;
    }

    private static String encrypt(String privateKeyStr, String plainText) {
        try {
            byte[] keyBytes = SecretUtil.base64Decode(privateKeyStr);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance("RSA", "SunRsaSign");
            PrivateKey privateKey = factory.generatePrivate(spec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            try {
                cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            } catch (InvalidKeyException e) {
                //For IBM JDK, 原因请看解密方法中的说明
                RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
                RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
                Key fakePublicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, fakePublicKey);
            }

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            String encryptedString = SecretUtil.base64Encode(encryptedBytes);
            return encryptedString;
        } catch (Exception e) {
            throw new RuntimeException("加密计算出现异常", e);
        }
    }

    private static String decrypt(String publicKeyStr, String cipherText) {
        try {
            PublicKey publicKey = getPublicKey(publicKeyStr);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            try {
                cipher.init(Cipher.DECRYPT_MODE, publicKey);
            } catch (InvalidKeyException e) {
                // 因为 IBM JDK 不支持私钥加密, 公钥解密, 所以要反转公私钥
                // 也就是说对于解密, 可以通过公钥的参数伪造一个私钥对象欺骗 IBM JDK
                RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
                RSAPrivateKeySpec spec = new RSAPrivateKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
                Key fakePrivateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, fakePrivateKey);
            }

            if (cipherText == null || cipherText.length() == 0) {
                return cipherText;
            }

            byte[] cipherBytes = SecretUtil.base64Decode(cipherText);
            byte[] plainBytes = cipher.doFinal(cipherBytes);

            return new String(plainBytes);
        } catch (Exception e) {
            throw new RuntimeException("解密计算出现异常", e);
        }
    }

    private static PublicKey getPublicKey(String publicKeyStr) {
        try {
            byte[] publicKeyBytes = SecretUtil.base64Decode(publicKeyStr);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "SunRsaSign");
            return keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        }
    }

}
