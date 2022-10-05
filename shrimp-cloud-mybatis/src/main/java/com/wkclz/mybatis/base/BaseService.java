package com.wkclz.mybatis.base;

import com.wkclz.common.annotation.Desc;
import com.wkclz.common.emuns.ResultStatus;
import com.wkclz.common.entity.BaseEntity;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.utils.AssertUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
    public Long count(Entity entity){
        checkEntity(entity);
        return mapper.count(entity);
    }

    @Desc("用ID查找")
    public Entity get(Long id){
        checkId(id);
        return mapper.getById(id);
    }

    @Desc("用ID查找, 若不存在则报错")
    public Entity getWithCheck(Long id){
        checkId(id);
        Entity entity = mapper.getById(id);
        if (entity == null) {
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        return entity;
    }

    @Desc("用 Entity 查找")
    public Entity get(Entity entity){
        checkEntity(entity);
        return mapper.getByEntity(entity);
    }

    @Desc("用 Entity 查找, 若不存在则报错")
    public Entity getWithCheck(Entity entity){
        checkEntity(entity);
        entity = mapper.getByEntity(entity);
        if (entity == null) {
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        return entity;
    }

    @Desc("查询列表，不包含Blobs")
    public List<Entity> list(Entity entity){
        checkEntity(entity);
        return mapper.list(entity);
    }

    @Desc("查询列分页，不包含Blobs")
    public PageData<Entity> page(Entity entity){
        checkEntity(entity);
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
    public Long insert(Entity entity){
        checkEntity(entity);
        mapper.insert(entity);
        return entity.getId();
    }

    @Desc("全量批量插入")
    @Transactional(rollbackFor = Exception.class)
    public Integer insert(List<Entity> entitys){
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
    public Integer updateAll(Entity entity){
        checkEntity(entity);
        checkId(entity.getId());
        Integer update = mapper.updateAll(entity);
        if (update == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST_OR_OUT_OF_DATE);
        }
        return update;
    }

    @Desc("选择性更新(带乐观锁)")
    public Integer updateSelective(Entity entity){
        checkEntity(entity);
        checkId(entity.getId());
        Integer update = mapper.updateSelective(entity);
        if (update == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST_OR_OUT_OF_DATE);
        }
        return update;
    }

    @Desc("选择性更新(不带乐观锁)")
    public Integer updateSelectiveWithoutLock(Entity entity){
        checkEntity(entity);
        checkId(entity.getId());
        Integer update = mapper.updateSelectiveWithoutLock(entity);
        if (update == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        return update;
    }

    @Desc("批量更新(不带乐观锁)")
    public Integer update(List<Entity> entitys){
        if (CollectionUtils.isEmpty(entitys)){
            throw BizException.error("entitys can not be null");
        }
        return mapper.updateBatch(entitys);
    }

    @Desc("保存，无id则新增，有id则修改，带乐观锁, 选择性更新")
    public Entity saveWithCheck(Entity entity) {
        checkEntity(entity);
        if (entity.getId() == null) {
            mapper.insert(entity);
            return entity;
        } else {
            AssertUtil.notNull(entity.getVersion(), "请求错误！参数[version]不能为空");
            Entity oldEntity = get(entity.getId());
            if (oldEntity == null) {
                throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
            }
            Entity.copyIfNotNull(entity, oldEntity);
            updateSelective(oldEntity);
            return oldEntity;
        }
    }

    @Desc("批量删除")
    public Integer deleteByEntitys(List<Entity> entitys){
        if (CollectionUtils.isEmpty(entitys)){
            throw BizException.error("entitys can not be null");
        }
        List<Long> ids = new ArrayList<>();
        entitys.forEach(entity -> ids.addAll(getIds(entity)));
        if (ids.isEmpty()) {
            throw BizException.error(ResultStatus.PARAM_NULL);
        }
        return delete(ids);
    }

    @Desc("删除")
    public Integer delete(Long id){
        checkId(id);
        BaseEntity baseEntity = new BaseEntity();
        baseEntity.setId(id);
        return deleteByBaseEntity(baseEntity);
    }

    @Desc("删除，若成功，返回删除前的对象")
    public Entity deleteWithCheck(Long id){
        checkId(id);
        Entity entity = get(id);
        if (entity == null) {
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        BaseEntity baseEntity = new BaseEntity();
        baseEntity.setId(id);
        deleteByBaseEntity(baseEntity);
        return entity;
    }

    @Desc("删除")
    public Integer delete(List<Long> ids){
        if (ids == null || ids.size() == 0) {
            throw BizException.error(ResultStatus.PARAM_NULL);
        }
        BaseEntity baseEntity = new BaseEntity();
        baseEntity.setIds(ids);
        return deleteByBaseEntity(baseEntity);
    }

    @Desc("批量删除")
    public Integer delete(Entity entity){
        checkEntity(entity);
        List<Long> ids = getIds(entity);
        BaseEntity baseEntity = new BaseEntity();
        baseEntity.setIds(ids);
        return deleteByBaseEntity(baseEntity);
    }

    private Integer deleteByBaseEntity(BaseEntity baseEntity){
        if (baseEntity == null) {
            throw BizException.error(ResultStatus.PARAM_NULL);
        }
        if (baseEntity.getId() == null && (baseEntity.getIds() == null || baseEntity.getIds().size() == 0)) {
            throw BizException.error("id or ids can not be null at the same time");
        }
        Entity entity = (Entity) baseEntity;
         Integer delete = mapper.delete(entity);
        if (delete == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        return delete;
    }

    public static <Entity extends BaseEntity> List<Long> getIds(Entity entity) {
        List<Long> ids = new ArrayList<>();
        Long tmpId = entity.getId();
        if (tmpId != null){
            ids.add(tmpId);
        }
        List<Long> tmpIds = entity.getIds();
        if (CollectionUtils.isNotEmpty(tmpIds)){
            ids.addAll(tmpIds);
        }
        if (CollectionUtils.isEmpty(ids)){
            throw BizException.error("id or ids can not be null at the same time");
        }
        return ids;
    }

    private void checkEntity(Entity entity) {
        if (entity == null) {
            throw BizException.error(ResultStatus.PARAM_NULL);
        }
    }

    private void checkId(Long id) {
        if (id == null) {
            throw BizException.error(ResultStatus.PARAM_NO_ID);
        }
    }
}
