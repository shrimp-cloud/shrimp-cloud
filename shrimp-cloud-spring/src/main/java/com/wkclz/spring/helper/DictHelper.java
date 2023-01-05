package com.wkclz.spring.helper;

import com.alibaba.fastjson2.JSON;
import com.wkclz.common.exception.BizException;
import com.wkclz.redis.entity.RedisMsgBody;
import com.wkclz.redis.topic.RedisTopicConfig;
import com.wkclz.spring.config.SpringContextHolder;
import com.wkclz.spring.config.SystemConfig;
import com.wkclz.spring.entity.Dict;
import com.wkclz.spring.entity.DictItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
public class DictHelper {

    private static List<Dict> DICTS = null;

    /**
     * 更新全部缓存
     */
    public static boolean reflash() {
        return reflash(DICTS);
    }
    public static boolean reflash(List<Dict> dicts) {
        if (CollectionUtils.isEmpty(dicts)) {
            throw BizException.error("dictTypes can not be null or empty!");
        }

        if (!SpringContextHolder.getBean(SystemConfig.class).isCloud()){
            DICTS = dicts;
            return true;
        }
        RedisMsgBody body = new RedisMsgBody();
        body.setTag(DictHelper.class.getName());
        body.setMsg(dicts);

        String msg = JSON.toJSONString(body);
        StringRedisTemplate stringRedisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        stringRedisTemplate.convertAndSend(SpringContextHolder.getBean(RedisTopicConfig.class).getCacheTopic(), msg);
        return true;
    }


    /**
     * 初始化 dictTypes 【仅给队列调用，不允许直接调用】
     */
    public static boolean setLocal(Object msg) {
        if (msg == null) {
            throw BizException.error("dicts can not be null or empty!");
        }
        List<Dict> dicts = JSON.parseArray(msg.toString(), Dict.class);
        return setLocal(dicts);
    }

    public static boolean setLocal(List<Dict> dicts) {
        if (CollectionUtils.isEmpty(dicts)) {
            throw BizException.error("dicts can not be null or empty!");
        }
        DICTS = dicts;
        return true;
    }

    public static List<Dict> getLocal() {
        return DICTS;
    }


    /**
     * 获取字典列表
     * @param dictType
     * @return
     */
    public static List<DictItem> get(String dictType){
        if (StringUtils.isBlank(dictType)){
            return null;
        }
        if (DICTS == null){
            throw BizException.error("do not init dict yet, please wait!");
        }

        for (Dict type : DICTS) {
            if (dictType.equals(type.getDictType())){
                return type.getItems();
            }
        }
        return null;
    }

    /**
     * 获取字典详情
     * @param dictType
     * @param dictValue
     * @return
     */
    public static DictItem get(String dictType, String dictValue){
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictValue)){
            return null;
        }
        List<DictItem> dicts = get(dictType);
        if (CollectionUtils.isEmpty(dicts)) {
            return null;
        }

        for (DictItem item : dicts) {
            if (dictValue.equals(item.getDictValue())){
                return item;
            }
        }
        return null;
    }

    /**
     * 获取字典值
     * @param dict
     * @param dictKey
     * @return
     */
    public static String getValue(String dict, String dictKey){
        DictItem item = get(dict, dictKey);
        if (item == null){
            return null;
        }
        return item.getDictValue();
    }
}











