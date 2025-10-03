package com.wkclz.mybatis.plugins.helper;

import com.wkclz.common.entity.BaseEntity;
import com.wkclz.common.entity.FieldInfo;
import com.wkclz.common.exception.SysException;
import com.wkclz.common.exception.UserException;
import com.wkclz.common.utils.BeanUtil;
import com.wkclz.common.utils.StringUtil;
import com.wkclz.mybatis.bean.ColumnInfo;
import com.wkclz.mybatis.bean.ColumnQuery;
import com.wkclz.mybatis.config.ShrimpMyBatisConfig;
import com.wkclz.mybatis.dao.TableInfoMapper;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author shrimp
 */
@Component
public class LengthCheckHelper {

    private final static Map<Class<?>, Map<String, ColumnInfo>> COLLUMN_MAP = new HashMap<>();

    @Resource
    private ShrimpMyBatisConfig config;
    @Resource
    private TableInfoMapper tableInfoMapper;

    public synchronized <T extends BaseEntity> void handleCheck(T entity) {
        if (entity == null) {
            return;
        }

        // 全局开关
        Integer dataLengthCheck = config.getDataLengthCheck();
        if (dataLengthCheck != null && dataLengthCheck != 1) {
            return;
        }

        Map<String, ColumnInfo> columns = COLLUMN_MAP.get(entity.getClass());
        if (columns == null) {
            CompletableFuture<Map<String, ColumnInfo>> future = CompletableFuture.supplyAsync(() -> {
                String tableName = getTableName(entity.getClass());
                String tableSchema = config.getTableSchema();
                ColumnQuery query = new ColumnQuery();
                query.setTableSchema(tableSchema);
                query.setTableName(tableName);
                List<ColumnInfo> cs = tableInfoMapper.getColumnLengthList(query);
                Map<String, ColumnInfo> map;
                if (CollectionUtils.isEmpty(cs)) {
                    map = new HashMap<>();
                } else {
                    map = cs.stream().collect(Collectors.toMap(
                        t -> StringUtil.underlineToCamel(t.getColumnName()),
                        t -> t
                    ));
                }
                return map;
            });
            try {
                columns = future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw SysException.error(e.getMessage());
            } catch (ExecutionException e) {
                throw SysException.error(e.getMessage());
            }
            COLLUMN_MAP.put(entity.getClass(), columns);
        }

        // 不是现有的表对象，或无任何字段信息
        if (columns.isEmpty()) {
            return;
        }

        Map<String, FieldInfo> getters = BeanUtil.getGetters(entity.getClass());
        if (getters.isEmpty()) {
            return;
        }
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            String key = entry.getKey();
            ColumnInfo column = entry.getValue();
            FieldInfo fieldInfo = getters.get(key);
            if (fieldInfo == null) {
                return;
            }

            Method getter = fieldInfo.getGetter();
            Object v;
            try {
                v = getter.invoke(entity);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw SysException.error("无法执行 getter 方法: {}", e.getMessage());
            }
            if (v == null) {
                continue;
            }
            int length = v.toString().length();
            if (length > column.getLength()) {
                throw UserException.error(
                    "字段【{}:{}】的长度最大限制为【{}】，内容【{}】的长度为：{}, 已超过了限制!",
                    key, column.getColumnComment(), column.getLength(), v, length);
            }
        }
    }


    private static String getTableName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        simpleName = StringUtil.firstChatToLowerCase(simpleName);
        return StringUtil.camelToUnderline(simpleName);
    }

}
