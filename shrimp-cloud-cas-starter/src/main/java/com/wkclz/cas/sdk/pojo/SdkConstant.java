package com.wkclz.cas.sdk.pojo;

import java.util.Arrays;
import java.util.List;

public interface SdkConstant {

    String HEADER_TOKEN_NAME = "token";
    String HEADER_APP_CODE = "app-code";
    String HEADER_TENANT_CODE = "tenant-code";

    String USER_INFO_USER_NAME = "username";
    String USER_INFO_USER_CODE = "userCode";

    // 特殊场景-微信登录
    String USER_INFO_OPEN_ID = "openId";


    // 资源缓存变量
    String APP = "APP";
    String ROLE = "ROLE";
    String RES = "RES";
    String API = "API";
    String ROLE_RES = "ROLE_RES";
    String RES_API = "RES_API";
    String ACCESS_TOKEN = "ACCESS_TOKEN";
    List<String> TOTAL_CACKE = Arrays.asList(APP, ROLE, RES, API, ROLE_RES, RES_API, ACCESS_TOKEN);

    // 【不属于资源类型，单独处理】
    String USER_ROLE = "USER_ROLE";


}
