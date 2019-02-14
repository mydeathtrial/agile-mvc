package com.agile.common.mvc.service;

import com.agile.common.base.Constant;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.RandomStringUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.validate.ValidateMsg;
import org.springframework.data.domain.Sort;

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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @param <T> 服务的实体类型
 * @author 佟盟
 * 业务service
 */
public class BusinessService<T> extends MainService {
    private Class<T> entityClass;
    private final String id = "id";
    private final String page = "page";
    private final String size = "size";
    private final String sortColumn = "sort-info";
    private final String sortDirection = "sort-direction";
    private final int length = 8;

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

    /**
     * 新增
     */
    public RETURN save() throws IllegalAccessException, NoSuchIDException, NoSuchMethodException {

        T entity = getInParam(entityClass);
        if (validateInParam(entity) || !ObjectUtil.isValidity(entity)) {
            return RETURN.PARAMETER_ERROR;
        }
        Field idField = dao.getIdField(entityClass);
        idField.setAccessible(true);
        Object idValue = idField.get(entity);
        List<GeneratedValue> generatedValues = ObjectUtil.getAllEntityPropertyAnnotation(entityClass, idField, GeneratedValue.class);
        boolean isGenerate = false;
        for (GeneratedValue generatedValue : generatedValues) {
            if (isGenerate) {
                continue;
            }
            isGenerate = generatedValue != null && generatedValue.strategy() != GenerationType.AUTO;
        }
        if (!isGenerate && idValue == null) {
            String idValueTemp;
            Class<?> type = idField.getType();
            if (type == int.class || type == long.class || type == Integer.class || type == Long.class) {
                idValueTemp = RandomStringUtil.getRandom(length, RandomStringUtil.Random.NUMBER);
            } else {
                idValueTemp = RandomStringUtil.getRandom(length, RandomStringUtil.Random.MIX_1);
            }
            idField.set(entity, ObjectUtil.cast(idField.getType(), idValueTemp));
        }
        dao.save(entity);
        return RETURN.SUCCESS;
    }

    /**
     * 删除
     */
    public RETURN delete() throws NoSuchIDException {
        T entity = getInParam(entityClass);
        boolean require = this.containsKey(id);
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

    /**
     * 修改
     */
    public RETURN update() throws NoSuchIDException, IllegalAccessException {
        T entity = getInParam(entityClass);
        if (validateInParam(entity) || ObjectUtil.isEmpty(entity) || !containsKey(id)) {
            return RETURN.PARAMETER_ERROR;
        }

        Field field = dao.getIdField(entityClass);
        field.set(entity, getInParam(id, String.class));

        dao.updateOfNotNull(entity);
        return RETURN.SUCCESS;
    }

    /**
     * 分页查询
     */
    public RETURN pageQuery() throws IllegalAccessException, InstantiationException {
        final int defPage = 0;
        final int defSize = 10;
        T entity = getInParam(entityClass);
        if (entity == null) {
            entity = entityClass.newInstance();
        }
        setOutParam(Constant.ResponseAbout.RESULT, dao.findAll(entity, getInParam(page, Integer.class, defPage), getInParam(size, Integer.class, defSize), getSort()));
        return RETURN.SUCCESS;
    }

    /**
     * 查询
     */
    public RETURN query() throws NoSuchIDException {
        T entity = getInParam(entityClass);
        boolean require = this.containsKey(id);
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
        String[] ids = this.getInParamOfArray(id);
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
        Sort.Direction direction = Sort.Direction.fromString(getInParam(sortDirection, String.class, "asc"));
        Sort sort = Sort.unsorted();
        if (this.containsKey(sortColumn)) {
            List<String> column = Arrays.asList(getInParamOfArray(sortColumn));
            sort = new Sort(direction, column);
        }
        return sort;
    }

    /**
     * 查询
     */
    public RETURN queryById() {
        boolean require = this.containsKey(id);
        if (!require) {
            return RETURN.PARAMETER_ERROR;
        }

        T entity = getInParam(entityClass);
        T target = dao.findOne(entityClass, getInParam(id, String.class));
        if (!ObjectUtil.isEmpty(entity) && !ObjectUtil.compareOfNotNull(entity, target)) {
            target = null;
        }

        setOutParam(Constant.ResponseAbout.RESULT, target);
        return RETURN.SUCCESS;
    }
}
