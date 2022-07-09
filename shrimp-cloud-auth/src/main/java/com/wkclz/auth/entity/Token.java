package com.wkclz.auth.entity;

import com.alibaba.fastjson2.JSON;
import com.wkclz.auth.helper.AliveTimeHelper;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.utils.SecretUtil;
import com.wkclz.spring.config.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;

public class Token {
    private static final Logger logger = LoggerFactory.getLogger(Token.class);

    public Token() {
    }

    public Token(Long authId, Long userId) {
        this.authId = authId;
        this.userId = userId;
        init();
    }

    public Token(Long authId, Long userId, String token) {
        this.authId = authId;
        this.userId = userId;
        this.token = token;
        init();
    }

    private void init() {
        if (this.token == null) {
            this.token = SecretUtil.getKey();
        }

        Integer sessionLiveTime = AliveTimeHelper.getSessionLiveTime();
        // 强制过期时间最小三天
        if (sessionLiveTime - (3 * 24 * 60 * 60) < 0) {
            sessionLiveTime = 3 * 24 * 60 * 60;
        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, sessionLiveTime);
        this.expireTime = calendar.getTime();
        this.sign = this.makeSign();
    }

    /**
     * Base64 编码
     */
    public String base64() {
        String jsonString = JSON.toJSONString(this);
        String base64Encode = SecretUtil.base64Encode(jsonString.getBytes());
        return base64Encode;
    }

    /**
     * 从 base64 中还原 token
     *
     * @param base64
     * @return
     */
    public static Token decodeToken(String base64) {
        try {
            String decode = URLDecoder.decode(base64, "UTF-8");
            byte[] base64Decode = SecretUtil.base64Decode(decode);
            Token token = JSON.parseObject(new String(base64Decode), Token.class);
            return token;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw BizException.error("token 解析失败:" + e.getMessage());
        }
    }

    public String contactRedisKey() {
        String redisKey = Sys.APPLICATION_GROUP + ":" + this.authId + ":" + this.userId + ":" + this.token;
        return redisKey;
    }


    /**
     * 产生 sign
     *
     * @return
     */
    public String makeSign() {
        StringBuffer sb = new StringBuffer();
        sb.append("authId-").append(authId);
        sb.append("token-").append(token);
        sb.append("expireTime-").append(expireTime.getTime());
        String sign = SecretUtil.md5(sb.toString());
        return sign;
    }

    /**
     * 验证 sign 是否合法
     *
     * @return
     */
    public boolean checkSign() {
        String sign = makeSign();
        boolean equals = sign.equals(this.getSign());
        return equals;
    }


    /**
     * 认证Id
     */
    private Long authId;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * token
     */
    private String token;

    /**
     * 强制过期时间
     */
    private Date expireTime;

    /**
     * 签名
     */
    private String sign;


    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


    @Override
    public String toString() {
        return "Token{" +
            "authId=" + authId +
            ", userId=" + userId +
            ", token='" + token + '\'' +
            ", expireTime=" + expireTime +
            ", sign='" + sign + '\'' +
            '}';
    }

}
