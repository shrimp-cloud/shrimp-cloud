package com.wkclz.cas.sdk.cache;

import org.springframework.stereotype.Component;

@Component
public class CUserApiCacheImpl {

    public synchronized boolean get(String userCode, String uri, String mathod) {
        return true;
    }

    public void refresh(String appCode, String userCode, String uri, String mathod) {
    }

    public void invalidateAll() {
    }

}
