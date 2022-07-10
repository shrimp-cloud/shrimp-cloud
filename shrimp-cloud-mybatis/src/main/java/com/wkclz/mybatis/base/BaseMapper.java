package com.wkclz.mybatis.base;

import com.wkclz.common.annotation.Desc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description: common mapper
 * Created: wangkaicun @ 2019-01-17 14:43
 * Updadte: wangkaicun @ 2019-12-31 23:01:47
 */
// 这个 Mapper 注解没啥子用，只是为方便 AOP
@Mapper
public interface BaseMapper<Entity> {

    @Desc("统计")
    Integer count(Entity entity);

    @Desc("用ID查找")
    Entity getById(@Param("id") Long id);

    @Desc("用 Entity 查找")
    Entity getByEntity(Entity entity);

    @Desc("查询列表，不包含Blobs")
    List<Entity> list(Entity entity);

    @Desc("(选择性)插入")
    Long insert(Entity entity);

    @Desc("全量批量插入")
    Integer insertBatch(@Param("list") List<Entity> entitys);

    @Desc("更新(带乐观锁)")
    Integer updateAll(Entity entity);

    @Desc("选择性更新(带乐观锁)")
    Integer updateSelective(Entity entity);

    @Desc("选择性更新(不带乐观锁)")
    Integer updateSelectiveWithoutLock(Entity entity);

    @Desc("批量更新(不带乐观锁)")
    Integer updateBatch(@Param("list") List<Entity> entitys);

    @Desc("删除")
    Integer delete(Entity entity);

}
