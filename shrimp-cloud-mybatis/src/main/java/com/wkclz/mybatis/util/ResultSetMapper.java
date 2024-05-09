package com.wkclz.mybatis.util;

import com.wkclz.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ResultSetMapper {
    private static final Logger logger = LoggerFactory.getLogger(ResultSetMapper.class);


    public static List<LinkedHashMap> toMapList(ResultSet rs) {
        List list = new ArrayList();
        try {
            // 获取数据库表结构
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                // 循环获取指定行的每一列的信息
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    // 当前列名
                    String colName = meta.getColumnLabel(i);
                    colName = StringUtil.underlineToCamel(colName);
                    Object value = rs.getObject(i);
                    if (value != null) {
                        map.put(colName, value);
                    }
                }
                list.add(map);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            // do nothing
        }
        return list;
    }


}
