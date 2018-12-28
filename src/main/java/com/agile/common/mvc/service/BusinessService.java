package com.agile.common.mvc.service;

import com.agile.common.base.Constant;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.RandomStringUtil;
import com.agile.common.validate.ValidateMsg;
import org.springframework.data.domain.Sort;

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
    private final String sortColumn = "sort-column";
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
        List<ValidateMsg> list = validate(entity, (Class<?>) null);
        if (list != null && list.size() > 0) {
            setOutParam(Constant.ResponseAbout.RESULT, list);
            return true;
        }
        return false;
    }

    /**
     * 新增
     */
    public RETURN save() throws IllegalAccessException, NoSuchIDException {

        T entity = getInParam(entityClass);
        if (validateInParam(entity) || !ObjectUtil.isValidity(entity)) {
            return RETURN.PARAMETER_ERROR;
        }
        Field idField = dao.getIdField(entityClass);
        Object idValue = idField.get(entity);
        if (idValue == null) {
            idField.set(entity, RandomStringUtil.getRandom(length, RandomStringUtil.Random.MIX_1));
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
            List<String> list = getIds();
            dao.deleteInBatch(entityClass, list.toArray());
            return RETURN.SUCCESS;
        } else if (!require && !ObjectUtil.isEmpty(entity)) {
            dao.delete(entity);
            return RETURN.SUCCESS;
        } else if (require && !ObjectUtil.isEmpty(entity)) {
            List<String> list = getIds();
            List<T> entityList = dao.findAllById(entityClass, list);
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
    public RETURN query() {
        T entity = getInParam(entityClass);
        boolean require = this.containsKey(id);
        if (require && ObjectUtil.isEmpty(entity)) {
            List<String> list = getIds();
            setOutParam(Constant.ResponseAbout.RESULT, dao.findAllById(entityClass, list));
        } else if (!require && !ObjectUtil.isEmpty(entity)) {
            setOutParam(Constant.ResponseAbout.RESULT, dao.findAll(entity, getSort()));
        } else if (require && !ObjectUtil.isEmpty(entity)) {
            List<String> list = getIds();
            List<T> entityList = dao.findAllById(entityClass, list);
            entityList.removeIf(o -> !ObjectUtil.compareOfNotNull(entity, o));

            setOutParam(Constant.ResponseAbout.RESULT, entityList);
        } else {
            setOutParam(Constant.ResponseAbout.RESULT, dao.findAll(entityClass));
        }
        return RETURN.SUCCESS;
    }

    private List<String> getIds() {
        List<String> list = new ArrayList<>();
        String[] ids = this.getInParamOfArray(id);
        for (String idValue : ids) {
            if (idValue.contains(Constant.RegularAbout.COMMA)) {
                String[] s = idValue.split(Constant.RegularAbout.COMMA);
                list.addAll(Arrays.asList(s));
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
