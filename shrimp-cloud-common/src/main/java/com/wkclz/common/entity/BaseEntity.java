package com.wkclz.common.entity;

import com.wkclz.common.annotation.Desc;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 下午10:47
 */
public class BaseEntity {

    public static final String DEFAULE_ORDER_BY = "sort, id desc";

    @Desc("主键ID")
    private Long id;

    @Desc("用户编码")
    private String userCode;
    @Desc("租户ID")
    private Long tenantId;


    /**
     * 查询辅助
     */
    @Desc("分页页码")
    private Long current;
    @Desc("分页大小")
    private Long size;
    @Desc("偏移量")
    private Long offset;
    @Desc("总数据量")
    protected Long total;

    @Desc("查询排序规则")
    private String orderBy;
    @Desc("统计数")
    private Long count;

    /**
     * 查询辅助
     */
    @Desc("主键ID数组")
    private List<Long> ids;
    private String keyword;
    @Desc("创建时间从")
    private Date timeFrom;
    @Desc("创建时间到")
    private Date timeTo;

    /**
     * 数据库规范字段
     */
    @Desc("排序号，越大越往后")
    private Integer sort;
    @Desc("创建时间")
    private Date createTime;
    @Desc("创建人code")
    private String createBy;
    @Desc("创建人昵称")
    private String createByName;
    @Desc("更新时间")
    private Date updateTime;
    @Desc("更新人code")
    private String updateBy;
    @Desc("更新人昵称")
    private String updateByName;
    @Desc("备注")
    private String comments;
    @Desc("数据版本")
    private Integer version;
    @Desc("数据状态:0(已删除),1(有效)")
    private Integer status;

    private Integer debug;

    public void init() {
        if (this.current == null || this.current < 1) {
            this.current = 1L;
        }
        if (this.size == null || this.size < 1) {
            this.size = 10L;
        }
        this.offset = (this.current -1 ) * this.size;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Date getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateByName() {
        return updateByName;
    }

    public void setUpdateByName(String updateByName) {
        this.updateByName = updateByName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDebug() {
        return debug;
    }

    public void setDebug(Integer debug) {
        this.debug = debug;
    }
}
