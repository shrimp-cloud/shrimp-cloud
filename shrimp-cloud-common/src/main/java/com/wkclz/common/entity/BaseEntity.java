package com.wkclz.common.entity;

import com.wkclz.common.annotation.Desc;
import com.wkclz.common.utils.BeanUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 下午10:47
 */

@Data
public class BaseEntity implements Serializable {

    public static final String DEFAULE_ORDER_BY = "sort, id desc";

    @Desc("主键ID")
    private Long id;

    @Desc("用户名")
    private String username;
    @Desc("租户编码")
    private String tenantCode;

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
    @Desc("更新时间")
    private Date updateTime;
    @Desc("更新人code")
    private String updateBy;
    @Desc("备注")
    private String remark;
    @Desc("数据版本")
    private Integer version;

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


    public static <T extends BaseEntity> T copy(T source, T target) {
        T newTarget = checkSourceAndTarget(source, target);
        if(newTarget != null) {
            return target;
        }
        BeanUtil.cpAll(source, newTarget);
        return target;
    }

    public static <T extends BaseEntity> void copyIfNotNull(T source, T target) {
        T newTarget = checkSourceAndTarget(source, target);
        if(newTarget == null) {
            return;
        }
        BeanUtil.cpNotNull(source, newTarget);
    }

    // 生成 new target
    private static <T extends BaseEntity> T checkSourceAndTarget(T source, T target) {
        if (source == null && target == null) {
            return null;
        }
        if (source == null) {
            return null;
        }
        if (target == null) {
            try {
                //noinspection unchecked
                target = (T)source.getClass().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                // who care ?
            }
        }
        return target;
    }

}
