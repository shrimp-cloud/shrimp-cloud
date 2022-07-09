package com.wkclz.auth.entity;


import java.util.List;
import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 上午2:29
 */
public class User {

    /**
     * token
     */
    private String token;

    /**
     * 当前 ip
     */
    private String ip;

    /**
     * 终端
     */
    private String terminal;

    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 认证id
     */
    private Long authId;

    /**
     * 父id 【子账号，邀请账号】
     */
    private Long pid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录次数
     */
    private Integer loginTimes;

    /**
     * 积分
     */
    private Integer point;

    /**
     * 用户
     */
    private UserInfo userInfo;

    /**
     * 账号
     */
    private UserAuth userAuth;

    /**
     * 用户扩展属性
     */
    private Map<String, Object> userProperties;

    /**
     * 管理id
     */
    private List<Long> adminIds;

    /**
     * 角色列表
     */
    private List<String> roles;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(Integer loginTimes) {
        this.loginTimes = loginTimes;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserAuth getUserAuth() {
        return userAuth;
    }

    public void setUserAuth(UserAuth userAuth) {
        this.userAuth = userAuth;
    }

    public Map<String, Object> getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(Map<String, Object> userProperties) {
        this.userProperties = userProperties;
    }

    public List<Long> getAdminIds() {
        return adminIds;
    }

    public void setAdminIds(List<Long> adminIds) {
        this.adminIds = adminIds;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
