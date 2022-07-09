package com.wkclz.spring.helper;

import com.alibaba.fastjson2.JSON;
import com.wkclz.common.exception.BizException;
import com.wkclz.redis.entity.RedisMsgBody;
import com.wkclz.redis.topic.RedisTopicConfig;
import com.wkclz.spring.config.SpringContextHolder;
import com.wkclz.spring.config.SystemConfig;
import com.wkclz.spring.entity.Dict;
import com.wkclz.spring.entity.DictType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
public class DictHelper {

    private static List<DictType> DICT_TYPES = null;

    /**
     * 更新全部缓存
     */
    public static boolean reflash() {
        return reflash(DICT_TYPES);
    }
    public static boolean reflash(List<DictType> dictTypes) {
        if (CollectionUtils.isEmpty(dictTypes)) {
            throw BizException.error("dictTypes can not be null or empty!");
        }

        if (!SpringContextHolder.getBean(SystemConfig.class).isCloud()){
            DICT_TYPES = dictTypes;
            return true;
        }
        RedisMsgBody body = new RedisMsgBody();
        body.setTag(DictHelper.class.getName());
        body.setMsg(dictTypes);

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
            throw BizException.error("dictTypes can not be null or empty!");
        }
        List<DictType> dictTypes = JSON.parseArray(msg.toString(), DictType.class);
        return setLocal(dictTypes);
    }

    public static boolean setLocal(List<DictType> dictTypes) {
        if (CollectionUtils.isEmpty(dictTypes)) {
            throw BizException.error("dictTypes can not be null or empty!");
        }
        DICT_TYPES = dictTypes;
        return true;
    }

    public static List<DictType> getLocal() {
        return DICT_TYPES;
    }


    /**
     * 获取字典列表
     * @param dictType
     * @return
     */
    public static List<Dict> get(String dictType){
        if (StringUtils.isBlank(dictType)){
            return null;
        }
        if (DICT_TYPES == null){
            throw BizException.error("do not init dict yet, please wait!");
        }

        for (DictType type : DICT_TYPES) {
            if (dictType.equals(type.getDictType())){
                return type.getDicts();
            }
        }
        return null;
    }

    /**
     * 获取字典详情
     * @param dictType
     * @return
     */
    public static Dict get(String dictType, String dictKey){
        if (StringUtils.isBlank(dictKey)){
            return null;
        }
        List<Dict> dicts = get(dictType);
        if (CollectionUtils.isEmpty(dicts)) {
            return null;
        }

        for (Dict dict : dicts) {
            if (dictKey.equals(dict.getDictKey())){
                return dict;
            }
        }
        return null;
    }

    /**
     * 获取字典值
     * @param dictType
     * @param dictKey
     * @return
     */
    public static String getValue(String dictType, String dictKey){
        Dict dict = get(dictType, dictKey);
        if (dict == null){
            return null;
        }
        return dict.getDictValue();
    }
}











