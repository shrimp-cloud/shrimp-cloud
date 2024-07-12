//package com.wkclz.mybatis.base;
//
//import com.github.pagehelper.Page;
//import com.github.pagehelper.PageHelper;
//import com.wkclz.core.exception.BizException;
//import com.wkclz.core.util.BeanUtil;
//import com.wkclz.core.util.DateUtil;
//import com.wkclz.core.util.MapUtil;
//import com.wkclz.core.util.StringUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Description:
// * Created: wangkaicun @ 2018-01-21 下午7:46
// */
//public class BaseRepoHandler {
//
//    private static final Logger logger = LoggerFactory.getLogger(BaseRepoHandler.class);
//
//    protected static final int INSERT_SIZE = 1200;
//
//    private static Long getUserId() {
//        String userId = MDC.get("userId");
//        if (userId != null) {
//            return Long.valueOf(userId);
//        }
//        return null;
//    }
//
//
//    /**
//     * @param @param  obj
//     * @param @param  req
//     * @param @return 设定文件
//     * @throws
//     * @Title: 处理基本信息【for update】
//     * @Description:
//     * @author wangkaicun @ current time
//     */
//    protected static <T extends BaseEntity> T setBaseInfo(T entity) {
//
//        Long userId = getUserId();
//        if (userId != null) {
//            entity.setUpdateBy(userId);
//            if (entity.getId() == null && entity.getCreateBy() == null) {
//                entity.setCreateBy(userId);
//            }
//        }
//        // entity.setUpdateTime(new Date()); 让数据库做更新
//        return entity;
//    }
//
//
//    /**
//     * 分页预处理
//     *
//     * @param entity
//     */
//    public static <T extends BaseEntity> boolean pagePreHandle(T entity) {
//
//        BeanUtil.removeBlank(entity);
//
//        // 是否分页
//        boolean isPage = entity.getIsPage() == null || entity.getIsPage() == 1;
//        if (isPage) {
//            entity.init();
//            PageHelper.startPage(entity.getPageNo(), entity.getPageSize());
//        }
//
//        String orderBy = entity.getOrderBy();
//        // 注入风险检测
//        if (orderBy != null && !orderBy.equals(BaseEntity.DEFAULE_ORDER_BY) && sqlInj(orderBy)) {
//            throw BizException.error("orderBy 有注入风险，请谨慎操作！");
//        }
//
//        // 大小写处理
//        entity.setOrderBy(StringUtil.check2LowerCase(orderBy, "DESC"));
//        entity.setOrderBy(StringUtil.check2LowerCase(orderBy, "ASC"));
//        // 驼峰处理
//        entity.setOrderBy(StringUtil.camelToUnderline(orderBy));
//
//        // keyword 查询处理
//        if (StringUtils.isNotBlank(entity.getKeyword())) {
//            entity.setKeyword("%" + entity.getKeyword() + "%");
//        }
//        // 时间范围查询处理
//        DateUtil.formatDateRange(entity);
//        return isPage;
//    }
//
//    /**
//     * 分页查询处理，返回类型为 Object
//     *
//     * @param entity
//     * @param list
//     * @return
//     */
//    protected static <T extends BaseEntity> PageData<T> pageSelect(T entity, Object list) {
//
//        // 是否分页
//        boolean isPage = entity.getIsPage() == null || entity.getIsPage() == 1;
//
//        // pageData 用于重新整合数据
//        PageData<T> pageData = new PageData<>(entity.getPageNo(), entity.getPageSize());
//
//        // 是否分页
//        if (isPage) {
//            // 此处强转是为了拿到 Total
//            Page<T> listPage = (Page<T>) list;
//            pageData.setTotalCount(listPage.getTotal());
//        } else {
//            pageData.setPageNo(null);
//            pageData.setPageSize(null);
//            pageData.setTotalPage(null);
//        }
//
//        pageData.setRows((List<T>) list);
//
//        // 完成分页查询
//        return pageData;
//    }
//
//
//    /**
//     * 分页查询处理，返回类型为 Map
//     *
//     * @param entity
//     * @param list
//     * @return
//     */
//    protected static <T> PageData<Map<String, T>> pageSelect4Map(BaseEntity entity, List<Map<String, T>> list) {
//
//        // 是否分页
//        boolean isPage = entity.getIsPage() == null || entity.getIsPage() == 1;
//
//        // pageData 用于重新整合数据
//        PageData<Map<String, T>> pageData = new PageData<>(entity.getPageNo(), entity.getPageSize());
//
//        // 是否分页
//        if (isPage) {
//            // 此处强转是为了拿到 Total
//            Page<Map<String, T>> listPage = (Page<Map<String, T>>) list;
//            pageData.setTotalCount(listPage.getTotal());
//        } else {
//            pageData.setPageNo(null);
//            pageData.setPageSize(null);
//            pageData.setTotalPage(null);
//        }
//
//        // 驼峰转换
//        pageData.setRows(MapUtil.toReplaceKeyLow(list));
//
//        // 完成分页查询
//        return pageData;
//    }
//
//    protected static <M, T extends BaseEntity> PageData<T> ansyList2Page(T entity, List<M> list) {
//        boolean isPage = entity.getIsPage() == null || entity.getIsPage() == 1;
//        PageData<T> pageData = new PageData<>(entity.getPageNo(), entity.getPageSize());
//        // 是否分页
//        if (isPage) {
//            // 转换为pageHelper的分页对象来提取分页信息
//            Page<M> listPage = (Page<M>) list;
//            pageData.setTotalCount(listPage.getTotal());
//        } else {
//            pageData.setPageNo(null);
//            pageData.setPageSize(null);
//        }
//        return pageData;
//    }
//
//    /**
//     * 获取Map 类型的 columns
//     *
//     * @param columns 列名
//     * @return
//     */
//    protected static Map<String, String> getMapColumns(List<String> columns) {
//        if (columns == null || columns.isEmpty()) {
//            return null;
//        }
//        Map<String, String> columnsMap = new HashMap<>();
//        columns.forEach(column -> {
//            String columnUnderLine = StringUtil.camelToUnderline(column);
//            String columnCamel = StringUtil.underlineToCamel(columnUnderLine);
//            columnsMap.put(columnCamel, columnCamel);
//        });
//        return columnsMap;
//    }
//
//    /**
//     * 插入 ElasticSearch
//     *
//     * @param client
//     * @param table
//     * @param obj
//     */
//    /*
//    protected static <T> void insertEs(RestHighLevelClient client, String table, T obj) {
//        if (obj == null) {
//            throw BizException.error("entity is null");
//        }
//        IndexRequest indexRequest = new IndexRequest(table, "doc");
//        String jsonString = JSONObject.toJSONString(obj);
//        indexRequest.source(jsonString, XContentType.JSON);
//        try {
//            client.index(indexRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            // who care ?
//        }
//    }
//    */
//
//
//
//}
