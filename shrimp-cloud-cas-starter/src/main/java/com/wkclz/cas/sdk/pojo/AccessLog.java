package com.wkclz.cas.sdk.pojo;

public class AccessLog {

    /**
     * id
     */
    private Integer id;

    /**
     * 浏览器基本信息
     */
    private String userAgent;


    /**
     * 浏览器名称
     */
    private String browserName;


    /**
     * 浏览器名称
     */
    private String browserVersion;


    /**
     * 浏览器内核名称
     */
    private String engineName;


    /**
     * 浏览器内核名称
     */
    private String engineVersion;

    /**
     * 用户系统
     */
    private String userOs;

    /**
     * 用户平台
     */
    private String platform;

    /**
     * 服务器名称
     */
    private String osName;

    /**
     * 服务器系统版本
     */
    private String osVersion;

    /**
     * 服务器架构
     */
    private String osArch;

    /**
     * 请求协议
     */
    private String httpProtocol;

    /**
     * 请求编码
     */
    private String characterEncoding;

    /**
     * Accept
     */
    private String accept;

    /**
     * Accept-语言
     */
    private String acceptLanguage;

    /**
     * Accept-编码
     */
    private String acceptEncoding;

    /**
     * Connection
     */
    private String connection;

    /**
     * Cookie
     */
    private String cookie;

    /**
     * Origin
     */
    private String origin;

    /**
     * 引用页
     */
    private String referer;

    /**
     * 请求 URL
     */
    private String requestUrl;

    /**
     * 请求 URI
     */
    private String requestUri;

    /**
     * 查询内容
     */
    private String queryString;

    /**
     * 客户端地址
     */
    private String remoteAddr;

    /**
     * 客户端端口
     */
    private Integer remotePort;

    /**
     * 地区
     */
    private String location;

    /**
     * ISP运营商
     */
    private String isp;

    /**
     * 服务器地址
     */
    private String localAddr;

    /**
     * 服务器名称
     */
    private String localName;

    /**
     * 请求方式
     */
    private String method;

    /**
     *
     */
    private String serverName;

    /**
     * 用户token
     */
    private String token;

    /**
     * 认证
     */
    private Long authId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 租户编码
     */
    private String tenantCode;

    /**
     * 用户昵称
     */
    private String nickName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public String getUserOs() {
        return userOs;
    }

    public void setUserOs(String userOs) {
        this.userOs = userOs;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getHttpProtocol() {
        return httpProtocol;
    }

    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getLocalAddr() {
        return localAddr;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
