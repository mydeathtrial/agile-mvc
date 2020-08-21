package com.agile.common.mvc.service;

import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.jpa.dao.Dao;
import cloud.agileframework.spring.util.spring.BeanUtil;
import cloud.agileframework.validate.ValidateCustomBusiness;
import cloud.agileframework.validate.ValidateMsg;
import cloud.agileframework.validate.ValidateUtil;
import cloud.agileframework.validate.annotation.Validate;
import cloud.agileframework.validate.group.Insert;
import cloud.agileframework.validate.group.Update;
import com.agile.common.annotation.AgileService;
import com.agile.common.annotation.Mapping;
import com.agile.common.base.Constant;
import com.agile.common.exception.NoSuchRequestServiceException;
import com.agile.common.param.AgileParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author 佟盟
 * 日期 2020/8/00021 9:49
 * 描述 基础服务，提供增删改查
 * @version 1.0
 * @since 1.0
 */
@AgileService
@Mapping("/api/{model}")
public class BaseService {
    @Autowired
    private Dao dao;

    @Validate(customBusiness = InsertValidateDo.class)
    @Mapping(method = RequestMethod.POST)
    public void save(String model) throws NoSuchRequestServiceException {
        dataAsParam(model, d -> {
            dao.save(d);
            return true;
        });
    }

    public static <T> T dataAsParam(String model, Function<Object, T> function) throws NoSuchRequestServiceException {
        return typeAsParam(model, javaType -> {
            Object data = AgileParam.getInParam(javaType);
            return function.apply(data);
        });
    }

    public static <T> T typeAsParam(String model, Function<Class<?>, T> function) throws NoSuchRequestServiceException {
        Optional<EntityType<?>> entityType = getEntityType(model);
        if (entityType.isPresent()) {
            Class<?> javaType = entityType.get().getJavaType();
            return function.apply(javaType);
        }
        throw new NoSuchRequestServiceException();
    }

    @Validate(value = "id", nullable = false, isBlank = false)
    @Mapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(String model, Object id) throws NoSuchRequestServiceException {
        typeAsParam(model, javaType -> dao.deleteById(javaType, id));
    }

    @Validate(value = "id", nullable = false, isBlank = false)
    @Mapping(method = RequestMethod.DELETE)
    public void delete(Object[] id, String model) throws NoSuchRequestServiceException {
        typeAsParam(model, javaType -> {
            dao.deleteInBatch(javaType, id);
            return true;
        });
    }

    @Validate(customBusiness = UpdateValidateDo.class)
    @Mapping(method = RequestMethod.PUT)
    public void update(String model) throws NoSuchRequestServiceException {
        dataAsParam(model, data -> dao.saveOrUpdate(data));
    }

    @Mapping(method = RequestMethod.GET)
    public List<Object> query(String model) throws NoSuchRequestServiceException {
        return dataAsParam(model, data -> dao.findAll(data));
    }

    @Validate(value = "id", nullable = false, isBlank = false)
    @Mapping(value = "/{id}", method = RequestMethod.GET)
    public Object queryById(String model, Object id) throws NoSuchRequestServiceException {
        return typeAsParam(model, data -> dao.findOne(data, id));
    }

    @Validate(value = "page", nullable = false, isBlank = false)
    @Validate(value = "size", nullable = false, isBlank = false)
    @Mapping(value = "/{page}/{size}", method = RequestMethod.GET)
    public Page<Class<?>> page(String model, int page, int size) throws NoSuchRequestServiceException {
        return typeAsParam(model, data -> dao.findAll(data, getPageRequest(page, size)));
    }

    public PageRequest getPageRequest(int page, int size) {
        return PageRequest.of(page - 1, size, getSort());
    }

    private static final String SORT_COLUMN = "sorts";

    public Sort getSort() {
        Sort sort = Sort.unsorted();
        if (AgileParam.containsKey(SORT_COLUMN)) {
            List<String> columns = AgileParam.getInParamOfArray(SORT_COLUMN);
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


    /**
     * 根据访问的模型，遍历查找对应的orm类，用于后续处理
     *
     * @param model 模型名字
     * @return orm类
     */
    private static Optional<EntityType<?>> getEntityType(String model) {
        Dao dao = BeanUtil.getBean(Dao.class);
        if (dao == null) {
            throw new RuntimeException("not found Dao bean");
        }
        return dao.getEntityManager().getEntityManagerFactory()
                .getMetamodel()
                .getEntities()
                .stream()
                .filter(n -> n.getName().equalsIgnoreCase(StringUtil.toUpperName(model)))
                .findFirst();
    }

    /**
     * 验证录入
     */
    public static class InsertValidateDo implements ValidateCustomBusiness {

        @Override
        public List<ValidateMsg> validate(Object params) {
            String model = AgileParam.getInParam("model", String.class);
            try {
                return dataAsParam(model, data -> ValidateUtil.validate(data, Insert.class));
            } catch (NoSuchRequestServiceException e) {
                return new ArrayList<>(0);
            }
        }
    }

    /**
     * 验证录入
     */
    public static class UpdateValidateDo implements ValidateCustomBusiness {

        @Override
        public List<ValidateMsg> validate(Object params) {
            String model = AgileParam.getInParam("model", String.class);
            try {
                return dataAsParam(model, data -> ValidateUtil.validate(data, Update.class));
            } catch (NoSuchRequestServiceException e) {
                return new ArrayList<>(0);
            }
        }
    }
}
