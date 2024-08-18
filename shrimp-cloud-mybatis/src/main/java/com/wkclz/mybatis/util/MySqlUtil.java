package com.wkclz.mybatis.util;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.fastjson2.JSONObject;
import com.wkclz.common.exception.BizException;
import com.wkclz.mybatis.bean.ColumnInfo;
import com.wkclz.mybatis.bean.KeyInfo;
import com.wkclz.mybatis.bean.TableInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MySqlUtil {

    public static TableInfo getTableInfo(String ddl) {
        MySqlCreateTableStatement tableStatement = getTableByCreateTableDdl(ddl);
        TableInfo tableInfo = new TableInfo();

        // 数据库类型
        tableInfo.setDbType(tableStatement.getDbType().name());
        // 表名
        tableInfo.setTableName(tableStatement.getTableName().trim().replace("`", ""));
        // 备注信息
        SQLExpr sqlExpr = tableStatement.getComment();
        if (sqlExpr instanceof SQLCharExpr sqlCharExpr) {
            tableInfo.setComment(sqlCharExpr.getText());
        }
        // 表 options 信息
        List<SQLAssignItem> tableOptions = tableStatement.getTableOptions();
        for (SQLAssignItem tableOption : tableOptions) {
            SQLExpr target = tableOption.getTarget();
            SQLExpr value = tableOption.getValue();

            if (target instanceof SQLIdentifierExpr expr) {
                String name = expr.getName();
                if ("ENGINE".equalsIgnoreCase(name)) {
                    if (value instanceof SQLIdentifierExpr v) {
                        tableInfo.setEngine(v.getName());
                    }
                }
                if ("AUTO_INCREMENT".equalsIgnoreCase(name)) {
                    if (value instanceof SQLIntegerExpr v) {
                        tableInfo.setAutoIncrement(v.getNumber());
                    }
                }
                if ("CHARSET".equalsIgnoreCase(name)) {
                    if (value instanceof SQLIdentifierExpr v) {
                        tableInfo.setCharset(v.getName());
                    }
                }
                if ("COLLATE".equalsIgnoreCase(name)) {
                    if (value instanceof SQLIdentifierExpr v) {
                        tableInfo.setCollate(v.getName());
                    }
                }
            }
        }

        // 字段信息
        List<SQLTableElement> elements = tableStatement.getTableElementList();
        List<ColumnInfo> columns = new ArrayList<>();
        List<KeyInfo> keys = new ArrayList<>();
        tableInfo.setColumns(columns);
        tableInfo.setKeys(keys);
        for (SQLTableElement element : elements) {
            if (element instanceof SQLColumnDefinition e) {
                ColumnInfo column = new ColumnInfo();
                columns.add(column);
                column.setAutoIncrement(e.isAutoIncrement() ? true : null);
                column.setColumnName(e.getColumnName().trim().replace("`", ""));
                SQLExpr comment = e.getComment();
                if (comment instanceof SQLCharExpr sqlCharExpr) {
                    column.setComment(sqlCharExpr.getText());
                }

                if (e.getDefaultExpr() != null) {
                    if (e.getDefaultExpr() instanceof SQLCharExpr t) {
                        column.setDefaultValue(t.getValue());
                    }
                    if (e.getDefaultExpr() instanceof SQLCurrentTimeExpr t) {
                        column.setDefaultValue(t.getType().name);
                    }
                }
                if (e.getOnUpdate() != null) {
                    if (e.getOnUpdate() instanceof SQLCurrentTimeExpr t) {
                        column.setOnUpdate(t.getType().name);
                    }
                }

                List<SQLColumnConstraint> constraints = e.getConstraints();
                if (CollectionUtils.isNotEmpty(constraints)) {
                    for (SQLColumnConstraint constraint : constraints) {
                        if (constraint instanceof SQLNotNullConstraint) {
                            column.setNotNull(true);
                        } else {
                            System.out.println(constraint);
                        }
                    }
                }

                SQLDataType dataType = e.getDataType();
                column.setDataType(dataType.getName());
                List<SQLExpr> arguments = dataType.getArguments();
                if (CollectionUtils.isNotEmpty(arguments)) {
                    SQLExpr sqlExpr1 = arguments.get(0);
                    if (sqlExpr1 instanceof SQLIntegerExpr s1) {
                        column.setLength(s1.getNumber());
                    } else {
                        System.out.println(sqlExpr1);
                    }
                }
                if (dataType instanceof SQLCharacterDataType type) {
                    column.setCharset(type.getCharSetName());
                    column.setCollate(type.getCollate());
                } else if (dataType instanceof SQLDataTypeImpl t) {
                    column.setUnsigned(t.isUnsigned() ? true : null);
                } else {
                    System.out.println(dataType);
                }
            }

            if (element instanceof MySqlKey k) {
                KeyInfo key = new KeyInfo();
                keys.add(key);
                key.setIndexType(k.getIndexType());
                SQLIndexDefinition indexDefinition = k.getIndexDefinition();
                key.setType(indexDefinition.getType());
                SQLName name = indexDefinition.getName();
                if (name != null) {
                    key.setName(name.getSimpleName().trim().replace("`", ""));
                }

                List<SQLSelectOrderByItem> keyColumns = indexDefinition.getColumns();
                if (CollectionUtils.isNotEmpty(keyColumns)) {
                    List<ColumnInfo> indexColumns = new ArrayList<>();
                    key.setColumns(indexColumns);
                    for (SQLSelectOrderByItem keyColumn : keyColumns) {
                        ColumnInfo idxColumn = new ColumnInfo();
                        indexColumns.add(idxColumn);
                        SQLExpr expr = keyColumn.getExpr();
                        if (expr instanceof SQLIdentifierExpr t) {
                            idxColumn.setColumnName(t.getName().trim().replace("`", ""));
                        } else {
                            System.out.println(keyColumn);
                        }
                    }
                    if (key.getName() == null) {
                        key.setName(indexColumns.get(0).getColumnName());
                    }
                }
            }
        }

        return tableInfo;
    }


    private static MySqlCreateTableStatement getTableByCreateTableDdl(String ddl) {
        if (StringUtils.isBlank(ddl)) {
            throw BizException.error("table create ddl can not be null");
        }
        List<SQLStatement> statements = SQLUtils.parseStatements(ddl, "mysql");
        if (CollectionUtils.isEmpty(statements)) {
            throw BizException.error("table create ddl can not be null");
        }
        if (statements.size() > 1) {
            throw BizException.error("table create ddl contant more than one table");
        }

        SQLStatement sqlStatement = statements.get(0);
        if (sqlStatement instanceof MySqlCreateTableStatement createTableStatement) {
            return createTableStatement;
        }

        throw BizException.error("table create ddl is not a create table ddl!");
    }



    public static void main(String[] args) {
        String demoDdl = getDemoDdl();
        TableInfo tableInfo = getTableInfo(demoDdl);
        String jsonString = JSONObject.toJSONString(tableInfo);
        System.out.println(jsonString);
    }



    private static String getDemoDdl() {
        return """
            CREATE TABLE `auth_api` (
                `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                `module` varchar(31) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '模块',
                `app_code` varchar(31) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '应用编码',
                `api_code` varchar(31) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '路由映射编码',
                `api_method` varchar(15) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '路由映射方法',
                `api_uri` varchar(127) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '路由映射URI',
                `api_name` varchar(127) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '路由映射名称',
                `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                `create_by` varchar(31) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '创建人',
                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                `update_by` varchar(31) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '更新人',
                `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '备注',
                `version` int NOT NULL DEFAULT '0' COMMENT '版本号',
                `status` bigint unsigned NOT NULL DEFAULT '1' COMMENT 'status',
                PRIMARY KEY (`id`) USING BTREE,
                KEY `app_code` (`app_code`) USING BTREE,
                KEY `mapping_code` (`api_code`) USING BTREE,
                KEY `mapping_uri` (`api_uri`) USING BTREE,
                KEY `mapping_name` (`api_name`) USING BTREE
              ) ENGINE=InnoDB AUTO_INCREMENT=394 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci COMMENT='路由映射';
             """;
    }

}
