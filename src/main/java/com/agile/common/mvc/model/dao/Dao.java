package com.agile.common.mvc.model.dao;

import com.agile.common.base.Constant;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.util.*;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.Level;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
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
            List result = getRepository(obj.getClass()).saveAll(list);
            if(result!=null)return result;
        }
        return new ArrayList<>();
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
        Field[] fields = clazz.getDeclaredFields();
        for(int i = 0 ; i < fields.length ; i++){
            Field field = fields[i];
            field.setAccessible(true);
            Id id = field.getAnnotation(Id.class);
            if(!ObjectUtil.isEmpty(id))
            return field;
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
     * 根据给定参数动态生成sql语句
     * @param sql 原sql模板
     * @param parameters Map格式参数集合
     * @return 生成的sql结果
     */
    public static String parserCountSQL(String sql,Map<String,Object> parameters){
        sql = parserSQL( sql, parameters);
        // 新建 MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();
        SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLSelectStatement) statement).getSelect().getQueryBlock();
        List<SQLSelectItem> items = sqlSelectQueryBlock.getSelectList();
        items.removeAll(items);
        items.add(new SQLSelectItem(SQLUtils.toSQLExpr("count(1)")));
        return sqlSelectQueryBlock.toString();
    }

    /**
     * 根据给定参数动态生成sql语句
     * @param sql 原sql模板
     * @param parameters Map格式参数集合
     * @return 生成的sql结果
     */
    public static String parserSQL(String sql,Map<String,Object> parameters){
        if(!sql.contains("{"))return sql;
        sql = sql.replaceAll("\\{","\\'{").replaceAll("}","\\}'");

        // 新建 MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);

        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();

        // 使用visitor来访问AST
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);

        SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLSelectStatement) statement).getSelect().getQueryBlock();

        SQLExpr where = sqlSelectQueryBlock.getWhere();
        if(where == null)return sql;
        List<SQLObject> conditions = where.getChildren();
        for (SQLObject c:conditions) {
            List<SQLExpr> items = SQLBinaryOpExpr.split((SQLBinaryOpExpr) c);
            for (SQLExpr item:items) {
                String key = StringUtil.getMatchedString(Constant.RegularAbout.URL_PARAM,item.toString(),0);
                if(key!=null){
                    Object value = parameters.get(key);
                    if(parameters.get(key) == null || StringUtil.isEmpty(String.valueOf(value))){
                        SQLUtils.replaceInParent(item,null);
                    }else{
                        String format = item.toString().replace(String.format("{%s}", key), "{%s}");
                        SQLUtils.replaceInParent(item,SQLUtils.toSQLExpr(format.replaceAll("\\{%s\\}",parameters.get(key).toString())));
                    }
                }
            }
        }

        System.out.println(sqlSelectQueryBlock.toString());
        return sqlSelectQueryBlock.toString();
    }

    /**
     * 查询列表
     * @param sql sql
     * @param clazz 返回ORM类型列表
     */
    @SuppressWarnings("unchecked")
    public <T>T findOne(String sql, Class<T> clazz,Object... parameters){
        Query query = creatClassQuery(sql,clazz,parameters);
        if(query.getResultList().size()==0){
            return null;
        }
        Object o = query.getSingleResult();
        return (T)o;
    }

    /**
     * 查询列表
     * @param sql sql
     * @param clazz 返回ORM类型列表
     */
    public <T>T findOne(String sql, Class<T> clazz,Map<String,Object> parameters){
        return findOne(parserSQL(sql,parameters),clazz);
    }

    /**
     * 按照例子查询多条
     */
    public <T> List findAll(T object) throws NoSuchIDException {
        List result = findAll(object, Sort.unsorted());
        if(result!=null)return result;
        return new ArrayList();
    }

    /**
     * 按照例子查询多条/排序
     */
    public <T> List findAll(T object, Sort sort) throws NoSuchIDException {
        Example<T> example = Example.of(object);
        List result = this.getRepository(object.getClass()).findAll(example,sort);
        if(result!=null)return result;
        return new ArrayList();
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
            List result = query.getResultList();
            if(result!=null)return result;
        }catch (NoSuchIDException e){
            List<Map<String, Object>> list = findAllBySQL(sql, parameters);
            if(list!=null && list.size()>0){
                List<T> result = new LinkedList<>();
                if(ClassUtil.isCustomClass(clazz)){
                    for (Map<String, Object> entity:list){
                        T node = ObjectUtil.cast(clazz, ArrayUtil.getLast(entity.values().toArray()));
                        if(node!=null)
                        result.add(node);
                    }
                }else{
                    for (Map<String, Object> entity:list){
                        T node = ObjectUtil.getObjectFromMap(clazz,entity);
                        if(node!=null){
                            result.add(node);
                        }
                    }
                }
                return result;
            }
        }
        return new ArrayList<>();
    }

    public <T>List<T> findAll(String sql, Class<T> clazz,Map<String,Object> parameters){
        List result = findAll(parserSQL(sql,parameters),clazz);
        if(result!=null)return result;
        return new ArrayList<>();
    }

    /**
     * 查询列表
     * @param sql sql
     */
    @SuppressWarnings("unchecked")
    public Page findPageBySQL(String sql, String countSql,int page, int size, Object... parameters){
        if(size<=0){
            new IllegalArgumentException().printStackTrace();
            return null;
        }
        PageImpl pageDate = null;
        PageRequest pageable;

        //sql格式化
        sql = sql.trim().replaceAll("[\t\r\n\\s]"," ");

        // 新建 MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();
        SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLSelectStatement) statement).getSelect().getQueryBlock();

        List<Sort.Order> orderList = null;
        SQLOrderBy orderBy = sqlSelectQueryBlock.getOrderBy();
        if(orderBy!=null){
            List<SQLSelectOrderByItem> items = orderBy.getItems();
            if(items!=null){
                orderList = new ArrayList<>();
                for (SQLSelectOrderByItem item:items) {
                    String column = item.getExpr().toString();
                    if(item.getType() == null){
                        orderList.add(Sort.Order.by(column));
                    }else{
                        Sort.Direction des = Sort.Direction.fromString(item.getType().name_lcase);
                        switch (des){
                            case ASC:
                                orderList.add(Sort.Order.asc(column));
                                break;
                            case DESC:
                                orderList.add(Sort.Order.desc(column));
                                break;
                        }
                    }
                }
            }
        }

        if(orderList!=null && orderList.size()>0){
            pageable = PageRequest.of(page,size,Sort.by(orderList));
        }else{
            pageable = PageRequest.of(page,size,Sort.unsorted());
        }

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

    public Page findPageBySQL(String sql, String countSql,int page, int size, Map<String,Object> parameters){
        return findPageBySQL(parserSQL(sql,parameters),parserSQL(countSql,parameters),page,size);
    }

    public Page findPageBySQL(String sql,int page, int size, Map<String,Object> parameters){
        return findPageBySQL(parserSQL(sql,parameters),parserCountSQL(sql,parameters),page,size);
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
        List result = query.getResultList();
        if(result!=null)return result;
        return new ArrayList<>();
    }

    public List<Map<String,Object>> findAllBySQL(String sql,Map<String,Object> parameters){
        List result = findAllBySQL(parserSQL(sql,parameters));
        if(result!=null)return result;
        return new ArrayList<>();
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

    public Object findParameter(String sql,Map<String,Object> parameters){
        return findParameter(parserSQL(sql,parameters));
    }

    /**
     * 更新sql
     * @param sql sql
     */
    public int updateBySQL(String sql,Object... parameters){
        Query query = creatQuery(sql,parameters);
        return query.executeUpdate();
    }

    public int updateBySQL(String sql,Map<String,Object> parameters){
        return updateBySQL(parserSQL(sql,parameters));
    }
    /**
     * 查询列表
     */
    @SuppressWarnings("unchecked")
    public <T,ID>List<T> findAllById(Class<T> tableClass,Iterable<ID> ids) throws NoSuchIDException {
        List result = getRepository(tableClass).findAllById(ids);
        if(result!=null)return result;
        return new ArrayList<>();
    }

    /**
     * 查询列表
     */
    @SuppressWarnings("unchecked")
    public <T,ID>List<T> findAllByArrayId(Class<T> tableClass,ID... ids) throws NoSuchIDException {
        List result = getRepository(tableClass).findAllById(ArrayUtil.asList(ids));
        if(result!=null)return result;
        return new ArrayList<>();
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
        List result = getRepository(tableClass).findAll();
        if(result!=null)return result;
        return new ArrayList<>();
    }

    /**
     * 查询表总数
     */
    @SuppressWarnings("unchecked")
    public long count(Class tableClass) throws NoSuchIDException {
        return getRepository(tableClass).count();
    }
}
