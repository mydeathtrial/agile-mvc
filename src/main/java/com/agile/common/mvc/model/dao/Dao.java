package com.agile.common.mvc.model.dao;

import com.agile.common.config.SpringConfig;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.util.*;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.Level;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.*;

/**
 * Created by 佟盟 on 2017/11/15
 */
@Lazy
@Component
public class Dao {
    private Log logger = com.agile.common.factory.LoggerFactory.createLogger("sql", Dao.class, Level.DEBUG,Level.ERROR);
    private static Map<String,Object> map = new HashMap<>();

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(SpringConfig.class);
        ctx.refresh();
        ctx.getBean("dao");
        ctx.close();
    }
    @PersistenceContext
    private EntityManager entityManager ;

    private <T,ID extends Serializable> JpaRepository getRepository(Class<T> tableClass) throws NoSuchIDException {
        Field field = getIdField(tableClass);

        @SuppressWarnings("unchecked")
        Class<ID> idClass = (Class<ID>) field.getType();
        JpaRepository repository = (JpaRepository) map.get(tableClass.getName());
        if(ObjectUtil.isEmpty(repository)){
            repository = new SimpleJpaRepository<T,ID>(tableClass,getEntityManager());
            map.put(tableClass.getName(),repository);
        }
        return repository;
    }

    /**
     * 获取EntityManager，操作jpa api的入口
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * 保存
     * @param o ORM对象
     */
    public void save(Object o){
        getEntityManager().persist(o);
    }

    /**
     * 保存
     * @param list ORM对象列表
     */
    public <T> boolean save(Iterable<T> list) throws NoSuchIDException {
        boolean isTrue = false;
        Iterator<T> iterator = list.iterator();
        if(iterator.hasNext()){
            T obj = iterator.next();
            getRepository(obj.getClass()).saveAll(list);
            isTrue = true;

        }
        return isTrue;
    }

    /**
     * 获取数据库连接
     * @return Connection
     */
    public Connection getConnection(){
        return getEntityManager().unwrap(SessionImplementor.class).connection();
    }

    /**
     * 保存并刷新
     */
    @SuppressWarnings("unchecked")
    public <T>T saveAndReturn(T o,boolean isFlush) throws NoSuchIDException {
        if(isFlush){
            return (T)getRepository(o.getClass()).saveAndFlush(o);
        }else {
            return (T)getRepository(o.getClass()).save(o);
        }

    }

    /**
     * 保存
     */
    @SuppressWarnings("unchecked")
    public <T>T saveAndReturn(T o) throws NoSuchIDException {
        return saveAndReturn(o,Boolean.FALSE);
    }

    /**
     * 批量保存
     * @param list 要保存的ORM对象
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> saveAndReturn(Iterable<T> list) throws NoSuchIDException {
        Iterator<T> iterator = list.iterator();
        if(iterator.hasNext()){
            T obj = iterator.next();
            getRepository(obj.getClass()).saveAll(list);
        }
        return null;
    }

    /**
     * 判断数据是否存在
     * @param tableClass ORM对象类型
     * @param id 数据主键
     */
    @SuppressWarnings("unchecked")
    public <T>Object existsById(Class<T> tableClass,Object id) throws NoSuchIDException {
        return getRepository(tableClass).existsById(id);
    }

    /**
     * 刷新数据库中表
     * @param tableClass ORM对象类型
     */
    public void flush(Class<?> tableClass) throws NoSuchIDException {
        getRepository(tableClass).flush();
    }

    /**
     * 刷新数据库中全部表
     */
    @SuppressWarnings("unchecked")
    public void flush(){
        getEntityManager().flush();
    }


    /**
     * 刷新数据库数据到实体类当中
     */
    @SuppressWarnings("unchecked")
    public void refresh(Object o){
        getEntityManager().refresh(o);
    }

    /**
     * 更新或新增
     * @param o ORM对象
     */
    public <T>T update(T o){
        return getEntityManager().merge(o);
    }

    /**
     * 更新或新增非空字段
     * @param o ORM对象
     */
    public <T>T updateOfNotNull(T o) throws NoSuchIDException, IllegalAccessException {
        Class<?> clazz = o.getClass();
        Field idField = getIdField(clazz);
        idField.setAccessible(true);
        T old = (T)findOne(clazz, idField.get(o));
        ObjectUtil.copyPropertiesOfNotNull(o,old);
        return getEntityManager().merge(old);
    }

    /**
     * 删除
     * @param o ORM对象
     */
    public void delete(Object o) throws NoSuchIDException {
        List list = this.findAll(o);
        deleteInBatch(list);
    }

    /**
     * 删除
     * @param tableClass ORM对象类型
     * @param id 删除的主键标识
     */
    @SuppressWarnings("unchecked")
    public <T>void deleteById(Class<T> tableClass,Object id) throws NoSuchIDException {
        getRepository(tableClass).deleteById(id);
    }

    /**
     * 删除全部(逐一删除)
     * @param tableClass ORM对象类型
     */
    @SuppressWarnings("unchecked")
    public <T>void deleteAll(Class<T> tableClass) throws NoSuchIDException {
        getRepository(tableClass).deleteAll();
    }

    /**
     * 删除全部(一次性删除)
     * @param tableClass ORM对象类型
     */
    @SuppressWarnings("unchecked")
    public <T>void deleteAllInBatch(Class<T> tableClass) throws NoSuchIDException {
        getRepository(tableClass).deleteAllInBatch();
    }

    /**
     * 主键数组转换ORM对象列表
     * @param tableClass ORM类型
     * @param ids 主键数组
     */
    private <T,ID>List createObjectList(Class<T> tableClass,ID[] ids) throws NoSuchIDException {
        ArrayList list = new ArrayList();
        Field idField =  getIdField(tableClass);
        for (int i = 0 ; i < ids.length;i++){
            try {
                Object instance = tableClass.newInstance();
                idField.setAccessible(true);
                idField.set(instance, ObjectUtil.cast(idField.getType(),ids[i]));
                list.add(instance);
            } catch (IllegalAccessException | InstantiationException e) {
                logger.error("主键数组转换ORM对象列表失败",e);
            }
        }
        return list;
    }

    /**
     * 获取ORM中的主键字段
     * @param clazz ORM类
     */
    public Field getIdField(Class clazz) throws NoSuchIDException {
        Method[] methods =  clazz.getDeclaredMethods();
        for (int i = 0 ; i < methods.length;i++){
            Method method = methods[i];
            method.setAccessible(true);
            Id id = method.getAnnotation(Id.class);
            if(!ObjectUtil.isEmpty(id))
            try {
                return clazz.getDeclaredField(StringUtil.toLowerName(method.getName().replaceFirst("get","")));
            }catch (Exception e){
                throw new NoSuchIDException();
            }
        }
        throw new NoSuchIDException();
    }

    /**
     * 部分删除
     * @param tableClass ORM对象类型
     */
    @SuppressWarnings("unchecked")
    public <T,ID>void deleteAll(Class<T> tableClass,ID[] ids) throws NoSuchIDException {
        if(ArrayUtil.isEmpty(ids) || ids.length<1)return;
        List list = createObjectList(tableClass,ids);
        if(!ObjectUtil.isEmpty(list) && list.size()>0){
            getRepository(tableClass).deleteAll(list);

        }
    }

    /**
     * 部分删除(一次性删除)
     * @param tableClass ORM对象类型
     */
    @SuppressWarnings("unchecked")
    public <T,ID>void deleteInBatch(Class<T> tableClass,ID[] ids) throws NoSuchIDException {
        if(ArrayUtil.isEmpty(ids) || ids.length<1)return;
        List list = createObjectList(tableClass, ids);
        if(!ObjectUtil.isEmpty(list) && list.size()>0){
            getRepository(tableClass).deleteInBatch(list);
        }
    }

    /**
     * 部分删除(一次性删除)
     * @param list 需要删除的对象列表
     */
    @SuppressWarnings("unchecked")
    public <T>void deleteInBatch(Iterable<T> list) throws NoSuchIDException {
        Iterator<T> iterator = list.iterator();
        if(iterator.hasNext()){
            T obj = iterator.next();
            getRepository(obj.getClass()).deleteInBatch(list);
        }
    }

    /**
     * 查询单条
     */
    public <T>T findOne(Class<T> clazz,Object id){
        return getEntityManager().find(clazz,id);
    }

    /**
     * 按照例子查询单条
     */
    @SuppressWarnings("unchecked")
    public <T>T findOne(T object) throws NoSuchIDException {
        Example<T> example = Example.of(object);
        return (T) this.getRepository(object.getClass()).findOne(example).get();
    }

    /**
     * 查询列表
     * @param sql sql
     * @param clazz 返回ORM类型列表
     */
    @SuppressWarnings("unchecked")
    public <T>T findOne(String sql, Class<T> clazz,Object... parameters){
        Query query = creatClassQuery(sql,clazz,parameters);
        Object o = query.getSingleResult();
        return (T)o;
    }

    /**
     * 按照例子查询多条
     */
    public <T> List findAll(T object) throws NoSuchIDException {
        return findAll(object,Sort.unsorted());
    }

    /**
     * 按照例子查询多条/排序
     */
    public <T> List findAll(T object, Sort sort) throws NoSuchIDException {
        Example<T> example = Example.of(object);
        return this.getRepository(object.getClass()).findAll(example,sort);
    }

    /**
     * 按照例子查询多条分页
     */
    public <T> Page findAll(T object, int page, int size) throws NoSuchIDException {
        return findAll(object,page,size,Sort.unsorted());
    }

    /**
     * 按照例子查询多条分页
     */
    public <T> Page findAll(T object, int page, int size,Sort sort) throws NoSuchIDException {
        Example<T> example = Example.of(object);
        return this.getRepository(object.getClass()).findAll(example, PageRequest.of(page,size,sort));
    }

    /**
     * 查询列表
     * @param sql sql
     * @param clazz 返回ORM类型列表
     */
    @SuppressWarnings("unchecked")
    public <T>List<T> findAll(String sql, Class<T> clazz,Object... parameters){
        try {
            getIdField(clazz);
            Query query = creatClassQuery(sql,clazz,parameters);
            List list = query.getResultList();
            if(list == null || list.size()==0 || list.get(0)==null)return null;
            return query.getResultList();
        }catch (NoSuchIDException e){
            List<Map<String, Object>> list = findAllBySQL(sql, parameters);
            if(list!=null && list.size()>0){
                List<T> result = new LinkedList<>();
                if(ClassUtil.isCustomClass(clazz)){
                    for (Map<String, Object> entity:list){
                        result.add(ObjectUtil.cast(clazz,ArrayUtil.getLast(entity.values().toArray())));
                    }
                }else{
                    for (Map<String, Object> entity:list){
                        result.add(ObjectUtil.getObjectFromMap(clazz,entity));
                    }
                }
                return result;
            }
        }
        return null;
    }

    /**
     * 查询列表
     * @param sql sql
     */
    @SuppressWarnings("unchecked")
    public Page findAllBySQL(String sql, String countSql,int page, int size, Object... parameters){
        if(size<=0){
            new IllegalArgumentException().printStackTrace();
            return null;
        }
        PageImpl pageDate = null;
        Sort sort = null;
        PageRequest pageable;

        //sql格式化
        sql = sql.trim().replaceAll("[\t\r\n\\s]"," ");

        //取排序
        String[] orders = StringUtil.getMatchedString("(order by)(\\s)([\\S]+)(\\s)?(desc|asc)?",sql.toLowerCase());
        if(!ObjectUtil.isEmpty(orders)){
            String order = ArrayUtil.getLast(orders).toString();
            String[] sortMsg = StringUtil.getGroupString("(order by)(\\s)([\\S]+)(\\s)?(desc|asc)?", order);
            if(!ObjectUtil.isEmpty(sortMsg) && sortMsg.length>2){
                Sort.Direction direction;
                if(sortMsg.length>4 && !StringUtil.isEmpty(sortMsg[4])){
                    if ("desc".equals(sortMsg[4])) {
                        direction = Sort.Direction.DESC;
                    } else {
                        direction = Sort.Direction.ASC;
                    }
                }else {
                    direction = Sort.Direction.ASC;
                }
                sort = new Sort(direction,sortMsg[2].replaceAll("[\\s`]",""));
            }
        }
        pageable = PageRequest.of(page,size,sort);

        Query countQuery = creatQuery(countSql,parameters);
        int count = Integer.parseInt(countQuery.getSingleResult().toString());

        //取查询结果集
        if(count>0){
            Query query = creatQuery(sql,parameters);
            query.setFirstResult(page*size);
            query.setMaxResults(size);
            ((NativeQueryImpl) query).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            List content = query.getResultList();
            pageDate = new PageImpl(content,pageable,count);
        }

        return pageDate;
    }

    private Query creatQuery(String sql, Object... parameters){
        Query query = getEntityManager().createNativeQuery(sql);
        for (int i = 0 ; i < parameters.length ; i++ ){
            query.setParameter(i+1,parameters[i]);
        }
        return query;
    }


    private Query creatClassQuery(String sql,Class clazz, Object... parameters){
        Query query = getEntityManager().createNativeQuery(sql,clazz);
        for (int i = 0 ; i < parameters.length ; i++ ){
            query.setParameter(i+1,parameters[i]);
        }
        return query;
    }

    /**
     * 查询列表
     * @param sql sql
     */
    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> findAllBySQL(String sql,Object... parameters){
        Query query = creatQuery(sql,parameters);
        ((NativeQueryImpl) query).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.getResultList();
    }

    /**
     * 查询属性
     * sql查询结果必须只包含一个字段
     * @param sql sql
     */
    @SuppressWarnings("unchecked")
    public Object findParameter(String sql,Object... parameters){
        Query query = creatQuery(sql,parameters);
        return query.getSingleResult();
    }

    /**
     * 更新sql
     * @param sql sql
     */
    public int updateBySQL(String sql,Object... parameters){
        Query query = creatQuery(sql,parameters);
        return query.executeUpdate();
    }

    /**
     * 查询列表
     */
    @SuppressWarnings("unchecked")
    public <T,ID>List<T> findAllById(Class<T> tableClass,Iterable<ID> ids) throws NoSuchIDException {
        return getRepository(tableClass).findAllById(ids);
    }

    /**
     * 查询列表
     */
    @SuppressWarnings("unchecked")
    public <T,ID>List<T> findAllByArrayId(Class<T> tableClass,ID... ids) throws NoSuchIDException {
        return getRepository(tableClass).findAllById(ArrayUtil.asList(ids));
    }

    /**
     * 查询列表
     */
    @SuppressWarnings("unchecked")
    public <T> Page<T> findAll(Class<T> tableClass, int page, int size) throws NoSuchIDException {
        return getRepository(tableClass).findAll(PageRequest.of(page,size));
    }

    /**
     * 查询列表
     */
    @SuppressWarnings("unchecked")
    public <T>List<T> findAll(Class<T> tableClass) throws NoSuchIDException {
        return getRepository(tableClass).findAll();
    }

    /**
     * 查询表总数
     */
    @SuppressWarnings("unchecked")
    public long count(Class tableClass) throws NoSuchIDException {
        return getRepository(tableClass).count();
    }
}
