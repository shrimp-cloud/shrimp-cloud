package com.wkclz.cas.sdk.cache;

import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.helper.AuthHelper;
import com.wkclz.common.utils.SecretUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class BUserRoleCacheImpl {

    private static Map<String, List<String>> USER_ROLES = new ConcurrentHashMap<>();

    @Autowired
    private AppInfoFacade appInfoFacade;
    @Autowired
    private AuthHelper authHelper;

    public synchronized List<String> get() {
        String token = authHelper.getToken(true);
        String tokenHash = SecretUtil.md5(token);
        List<String> roles = USER_ROLES.get(tokenHash);
        if (roles == null) {
            String userCode = authHelper.getUserCode();
            List<String> userRoles = appInfoFacade.getUserRoles(userCode);
            if (userRoles == null) {
                userRoles = new ArrayList<>();
            }
            USER_ROLES.put(tokenHash, userRoles);
        }
        return USER_ROLES.get(tokenHash);
    }
}
