package com.wkclz.auth.entity;

import java.util.Date;

/**
 * Description: Create by lz-gen
 * @author: wangkaicun
 * @table: cas_user_auth (认证表，存储用户的认证信息) 重新生成代码会覆盖
 */

public class UserAuth {

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号（登录）
     */
    private String mobile;

    /**
     * 邮箱（登录）
     */
    private String email;

    /**
     * 微信openId
     */
    private String openId;

    /**
     * 微信公众平台unionId
     */
    private String unionId;

    /**
     * 密码（加密）
     */
    private String password;

    /**
     * 密码加密散列值
     */
    private String salt;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建者IP
     */
    private String createIp;

    /**
     * 登录成功次数
     */
    private Integer loginTimes;

    /**
     * 最后更新人IP
     */
    private String lastUpdateIp;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录失败时间
     */
    private Date lastFailedTime;

    /**
     * 最后登录失败IP
     */
    private String lastFailedIp;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCreateIp() {
        return createIp;
    }

    public void setCreateIp(String createIp) {
        this.createIp = createIp;
    }

    public Integer getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(Integer loginTimes) {
        this.loginTimes = loginTimes;
    }

    public String getLastUpdateIp() {
        return lastUpdateIp;
    }

    public void setLastUpdateIp(String lastUpdateIp) {
        this.lastUpdateIp = lastUpdateIp;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Date getLastFailedTime() {
        return lastFailedTime;
    }

    public void setLastFailedTime(Date lastFailedTime) {
        this.lastFailedTime = lastFailedTime;
    }

    public String getLastFailedIp() {
        return lastFailedIp;
    }

    public void setLastFailedIp(String lastFailedIp) {
        this.lastFailedIp = lastFailedIp;
    }
}

