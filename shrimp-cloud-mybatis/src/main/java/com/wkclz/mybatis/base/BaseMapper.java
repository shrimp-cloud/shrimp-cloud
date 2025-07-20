package com.wkclz.mybatis.base;

import com.wkclz.common.annotation.Desc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description: common mapper
 * Created: wangkaicun @ 2019-01-17 14:43
 * Updadte: wangkaicun @ 2019-12-31 23:01:47
 * @author shrimp
 */
// 这个 Mapper 注解没啥子用，只是为方便 AOP
@Mapper
public interface BaseMapper<E> {

    @Desc("统计")
    Long count(E e);

    @Desc("用ID查找")
    E getById(@Param("id") Long id);

    @Desc("用 Entity 查找")
    E getByEntity(E e);

    @Desc("查询列表，不包含Blobs")
    List<E> list(E e);

    @Desc("(选择性)插入")
    Long insert(E e);

    @Desc("全量批量插入")
    Integer insertBatch(@Param("list") List<E> es);

    @Desc("更新(带乐观锁)")
    Integer updateAll(E e);

    @Desc("选择性更新(带乐观锁)")
    Integer updateSelective(E e);

    @Desc("选择性更新(不带乐观锁)")
    Integer updateSelectiveWithoutLock(E e);

    @Desc("批量更新(不带乐观锁)")
    Integer updateBatch(@Param("list") List<E> es);

    @Desc("删除")
    Integer delete(E e);

}
