package com.wkclz.mybatis.helper;

import com.wkclz.common.exception.BizException;
import com.wkclz.spring.config.SpringContextHolder;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SqlHelper {

    private final static Pattern OBJ_PATTERN = Pattern.compile("#\\{.*?}");
    private final static  Pattern LIST_PATTERN = Pattern.compile("collection=([\"|']).*?[\"|']");

    /**
     * 从 sql 中提取参数集
     */
    public static List<String> getParams(String sql) {
        List<String> params = new ArrayList<>();
        if (StringUtils.isBlank(sql)) {
            return params;
        }
        List<String> objParams = getObjParams(sql);
        List<String> listParams = getListParams(sql);

        params.addAll(objParams);
        params.addAll(listParams);
        return params;
    }

    private static List<String> getObjParams(String sql) {
        Matcher matcher = OBJ_PATTERN.matcher(sql);
        // 遍历所有匹配的结果
        List<String> params = new ArrayList<>();
        while (matcher.find()) {
            // 提取匹配的数字
            String rt = matcher.group();
            rt = rt.substring(2, rt.length() -1);
            params.add(rt);
        }
        return params;
    }
    private static List<String> getListParams(String sql) {
        Matcher matcher = LIST_PATTERN.matcher(sql);
        // 遍历所有匹配的结果
        List<String> params = new ArrayList<>();
        while (matcher.find()) {
            // 提取匹配的数字
            String rt = matcher.group();
            rt = rt.substring(12, rt.length() -1);
            params.add(rt);
        }
        return params;
    }


    /**
     * 从 sql 中提取结果集
     */
    public static List<String> getResults(String sql, Map<String, Object> map) {
        List<String> results = new ArrayList<>();
        if (StringUtils.isBlank(sql)) {
            return results;
        }
        if (map == null) {
            map = new HashMap<>();
        }

        String statementStr = MyBatisHelper.reloadSql(sql);
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSession.class);
        Configuration configuration = sqlSession.getConfiguration();
        MappedStatement mappedStatement = configuration.getMappedStatement(statementStr);
        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(map);

        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(boundSql.getSql());
        } catch (JSQLParserException e) {
            throw BizException.error("sql 解析异常: " + e.getMessage());
        }

        if (!(statement instanceof PlainSelect plainSelect)) {
            throw BizException.error("此 sql 语句不是 select 语句，请修正全重试！");
        }

        List<SelectItem<?>> selectItems = plainSelect.getSelectItems();
        for (SelectItem<?> selectItem : selectItems) {
            // 别名
            Alias alias = selectItem.getAlias();
            if (alias != null) {
                results.add(alias.getName());
                continue;
            }
            // 字段
            Expression expression = selectItem.getExpression();
            if (expression instanceof Column column) {
                results.add(column.getColumnName());
                continue;
            }
            // 无法识别
            results.add(selectItem.toString());
        }
        // 处理多余的信息
        results = results.stream().map(t -> {
            t = t.trim();
            t = t.replace("`", "");
            return t;
        }).collect(Collectors.toList());
        return results;
    }

}
