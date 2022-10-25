package com.wkclz.cas.sdk.pojo.appinfo;

import java.util.List;

public class AppInfo {

    private App app;
    private List<Role> roles;
    private List<Res> reses;
    private List<Api> apis;

    private List<RoleRes> roleReses;
    private List<ResApi> resApis;

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Res> getReses() {
        return reses;
    }

    public void setReses(List<Res> reses) {
        this.reses = reses;
    }

    public List<Api> getApis() {
        return apis;
    }

    public void setApis(List<Api> apis) {
        this.apis = apis;
    }

    public List<RoleRes> getRoleReses() {
        return roleReses;
    }

    public void setRoleReses(List<RoleRes> roleReses) {
        this.roleReses = roleReses;
    }

    public List<ResApi> getResApis() {
        return resApis;
    }

    public void setResApis(List<ResApi> resApis) {
        this.resApis = resApis;
    }
}
