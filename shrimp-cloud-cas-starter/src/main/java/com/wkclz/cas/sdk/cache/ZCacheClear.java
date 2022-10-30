package com.wkclz.cas.sdk.cache;

import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.pojo.appinfo.App;
import com.wkclz.cas.sdk.pojo.appinfo.CacheRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ZCacheClear {
    private final static Logger logger = LoggerFactory.getLogger(ZCacheClear.class);

    private static Map<String, Long> CACHE_COMMAND = new HashMap<>();
    private static Long FIRST_TIME = null;

    @Autowired
    private AppInfoFacade appInfoFacade;
    @Autowired
    private AppInfoCache appInfoCache;
    @Autowired
    private DUserApiCache dUserApiCache;

    @Scheduled(fixedRate=5000)
    public void clearCache() {

        List<CacheRecord> cacheRecord = appInfoFacade.getCacheRecord();
        if (CollectionUtils.isEmpty(cacheRecord)) {
            return;
        }

        if (FIRST_TIME == null) {
            FIRST_TIME = System.currentTimeMillis();
        }
        long now = System.currentTimeMillis();

        List<CacheRecord> newRecord = new ArrayList<>();
        for (CacheRecord record : cacheRecord) {
            String key = record.getAppCode() + ":" + record.getCacheType();
            // 比第一次扫描还早，10 秒以上，不刷新。系统会自动加载
            if (FIRST_TIME - record.getCreateTime().getTime() > 10_000) {
                CACHE_COMMAND.put(key, FIRST_TIME);
                continue;
            }

            // 已经存在的旧指令，不刷新
            Long lastTime = CACHE_COMMAND.get(key);
            if (lastTime != null && lastTime < now) {
                continue;
            }

            // 不存在的新指令，或存在但有更新的指令，刷新
            CACHE_COMMAND.put(key, record.getCreateTime().getTime());
            newRecord.add(record);
        }
        if (CollectionUtils.isEmpty(newRecord)) {
            return;
        }

        // 变成 map 方便处理
        Map<String, List<String>> record = new HashMap<>();
        for (CacheRecord r : newRecord) {
            List<String> types = record.get(r.getAppCode());
            if (types == null) {
                types = new ArrayList<>();
            }
            types.add(r.getCacheType());
            record.put(r.getAppCode(), types);
        }

        for (Map.Entry<String, List<String>> entry : record.entrySet()) {
            String appCode = entry.getKey();
            String types = StringUtils.join(entry.getValue(), ",");

            logger.info("应用: {}, 缓存: {} 更新...", appCode, types);

            // AppInfo 的更新，无需关心类型
            appInfoCache.refresh(appCode, types);

            String[] typeArr = types.split(",");
            for (String type : typeArr) {
                if ("APP".equals(type)) {
                    List<App> apps = appInfoFacade.getApps();
                    if (CollectionUtils.isNotEmpty(apps)) {
                        BTenantDomainCache.setTenantDomains(apps);
                    }
                }
                if ("USER_ROLE".equals(type)) {
                    // 用户需要重新登录，不管
                    continue;
                }

                // 其他情况均需清空 userApi
                // ROLE,RES,API,ROLE_RES,RES_API
                dUserApiCache.invalidateAll();
            }

        }

    }

}
