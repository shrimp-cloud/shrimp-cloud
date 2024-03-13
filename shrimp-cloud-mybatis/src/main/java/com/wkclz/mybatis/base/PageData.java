package com.wkclz.mybatis.base;

import com.wkclz.common.entity.BaseEntity;

import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2017-11-12 上午12:10
 */
public class PageData<T> {

    private List<T> rows;


    private Long current = 1L;
    private Long size = 10L;
    private Long total;
    private Long page;
    private Long offset = 0L;

    public PageData() {
        init();
    }

    public <M extends BaseEntity> PageData(M entity) {
        this.current = entity.getCurrent();
        this.size = entity.getSize();
        if (this.current != null && this.size != null){
            this.offset = (this.current -1) * this.size;
        }
    }

    public PageData(Long current, Long size) {
        this.current = current;
        this.size = size;
        if (this.current != null && this.size != null){
            this.offset = (this.current -1) * this.size;
        }
    }

    public PageData(Long current, Long size, Long total, List<T> list) {
        this.current = current;
        this.size = size;
        this.total = total;
        init();
        this.rows = list;
    }

    /**
     * 自定义辅助类 分页
     * 使用查询数据源构造新分页参数
     */
    public PageData(PageData oldPageData, List<T> pageList) {
        this.current = oldPageData.getCurrent();
        this.size = oldPageData.getSize();
        this.total = oldPageData.getTotal();
        this.page = oldPageData.getPage();
        this.offset = oldPageData.getOffset();
        this.rows = pageList;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> data) {
        this.rows = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
        init(); // 只有设置了总数据数的时候才做分页处理
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
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

    private void init() {
        if (this.current == null || this.current < 1) {
            this.current = 1L;
        }
        if (this.size == null || this.size < 1) {
            this.size = 10L;
        }
        if (this.total == null) {
            this.total = 0L;
        }

        this.page = this.total / this.size;
        long remainder = this.total % this.size;
        if (remainder > 0) {
            this.page = this.page + 1;
        }
        this.current = this.current > this.page ? this.page : this.current;
        this.offset = (this.current -1 ) * this.size;
        /*
        this.url = "?current="+current+"&size="+size;
        this.prevUrl = "?current="+(this.current > 1 ? this.current - 1 : 1)+"&size="+size;
        this.nextUrl = "?current="+(this.current.equals(this.page) ? this.current : this.current + 1)+"&size="+this.size;
        */
    }
}
