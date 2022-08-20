package com.wkclz.mybatis.base;

import com.sun.istack.NotNull;
import com.wkclz.common.annotation.Desc;
import com.wkclz.common.emuns.ResultStatus;
import com.wkclz.common.entity.BaseEntity;
import com.wkclz.common.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2019-01-17 15:22:38
 * Updadte: wangkaicun @ 2019-12-31 23:39:46
 */
public class BaseService<Entity extends BaseEntity, Mapper extends BaseMapper<Entity>> {

    private final static int INSERT_SIZE = 1000;

    @Autowired
    protected Mapper mapper;

    @Desc("统计")
    public Long count(@NotNull Entity entity){
        return mapper.count(entity);
    }

    @Desc("用ID查找")
    public Entity get(@NotNull Long id){
        return mapper.getById(id);
    }

    @Desc("用 Entity 查找")
    public Entity get(@NotNull Entity entity){
        return mapper.getByEntity(entity);
    }

    @Desc("查询列表，不包含Blobs")
    public List<Entity> list(@NotNull Entity entity){
        return mapper.list(entity);
    }

    @Desc("查询列分页，不包含Blobs")
    public PageData<Entity> page(@NotNull Entity entity){
        entity.init();
        Long count = mapper.count(entity);
        List<Entity> list = null;
        if (count > 0){
            list = mapper.list(entity);
        }
        if (list == null){
            list = new ArrayList<>();
        }
        PageData<Entity> pageData = new PageData<>(entity.getCurrent(), entity.getSize(), count, list);
        return pageData;
    }

    @Desc("(选择性)插入")
    public Long insert(@NotNull Entity entity){
        mapper.insert(entity);
        return entity.getId();
    }

    @Desc("全量批量插入")
    public Integer insert(@NotNull List<Entity> entitys){
        if (CollectionUtils.isEmpty(entitys)) {
            return 0;
        }
        int size = entitys.size();
        int counter = 0;
        int success = 0;
        List<Entity> tmpList = new ArrayList<>();
        for (Entity entity : entitys) {
            counter++;
            tmpList.add(entity);
            if (counter % INSERT_SIZE == 0 || counter == size) {
                success += mapper.insertBatch(tmpList);
                tmpList = new ArrayList<>();
            }
        }
        return success;
    }

    @Desc("更新(带乐观锁)")
    public Integer updateAll(@NotNull Entity entity){
        Integer update = mapper.updateAll(entity);
        if (update == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST_OR_OUT_OF_DATE);
        }
        return update;
    }

    @Desc("选择性更新(带乐观锁)")
    public Integer updateSelective(@NotNull Entity entity){
        Integer update = mapper.updateSelective(entity);
        if (update == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST_OR_OUT_OF_DATE);
        }
        return update;
    }

    @Desc("选择性更新(不带乐观锁)")
    public Integer updateSelectiveWithoutLock(@NotNull Entity entity){
        Integer update = mapper.updateSelectiveWithoutLock(entity);
        if (update == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        return update;
    }

    @Desc("批量更新(不带乐观锁)")
    public Integer update(@NotNull List<Entity> entitys){
        return mapper.updateBatch(entitys);
    }

    @Desc("删除")
    public Integer delete(@NotNull Long id){
        BaseEntity baseEntity = new BaseEntity();
        baseEntity.setId(id);
        Entity entity = (Entity)baseEntity;
        return mapper.delete(entity);
    }

    @Desc("删除")
    public Integer delete(@NotNull List<Long> ids){
        BaseEntity baseEntity = new BaseEntity();
        baseEntity.setIds(ids);
        Entity entity = (Entity)baseEntity;
        return mapper.delete(entity);
    }
    /*
    @Desc("批量删除")
    public Integer delete(@NotNull List<Entity> entitys){
        if (CollectionUtils.isEmpty(entitys)){
            throw BizException.error("entitys can not be null");
        }
        List<Long> ids = new ArrayList<>();
        entitys.forEach(entity -> {
            Long tmpId = entity.getId();
            List<Long> tmpIds = entity.getIds();
            if (tmpId != null){
                ids.add(tmpId);
            }
            if (CollectionUtils.isNotEmpty(tmpIds)){
                ids.addAll(tmpIds);
            }
        });
        Entity entity = entitys.get(0);
        entity.setId(null);
        entity.setIds(ids);
        return mapper.delete(entity);
    }
    */
    @Desc("批量删除")
    public Integer delete(@NotNull Entity entity){
        List<Long> ids = new ArrayList<>();
        Long tmpId = entity.getId();
        List<Long> tmpIds = entity.getIds();
        if (tmpId != null){
            ids.add(tmpId);
        }
        if (CollectionUtils.isNotEmpty(tmpIds)){
            ids.addAll(tmpIds);
        }
        if (CollectionUtils.isEmpty(ids)){
            throw BizException.error("id or ids can not be null at the same time");
        }
        // id 和 ids 保证只有一个存在
        if (ids.size() == 1){
            entity.setId(ids.get(0));
            entity.setIds(null);
        } else {
            entity.setId(null);
            entity.setIds(ids);
        }
        Integer delete = mapper.delete(entity);
        if (delete == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        return delete;
    }

}
