package com.wkclz.mybatis.base;

import com.wkclz.common.entity.BaseEntity;

import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2017-11-12 上午12:10
 */
public class PageData<T> {

    private List<T> rows;
    private Integer totalCount;
    private Integer totalPage;
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private Integer offset = 0;

    public PageData() {
        init();
    }

    public <M extends BaseEntity> PageData(M entity) {
        this.pageNo = entity.getPageNo();
        this.pageSize = entity.getPageSize();
        if (this.pageNo != null && this.pageSize != null){
            this.offset = (this.pageNo -1) * this.pageSize;
        }
    }

    public PageData(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        if (this.pageNo != null && this.pageSize != null){
            this.offset = (this.pageNo -1) * this.pageSize;
        }
    }

    public PageData(Integer pageNo, Integer pageSize, Integer totalCount, List<T> list) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        init();
        this.rows = list;
    }

    /**
     * 自定义辅助类 分页
     * 使用查询数据源构造新分页参数
     */
    public PageData(PageData oldPageData, List<T> pageList) {
        this.pageNo = oldPageData.getPageNo();
        this.pageSize = oldPageData.getPageSize();
        this.totalCount = oldPageData.getTotalCount();
        this.totalPage = oldPageData.getTotalPage();
        this.offset = oldPageData.getOffset();
        this.rows = pageList;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> data) {
        this.rows = data;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        init(); // 只有设置了总数据数的时候才做分页处理
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    private void init() {
        if (this.pageNo == null || this.pageNo < 1) {
            this.pageNo = 1;
        }
        if (this.pageSize == null || this.pageSize < 1) {
            this.pageSize = 10;
        }
        if (this.totalCount == null) {
            this.totalCount = 0;
        }

        this.totalPage = (int) Math.ceil((double) this.totalCount / (double) this.pageSize);
        if (this.totalPage == 0) {
            this.totalPage = 1;
        }
        this.pageNo = this.pageNo > this.totalPage ? this.totalPage : this.pageNo;
        this.offset = (this.pageNo -1 ) * this.pageSize;
        /*
        this.url = "?pageNo="+pageNo+"&pageSize="+pageSize;
        this.prevUrl = "?pageNo="+(this.pageNo > 1 ? this.pageNo - 1 : 1)+"&pageSize="+pageSize;
        this.nextUrl = "?pageNo="+(this.pageNo.equals(this.totalPage) ? this.pageNo : this.pageNo + 1)+"&pageSize="+this.pageSize;
        */
    }
}
