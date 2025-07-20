package com.wkclz.mybatis.base;

import com.wkclz.common.annotation.Desc;
import com.wkclz.common.emuns.ResultStatus;
import com.wkclz.common.entity.BaseEntity;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.utils.AssertUtil;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2019-01-17 15:22:38
 * Updadte: wangkaicun @ 2019-12-31 23:39:46
 */
public class BaseService<E extends BaseEntity, Mapper extends BaseMapper<E>> {

    private static final int INSERT_SIZE = 1000;

    @Resource
    protected Mapper mapper;

    @Desc("统计")
    public Long count(E e){
        checkEntity(e);
        return mapper.count(e);
    }

    @Desc("用ID查找")
    public E get(Long id){
        checkId(id);
        return mapper.getById(id);
    }

    @Desc("用ID查找, 若不存在则报错")
    public E getWithCheck(Long id){
        checkId(id);
        E e = mapper.getById(id);
        if (e == null) {
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        return e;
    }

    @Desc("用 Entity 查找")
    public E get(E e){
        checkEntity(e);
        return mapper.getByEntity(e);
    }

    @Desc("用 Entity 查找, 若不存在则报错")
    public E getWithCheck(E e){
        checkEntity(e);
        e = mapper.getByEntity(e);
        if (e == null) {
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        return e;
    }

    @Desc("查询列表，不包含Blobs")
    public List<E> list(E e){
        checkEntity(e);
        return mapper.list(e);
    }

    @Desc("查询列分页，不包含Blobs")
    public PageData<E> page(E e){
        checkEntity(e);
        e.init();
        Long count = mapper.count(e);
        List<E> list = null;
        if (count > 0){
            list = mapper.list(e);
        }
        if (list == null){
            list = new ArrayList<>();
        }
        PageData<E> pageData = new PageData<>(e.getCurrent(), e.getSize(), count, list);
        return pageData;
    }

    @Desc("(选择性)插入")
    public Long insert(E e){
        checkEntity(e);
        mapper.insert(e);
        return e.getId();
    }

    @Desc("全量批量插入")
    @Transactional(rollbackFor = Exception.class)
    public Integer insert(List<E> es){
        if (CollectionUtils.isEmpty(es)) {
            return 0;
        }
        int size = es.size();
        int counter = 0;
        int success = 0;
        List<E> tmpList = new ArrayList<>();
        for (E e : es) {
            counter++;
            tmpList.add(e);
            if (counter % INSERT_SIZE == 0 || counter == size) {
                success += mapper.insertBatch(tmpList);
                tmpList = new ArrayList<>();
            }
        }
        return success;
    }

    @Desc("更新(带乐观锁)")
    public Integer updateAll(E e){
        checkEntity(e);
        checkId(e.getId());
        Integer update = mapper.updateAll(e);
        if (update == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST_OR_OUT_OF_DATE);
        }
        return update;
    }

    @Desc("选择性更新(带乐观锁)")
    public Integer updateSelective(E e){
        checkEntity(e);
        checkId(e.getId());
        Integer update = mapper.updateSelective(e);
        if (update == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST_OR_OUT_OF_DATE);
        }
        return update;
    }

    @Desc("选择性更新(不带乐观锁)")
    public Integer updateSelectiveWithoutLock(E e){
        checkEntity(e);
        checkId(e.getId());
        Integer update = mapper.updateSelectiveWithoutLock(e);
        if (update == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        return update;
    }

    @Desc("批量更新(不带乐观锁)")
    public Integer update(List<E> es){
        if (CollectionUtils.isEmpty(es)){
            throw BizException.error("entitys can not be null");
        }
        return mapper.updateBatch(es);
    }

    @Desc("保存，无id则新增，有id则修改，带乐观锁, 选择性更新")
    public E saveWithCheck(E e) {
        checkEntity(e);
        if (e.getId() == null) {
            mapper.insert(e);
            return e;
        } else {
            AssertUtil.notNull(e.getVersion(), "请求错误！参数[version]不能为空");
            E oldE = get(e.getId());
            if (oldE == null) {
                throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
            }
            E.copyIfNotNull(e, oldE);
            updateSelective(oldE);
            return oldE;
        }
    }

    @Desc("批量删除")
    public Integer deleteByEntitys(List<E> es){
        if (CollectionUtils.isEmpty(es)){
            throw BizException.error("entitys can not be null");
        }
        List<Long> ids = new ArrayList<>();
        es.forEach(e -> ids.addAll(getIds(e)));
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
    public E deleteWithCheck(Long id){
        checkId(id);
        E e = get(id);
        if (e == null) {
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        BaseEntity baseEntity = new BaseEntity();
        baseEntity.setId(id);
        deleteByBaseEntity(baseEntity);
        return e;
    }

    @Desc("删除")
    public Integer delete(List<Long> ids){
        if (ids == null || ids.isEmpty()) {
            throw BizException.error(ResultStatus.PARAM_NULL);
        }
        BaseEntity baseEntity = new BaseEntity();
        baseEntity.setIds(ids);
        return deleteByBaseEntity(baseEntity);
    }

    @Desc("批量删除")
    public Integer delete(E e){
        checkEntity(e);
        List<Long> ids = getIds(e);
        BaseEntity baseEntity = new BaseEntity();
        baseEntity.setIds(ids);
        return deleteByBaseEntity(baseEntity);
    }

    private Integer deleteByBaseEntity(BaseEntity baseEntity){
        if (baseEntity == null) {
            throw BizException.error(ResultStatus.PARAM_NULL);
        }
        if (baseEntity.getId() == null && (baseEntity.getIds() == null || baseEntity.getIds().isEmpty())) {
            throw BizException.error("id or ids can not be null at the same time");
        }
        E e = (E) baseEntity;
         Integer delete = mapper.delete(e);
        if (delete == 0){
            throw BizException.error(ResultStatus.RECORD_NOT_EXIST);
        }
        return delete;
    }

    public static <E extends BaseEntity> List<Long> getIds(E e) {
        List<Long> ids = new ArrayList<>();
        Long tmpId = e.getId();
        if (tmpId != null){
            ids.add(tmpId);
        }
        List<Long> tmpIds = e.getIds();
        if (CollectionUtils.isNotEmpty(tmpIds)){
            ids.addAll(tmpIds);
        }
        if (CollectionUtils.isEmpty(ids)){
            throw BizException.error("id or ids can not be null at the same time");
        }
        return ids;
    }

    private void checkEntity(E e) {
        if (e == null) {
            throw BizException.error(ResultStatus.PARAM_NULL);
        }
    }

    private void checkId(Long id) {
        if (id == null) {
            throw BizException.error(ResultStatus.PARAM_NO_ID);
        }
    }
}
