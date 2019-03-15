package com.agile.common.mvc.service;

import com.agile.common.annotation.Init;
import com.agile.common.base.Constant;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.IdUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.util.TreeUtil;
import com.agile.common.validate.ValidateMsg;
import com.agile.mvc.entity.DictionaryDataEntity;
import org.springframework.data.domain.Sort;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @param <T> 服务的实体类型
 * @author 佟盟
 * 业务service
 */
public class BusinessService<T> extends MainService {
    private Class<T> entityClass;
    private static final String ID = "id";
    private static final String PAGE_NUM = "pageNum";
    private static final String PAGE_SIZE = "pageSize";
    private static final String SORT_COLUMN = "sorts";
    private static final int DEF_LENGTH = 8;

    public BusinessService() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) params[0];
    }

    private List<ValidateMsg> validate(T bean, Class<?>... groups) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Object>> set = validator.validate(bean, groups);
        List<ValidateMsg> list;
        if (set != null && set.size() > 0) {
            list = new ArrayList<>();
        } else {
            return null;
        }
        for (ConstraintViolation<Object> m : set) {
            ValidateMsg r = new ValidateMsg(m.getMessage(), false, m.getPropertyPath().toString(), m.getInvalidValue());
            list.add(r);
        }
        return list;
    }

    private boolean validateInParam(T entity) {
        List<ValidateMsg> list = validate(entity);
        if (list != null && list.size() > 0) {
            setOutParam(Constant.ResponseAbout.RESULT, list);
            return true;
        }
        return false;
    }

    public RETURN save() throws IllegalAccessException, NoSuchIDException, NoSuchMethodException {
        T entity = getInParam(entityClass);
        return save(entity);
    }

    /**
     * 新增
     */
    public RETURN save(T entity) throws IllegalAccessException, NoSuchIDException, NoSuchMethodException {

        if (validateInParam(entity) || !ObjectUtil.isValidity(entity)) {
            return RETURN.PARAMETER_ERROR;
        }
        Field idField = dao.getIdField(entityClass);
        idField.setAccessible(true);
        Object idValue = idField.get(entity);
        GeneratedValue generatedValue = ObjectUtil.getAllEntityPropertyAnnotation(entityClass, idField, GeneratedValue.class);
        boolean isGenerate = generatedValue != null && generatedValue.strategy() != GenerationType.AUTO;

        if (!isGenerate && idValue == null) {
            String idValueTemp;
            Column column = ObjectUtil.getAllEntityPropertyAnnotation(entityClass, idField, Column.class);
            int idLength = DEF_LENGTH;
            if (column != null) {
                idLength = column.length();
            }
            idValueTemp = Long.toString(IdUtil.generatorId());

            idValueTemp = idValueTemp.substring(idValueTemp.length() - idLength);
            idField.set(entity, ObjectUtil.cast(idField.getType(), idValueTemp));
        }
        dao.save(entity);
        return RETURN.SUCCESS;
    }

    public RETURN delete() throws NoSuchIDException {
        return delete(getInParam(entityClass));
    }

    /**
     * 删除
     */
    public RETURN delete(T entity) throws NoSuchIDException {

        boolean require = this.containsKey(ID);
        if (require && ObjectUtil.isEmpty(entity)) {
            dao.deleteInBatch(entityClass, getIds().toArray());
            return RETURN.SUCCESS;
        } else if (!require && !ObjectUtil.isEmpty(entity)) {
            dao.delete(entity);
            return RETURN.SUCCESS;
        } else if (require && !ObjectUtil.isEmpty(entity)) {
            List<T> entityList = dao.findAllById(entityClass, getIds());
            for (T o : entityList) {
                if (ObjectUtil.compareOfNotNull(entity, o)) {
                    dao.delete(o);
                }
            }
            return RETURN.SUCCESS;
        } else {
            return RETURN.PARAMETER_ERROR;
        }
    }

    public RETURN update() throws IllegalAccessException, NoSuchIDException {
        return update(getInParam(entityClass));
    }

    /**
     * 修改
     */
    public RETURN update(T entity) throws NoSuchIDException, IllegalAccessException {
        if (validateInParam(entity) || ObjectUtil.isEmpty(entity)) {
            return RETURN.PARAMETER_ERROR;
        }

        Field field = dao.getIdField(entityClass);
        if (containsKey(ID)) {
            field.set(entity, getInParam(ID, String.class));
        } else if (field.get(entity) == null) {
            return RETURN.PARAMETER_ERROR;
        }

        dao.updateOfNotNull(entity);
        return RETURN.SUCCESS;
    }

    public RETURN pageQuery() throws IllegalAccessException, InstantiationException {
        return pageQuery(getInParam(entityClass));
    }

    /**
     * 分页查询
     */
    public RETURN pageQuery(T entity) {
        final int defPage = 0;
        final int defSize = 10;

        if (entity == null) {
            setOutParam(Constant.ResponseAbout.RESULT, dao.findAll(entityClass, getInParam(PAGE_NUM, Integer.class, defPage), getInParam(PAGE_SIZE, Integer.class, defSize), getSort()));
        } else {
            setOutParam(Constant.ResponseAbout.RESULT, dao.findAll(entity, getInParam(PAGE_NUM, Integer.class, defPage), getInParam(PAGE_SIZE, Integer.class, defSize), getSort()));
        }
        return RETURN.SUCCESS;
    }

    public RETURN query() throws NoSuchIDException {
        return query(getInParam(entityClass));
    }

    /**
     * 查询
     */
    public RETURN query(T entity) throws NoSuchIDException {
        boolean require = this.containsKey(ID);
        if (require && ObjectUtil.isEmpty(entity)) {
            setOutParam(Constant.ResponseAbout.RESULT, dao.findAllById(entityClass, getIds()));
        } else if (!require && !ObjectUtil.isEmpty(entity)) {
            setOutParam(Constant.ResponseAbout.RESULT, dao.findAll(entity, getSort()));
        } else if (require && !ObjectUtil.isEmpty(entity)) {
            List<T> entityList = dao.findAllById(entityClass, getIds());
            entityList.removeIf(o -> !ObjectUtil.compareOfNotNull(entity, o));

            setOutParam(Constant.ResponseAbout.RESULT, entityList);
        } else {
            setOutParam(Constant.ResponseAbout.RESULT, dao.findAll(entityClass));
        }
        return RETURN.SUCCESS;
    }

    private List<Object> getIds() throws NoSuchIDException {
        Field idField = dao.getIdField(entityClass);
        if (idField == null) {
            throw new NoSuchIDException();
        }
        Class<?> type = idField.getType();
        List<Object> list = new ArrayList<>();
        List<String> ids = this.getInParamOfArray(ID);
        for (Object idValue : ids) {
            if (StringUtil.isString(idValue)) {
                String v = idValue.toString();
                if (v.contains(Constant.RegularAbout.COMMA)) {
                    String[] s = v.split(Constant.RegularAbout.COMMA);
                    for (String child : s) {
                        list.add(ObjectUtil.cast(type, child));
                    }
                } else {
                    list.add(ObjectUtil.cast(type, v));
                }
            } else {
                list.add(idValue);
            }

        }
        return list;
    }

    private Sort getSort() {
        Sort sort = Sort.unsorted();
        if (this.containsKey(SORT_COLUMN)) {
            List<String> columns = getInParamOfArray(SORT_COLUMN);
            List<Sort.Order> orders = new ArrayList<>(columns.size());
            for (String column : columns) {
                if (column.startsWith(Constant.RegularAbout.MINUS)) {
                    orders.add(new Sort.Order(Sort.Direction.DESC, column));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.ASC, column));
                }

            }
            sort = Sort.by(orders);
        }
        return sort;
    }

    public RETURN queryById() {
        return queryById(getInParam(entityClass));
    }

    /**
     * 查询
     */
    public RETURN queryById(T entity) {
        boolean require = this.containsKey(ID);
        if (!require) {
            return RETURN.PARAMETER_ERROR;
        }

        T target = dao.findOne(entityClass, getInParam(ID, String.class));
        if (!ObjectUtil.isEmpty(entity) && !ObjectUtil.compareOfNotNull(entity, target)) {
            target = null;
        }

        setOutParam(Constant.ResponseAbout.RESULT, target);
        return RETURN.SUCCESS;
    }

    @Init
    public void synchronousCache() throws NoSuchFieldException, IllegalAccessException {
        List<DictionaryDataEntity> list = dao.findAll(DictionaryDataEntity.class);
        List<DictionaryDataEntity> tree = TreeUtil.createTree(list, "dictionaryDataId", "parentId", "children", "root");
        for (DictionaryDataEntity entity : tree) {
            coverCacheCode(entity);
            if ("root".equals(entity.getParentId())) {
                CacheUtil.getDicCache().put(entity.getCode(), entity);
            }
        }
    }

    /**
     * 递归处理所有字典的CacheCode
     *
     * @param entity 字典实体
     */
    private void coverCacheCode(DictionaryDataEntity entity) {
        List<DictionaryDataEntity> children = entity.getChildren();
        if (children == null || children.size() == 0) {
            return;
        }
        for (DictionaryDataEntity child : children) {
            coverCacheCode(child);
            entity.addCodeCache(child.getCode(), child);
        }
    }
}
