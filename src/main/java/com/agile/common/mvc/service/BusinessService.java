package com.agile.common.mvc.service;

import com.agile.common.base.Constant;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.RandomStringUtil;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 佟盟 on 2018/5/29
 */
public class BusinessService<T> extends MainService {
    private Class<T> entityClass;

    public BusinessService() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) params[0];
    }

    /**
     * 新增
     */
    public RETURN save() throws IllegalAccessException, NoSuchIDException {
        T entity = ObjectUtil.getObjectFromMap(entityClass, this.getInParam());
        if (ObjectUtil.isEmpty(entity) || !ObjectUtil.isValidity(entity)) return RETURN.PARAMETER_ERROR;
        Field idField = dao.getIdField(entityClass);
        idField.setAccessible(true);
        if(!ObjectUtil.haveId(idField,entity)) idField.set(entity, RandomStringUtil.getRandom(8,"ID-", RandomStringUtil.Random.MIX_1));
        dao.save(entity);
        return RETURN.SUCCESS;
    }

    /**
     * 删除
     */
    public RETURN delete() throws NoSuchIDException {
        T entity = ObjectUtil.getObjectFromMap(entityClass, this.getInParam());
        boolean require = this.containsKey("id");
        if (require && ObjectUtil.isEmpty(entity)){
            List<String> list = getIds();
            dao.deleteInBatch(entityClass, list.toArray());
            return RETURN.SUCCESS;
        }else if(!require && !ObjectUtil.isEmpty(entity)){
            dao.delete(entity);
            return RETURN.SUCCESS;
        }else if(require && !ObjectUtil.isEmpty(entity)){
            List<String> list = getIds();
            List<T> entityList = dao.findAll(entityClass, list);
            Iterator<T> it = entityList.iterator();
            while (it.hasNext()){
                T o = it.next();
                if(ObjectUtil.compareOfNotNull(entity,o)){
                    dao.delete(o);
                }
            }
            return RETURN.SUCCESS;
        }else{
            return RETURN.PARAMETER_ERROR;
        }
    }

    /**
     * 修改
     */
    public RETURN update() throws NoSuchIDException, IllegalAccessException {
        T entity = ObjectUtil.getObjectFromMap(entityClass, this.getInParam());
        if(ObjectUtil.isEmpty(entity)) return RETURN.PARAMETER_ERROR;

        Field field = dao.getIdField(entityClass);
        field.setAccessible(true);

        if(containsKey("id")){
            field.set(entity,getInParam("id",String.class));
        }
        if (ObjectUtil.isEmpty(field.get(entity)) || !ObjectUtil.isValidity(entity)) return RETURN.PARAMETER_ERROR;

        dao.update(entity);
        return RETURN.SUCCESS;
    }

    /**
     * 分页查询
     */
    public RETURN pageQuery() throws IllegalAccessException, InstantiationException, NoSuchIDException {
        T entity = ObjectUtil.getObjectFromMap(entityClass, this.getInParam());
        if(entity==null){
            entity = entityClass.newInstance();
        }
        setOutParam(Constant.ResponseAbout.RESULT,dao.findAll(entity,getInParam("page",Integer.class,0),getInParam("size",Integer.class,10),getSort()));
        return RETURN.SUCCESS;
    }

    /**
     * 查询
     */
    public RETURN query() throws NoSuchIDException {
        T entity = ObjectUtil.getObjectFromMap(entityClass, this.getInParam());
        boolean require = this.containsKey("id");
        if (require && ObjectUtil.isEmpty(entity)){
            List<String> list = getIds();
            setOutParam(Constant.ResponseAbout.RESULT,dao.findAll(entityClass,list));
        }else if(!require && !ObjectUtil.isEmpty(entity)){
            setOutParam(Constant.ResponseAbout.RESULT,dao.findAll(entity,getSort()));
        }else if(require && !ObjectUtil.isEmpty(entity)){
            List<String> list = getIds();
            List<T> entityList = dao.findAll(entityClass, list);
            Iterator<T> it = entityList.iterator();
            while (it.hasNext()){
                T o = it.next();
                if(!ObjectUtil.compareOfNotNull(entity,o)){
                    it.remove();
                }
            }

            setOutParam(Constant.ResponseAbout.RESULT,entityList);
        }else{
            setOutParam(Constant.ResponseAbout.RESULT,dao.findAll(entityClass));
        }
        return RETURN.SUCCESS;
    }

    private List<String> getIds(){
        List<String> list = new ArrayList<>();
        String[] ids = this.getInParamOfArray("id");
        for (int i = 0 ; i < ids.length;i++){
            String id = ids[i];
            if(id.contains(",")){
                String[] s = id.split(",");
                list.addAll(Arrays.asList(s));
            }else{
                list.add(id);
            }
        }
        return list;
    }

    private Sort getSort(){
        Sort.Direction sortDirection = Sort.Direction.fromString(getInParam("sort-direction",String.class,"asc"));
        Sort sort = Sort.unsorted();
        if(this.containsKey("sort-column")){
            List<String> sortColumn = Arrays.asList(getInParamOfArray("sort-column"));
            sort = new Sort(sortDirection, sortColumn);
        }
        return sort;
    }

    /**
     * 查询
     */
    public RETURN queryById() {
        boolean require = this.containsKey("id");
        if(!require)return RETURN.PARAMETER_ERROR;

        T entity = ObjectUtil.getObjectFromMap(entityClass, this.getInParam());
        T target = dao.findOne(entityClass, getInParam("id", String.class));
        if (!ObjectUtil.isEmpty(entity)){
            if(!ObjectUtil.compareOfNotNull(entity,target)){
                target = null;
            }
        }

        setOutParam(Constant.ResponseAbout.RESULT,target);
        return RETURN.SUCCESS;
    }
}
