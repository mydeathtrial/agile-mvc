package com.agile.common.mvc.model.dao;

import com.agile.common.exception.NoSuchIDException;
import com.agile.common.util.ArrayUtil;
import com.agile.common.util.ClassUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.SqlUtil;
import com.agile.common.util.StringUtil;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.Level;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟 on 2017/11/15
 */
@Lazy
@Component
public class Dao {
    private static Map<String, SimpleJpaRepository> map = new HashMap<>();
    private Log logger = com.agile.common.factory.LoggerFactory.createLogger("sql", Dao.class, Level.DEBUG, Level.ERROR);
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 根据java类型获取对应的数据库表的JpaRepository对象
     *
     * @param tableClass 表对应的实体类型
     * @param <T>        表对应的实体类型
     * @param <ID>       主键类型
     * @return 对应的数据库表的JpaRepository对象
     */
    public <T, ID extends Serializable> SimpleJpaRepository getRepository(Class<T> tableClass) {
        SimpleJpaRepository repository = map.get(tableClass.getName());
        if (ObjectUtil.isEmpty(repository)) {
            repository = new SimpleJpaRepository<T, ID>(tableClass, getEntityManager());
            map.put(tableClass.getName(), repository);
        }
        return repository;
    }

    /**
     * 获取EntityManager，操作jpa api的入口
     *
     * @return EntityManager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * 保存
     *
     * @param o ORM对象
     */
    public void save(Object o) {
        getEntityManager().persist(o);
    }

    /**
     * 批量保存
     *
     * @param list 表对应的实体类型的对象列表
     * @param <T>  表对应的实体类型
     * @return 是否保存成功
     */
    public <T> boolean save(Iterable<T> list) {
        boolean isTrue = false;
        Iterator<T> iterator = list.iterator();
        if (iterator.hasNext()) {
            T obj = iterator.next();
            getRepository(obj.getClass()).saveAll(list);
            isTrue = true;

        }
        return isTrue;
    }

    /**
     * 获取数据库连接
     *
     * @return Connection
     */
    public Connection getConnection() {
        return getEntityManager().unwrap(SessionImplementor.class).connection();
    }

    /**
     * 保存并刷新
     *
     * @param o       表对应的实体类型的对象
     * @param isFlush 是否刷新
     * @return 保存后的对象
     */
    @SuppressWarnings("unchecked")
    public <T> T saveAndReturn(T o, boolean isFlush) {
        if (isFlush) {
            return (T) getRepository(o.getClass()).saveAndFlush(o);
        } else {
            return (T) getRepository(o.getClass()).save(o);
        }

    }

    /**
     * 保存
     *
     * @param o 要保存的对象
     * @return 保存后的对象
     */
    @SuppressWarnings("unchecked")
    public <T> T saveAndReturn(T o) {
        return saveAndReturn(o, Boolean.FALSE);
    }

    /**
     * 批量保存
     *
     * @param list 要保存的对象列表
     * @param <T>  表对应的实体类型
     * @return 保存后的数据集
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> saveAndReturn(Iterable<T> list) {
        Iterator<T> iterator = list.iterator();
        if (iterator.hasNext()) {
            T obj = iterator.next();
            List result = getRepository(obj.getClass()).saveAll(list);
            if (result != null) {
                return result;
            }
        }
        return new ArrayList<>(0);
    }

    /**
     * 根据表实体类型与主键值，判断数据是否存在
     *
     * @param tableClass 表对应的实体类型
     * @param id         数据主键
     * @return 是否存在
     */
    @SuppressWarnings("unchecked")
    public boolean existsById(Class tableClass, Object id) {
        return getRepository(tableClass).existsById(id);
    }

    /**
     * 刷新数据库中指定tableClass类型实体对应的表
     *
     * @param tableClass 表对应的实体类型
     */
    public void flush(Class<?> tableClass) {
        getRepository(tableClass).flush();
    }

    /**
     * 刷新数据库中全部表
     */
    @SuppressWarnings("unchecked")
    public void flush() {
        getEntityManager().flush();
    }


    /**
     * 刷新数据库数据到实体类当中
     *
     * @param o 表对应的实体类型的对象
     */
    @SuppressWarnings("unchecked")
    public void refresh(Object o) {
        getEntityManager().refresh(o);
    }

    /**
     * 更新或新增
     *
     * @param o   ORM对象
     * @param <T> 表对应的实体类型
     * @return 返回更新后的数据
     */
    public <T> T update(T o) {
        return getEntityManager().merge(o);
    }

    /**
     * 更新或新增非空字段，空字段不进行更新
     *
     * @param o   表映射实体类型的对象
     * @param <T> 表映射实体类型的对象
     * @return 返回更新后的数据
     */
    public <T> T updateOfNotNull(T o) throws NoSuchIDException, IllegalAccessException {
        Class<?> clazz = o.getClass();
        Field idField = getIdField(clazz);
        idField.setAccessible(true);
        T old = (T) findOne(clazz, idField.get(o));
        ObjectUtil.copyPropertiesOfNotNull(o, old);
        return getEntityManager().merge(old);
    }

    /**
     * 根据提供的对象参数，作为例子，查询出结果并删除
     *
     * @param o 表实体对象
     */
    public void delete(Object o) {
        List list = this.findAll(o);
        deleteInBatch(list);
    }

    /**
     * 删除
     *
     * @param tableClass 查询的目标表对应实体类型，Entity
     * @param id         删除的主键标识
     * @param <T>        查询的目标表对应实体类型
     */
    @SuppressWarnings("unchecked")
    public <T> void deleteById(Class<T> tableClass, Object id) {
        getRepository(tableClass).deleteById(id);
    }

    /**
     * 删除全部(逐一删除)
     *
     * @param tableClass 查询的目标表对应实体类型，Entity
     * @param <T>        查询的目标表对应实体类型
     */
    @SuppressWarnings("unchecked")
    public <T> void deleteAll(Class<T> tableClass) {
        getRepository(tableClass).deleteAll();
    }

    /**
     * 删除全部(一次性删除)
     *
     * @param tableClass 查询的目标表对应实体类型，Entity
     * @param <T>        查询的目标表对应实体类型
     */
    @SuppressWarnings("unchecked")
    public <T> void deleteAllInBatch(Class<T> tableClass) {
        getRepository(tableClass).deleteAllInBatch();
    }

    /**
     * 根据表映射的实体类型与主键值集合，创建一个只包含主键值的空的对象集合
     *
     * @param tableClass 查询的目标表对应实体类型，Entity
     * @param ids        主键数组
     * @throws NoSuchIDException tableClass实体类型中没有找到@ID的注解，识别成主键字段
     */
    private <T, ID> List<T> createObjectList(Class<T> tableClass, ID[] ids) throws NoSuchIDException {
        ArrayList<T> list = new ArrayList<>();
        Field idField = getIdField(tableClass);
        for (int i = 0; i < ids.length; i++) {
            try {
                Object instance = tableClass.newInstance();
                idField.setAccessible(true);
                idField.set(instance, ObjectUtil.cast(idField.getType(), ids[i]));
                list.add((T) instance);
            } catch (IllegalAccessException | InstantiationException e) {
                logger.error("主键数组转换ORM对象列表失败", e);
            }
        }
        return list;
    }

    /**
     * 获取ORM中的主键字段
     *
     * @param clazz 查询的目标表对应实体类型，Entity
     * @return 主键属性
     * @throws NoSuchIDException tableClass实体类型中没有找到@ID的注解，识别成主键字段
     */
    public Field getIdField(Class clazz) throws NoSuchIDException {
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            method.setAccessible(true);
            Id id = method.getAnnotation(Id.class);
            if (!ObjectUtil.isEmpty(id)) {
                try {
                    return clazz.getDeclaredField(StringUtil.toLowerName(method.getName().replaceFirst("get", "")));
                } catch (Exception e) {
                    throw new NoSuchIDException();
                }
            }
        }
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Id id = field.getAnnotation(Id.class);
            if (!ObjectUtil.isEmpty(id)) {
                return field;
            }
        }
        throw new NoSuchIDException();
    }

    /**
     * 根据主键与实体类型，部分删除，删除对象集(一次性删除)
     *
     * @param tableClass 查询的目标表对应实体类型，Entity
     * @param <T>        查询的目标表对应实体类型
     * @param ids        主键数组
     * @param <ID>       主键类型
     * @throws NoSuchIDException tableClass实体类型中没有找到@ID的注解，识别成主键字段
     */
    @SuppressWarnings("unchecked")
    public <T, ID> void deleteInBatch(Class<T> tableClass, ID[] ids) throws NoSuchIDException {
        if (ArrayUtil.isEmpty(ids) || ids.length < 1) {
            return;
        }
        List list = createObjectList(tableClass, ids);
        if (!ObjectUtil.isEmpty(list) && list.size() > 0) {
            getRepository(tableClass).deleteInBatch(list);
        }
    }

    /**
     * 根据表映射类型的对象集合，部分删除，删除对象集(一次性删除)，无返回值
     *
     * @param list 需要删除的对象列表
     * @param <T>  删除对象集合的对象类型，用于生成sql语句时与对应的表进行绑定
     */
    @SuppressWarnings("unchecked")
    public <T> void deleteInBatch(Iterable<T> list) {
        Iterator<T> iterator = list.iterator();
        if (iterator.hasNext()) {
            T obj = iterator.next();
            getRepository(obj.getClass()).deleteInBatch(list);
        }
    }

    /**
     * 根据主键，查询单条
     *
     * @param clazz 查询的目标表对应实体类型，Entity
     * @param id    主键
     * @param <T>   查询的目标表对应实体类型
     * @return clazz类型对象
     */
    public <T> T findOne(Class<T> clazz, Object id) {
        return getEntityManager().find(clazz, id);
    }

    /**
     * 按照例子查询单条
     *
     * @param <T>    查询的表的映射实体类型
     * @param object 查询一句的例子对象
     * @return 返回查询结果
     */
    @SuppressWarnings("unchecked")
    public <T> T findOne(T object) {
        Example<T> example = Example.of(object);
        return (T) this.getRepository(object.getClass()).findOne(example).get();
    }

    /**
     * 根据sql查询出单条数据，并映射成指定clazz类型
     *
     * @param <T>        查询的表的映射实体类型
     * @param sql        sql
     * @param clazz      查询的目标表对应实体类型，Entity
     * @param parameters 对象数组格式的sql语句中的参数集合，使用?方式占位
     * @return 查询的结果
     */
    @SuppressWarnings("unchecked")
    public <T> T findOne(String sql, Class<T> clazz, Object... parameters) {
        Query query = creatClassQuery(sql, clazz, parameters);
        if (query.getResultList().size() == 0) {
            return null;
        }
        Object o = query.getSingleResult();
        return (T) o;
    }

    /**
     * 根据sql查询出单条数据，并映射成指定clazz类型
     *
     * @param <T>        查询的表的映射实体类型
     * @param sql        原生sql，使用{Map的key值}形式占位
     * @param clazz      查询的目标表对应实体类型，Entity
     * @param parameters Map形式参数集合
     * @return 查询的结果
     */
    public <T> T findOne(String sql, Class<T> clazz, Map<String, Object> parameters) {
        return findOne(SqlUtil.parserSQL(sql, parameters), clazz);
    }

    /**
     * 按照例子查询多条
     *
     * @param <T>    查询的表的映射实体类型
     * @param object 例子对象
     * @return 查询结果数据集合
     */
    public <T> List<T> findAll(T object) {
        List<T> result = findAll(object, Sort.unsorted());
        if (result != null) {
            return result;
        }
        return new ArrayList(0);
    }

    /**
     * 按照例子查询多条/排序
     *
     * @param <T>    查询的表的映射实体类型
     * @param object 例子对象
     * @param sort   排序对象
     * @return 查询结果数据集合
     */
    public <T> List<T> findAll(T object, Sort sort) {
        Example<T> example = Example.of(object);
        List<T> result = this.getRepository(object.getClass()).findAll(example, sort);
        if (result != null) {
            return result;
        }
        return new ArrayList(0);
    }

    /**
     * 按照例子查询多条分页
     *
     * @param <T>    查询的表的映射实体类型
     * @param object 例子对象
     * @param page   第几页
     * @param size   每页条数
     * @return 分页对象
     */
    public <T> Page findAll(T object, int page, int size) {
        return findAll(object, page, size, Sort.unsorted());
    }

    /**
     * 按照例子对象查询多条分页
     *
     * @param object 例子对象
     * @param <T>    查询的表的映射实体类型
     * @param page   第几页
     * @param size   每页条数
     * @param sort   排序对象
     * @return 分页信息
     */
    public <T> Page findAll(T object, int page, int size, Sort sort) {
        Example<T> example = Example.of(object);
        return this.getRepository(object.getClass()).findAll(example, PageRequest.of(page, size, sort));
    }

    /**
     * 根据sql语句查询指定类型clazz列表
     *
     * @param sql        查询的sql语句，参数使用？占位
     * @param clazz      希望查询结果映射成的实体类型
     * @param <T>        指定返回类型
     * @param parameters 对象数组类型的参数集合
     * @return 结果集
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(String sql, Class<T> clazz, Object... parameters) {
        return findAll(sql, clazz, null, null, parameters);
    }

    /**
     * 根据sql语句查询指定类型clazz列表
     *
     * @param sql        查询的sql语句，参数使用？占位
     * @param clazz      希望查询结果映射成的实体类型
     * @param <T>        指定返回类型
     * @param parameters 对象数组类型的参数集合
     * @return 结果集
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(String sql, Class<T> clazz, Integer firstResult, Integer maxResults, Object... parameters) {
        try {
            getIdField(clazz);
            Query query = creatClassQuery(sql, clazz, parameters);
            if (firstResult != null) {
                query.setFirstResult(firstResult);
            }
            if (maxResults != null) {
                query.setMaxResults(maxResults);
            }
            List result = query.getResultList();
            if (result != null) {
                return result;
            }
        } catch (NoSuchIDException e) {
            List<Map<String, Object>> list = findAllBySQL(sql, parameters);
            if (list != null && list.size() > 0) {
                List<T> result = new LinkedList<>();
                if (ClassUtil.canCastClass(clazz)) {
                    for (Map<String, Object> entity : list) {
                        T node = ObjectUtil.cast(clazz, ArrayUtil.getLast(entity.values().toArray()));
                        if (node != null) {
                            result.add(node);
                        }
                    }
                } else {
                    for (Map<String, Object> entity : list) {
                        T node = ObjectUtil.getObjectFromMap(clazz, entity);
                        if (node != null) {
                            result.add(node);
                        }
                    }
                }
                return result;
            }
        }
        return new ArrayList<>(0);
    }

    /**
     * 根据sql查询结果为List<clazz>类型的结果集
     *
     * @param sql        查询sql语句，使用{Map的key值}形式占位
     * @param clazz      查询结果需要映射的实体
     * @param parameters Map类型参数集合
     * @param <T>        查询结果需要映射的实体类型
     * @return List<clazz>类型的结果集
     */
    public <T> List<T> findAll(String sql, Class<T> clazz, Map<String, Object> parameters) {
        List result = findAll(SqlUtil.parserSQL(sql, parameters), clazz);
        if (result != null) {
            return result;
        }
        return new ArrayList<>(0);
    }

    /**
     * 分页查询
     *
     * @param sql        查询的sql语句
     * @param countSql   统计总数的sql语句
     * @param page       第几页
     * @param size       页大小
     * @param parameters 对象数组类型的参数集合
     * @return Page类型的查询结果
     */
    @SuppressWarnings("unchecked")
    public <T> Page<T> findPageBySQL(String sql, String countSql, int page, int size, Class<T> clazz, Object... parameters) {
        if (size <= 0) {
            new IllegalArgumentException().printStackTrace();
            return null;
        }
        PageImpl pageDate = null;
        PageRequest pageable;

        //sql格式化
        sql = sql.trim().replaceAll("[\t\r\n\\s]", " ");

        // 新建 MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();
        SQLSelectQueryBlock sqlSelectQueryBlock = ((SQLSelectStatement) statement).getSelect().getQueryBlock();

        List<Sort.Order> orderList = null;
        SQLOrderBy orderBy = sqlSelectQueryBlock.getOrderBy();
        if (orderBy != null) {
            List<SQLSelectOrderByItem> items = orderBy.getItems();
            if (items != null) {
                orderList = new ArrayList<>();
                for (SQLSelectOrderByItem item : items) {
                    String column = item.getExpr().toString();
                    if (item.getType() == null) {
                        orderList.add(Sort.Order.by(column));
                    } else {
                        Sort.Direction des = Sort.Direction.fromString(item.getType().name_lcase);
                        switch (des) {
                            case ASC:
                                orderList.add(Sort.Order.asc(column));
                                break;
                            case DESC:
                                orderList.add(Sort.Order.desc(column));
                                break;
                            default:
                        }
                    }
                }
            }
        }

        if (orderList != null && orderList.size() > 0) {
            pageable = PageRequest.of(page, size, Sort.by(orderList));
        } else {
            pageable = PageRequest.of(page, size, Sort.unsorted());
        }

        Query countQuery = creatQuery(countSql, parameters);
        int count = Integer.parseInt(countQuery.getSingleResult().toString());

        //取查询结果集
        if (count > 0) {
            List content;
            if (clazz != null) {
                content = findAll(sql, clazz, page * size, size, parameters);
            } else {
                Query query = creatQuery(sql, parameters);
                query.setFirstResult(page * size);
                query.setMaxResults(size);
                ((NativeQueryImpl) query).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
                content = query.getResultList();
            }

            pageDate = new PageImpl(content, pageable, count);
        }

        return pageDate;
    }

    /**
     * 分页查询，自提供条数汇总sql语句
     *
     * @param sql        原生sql，使用{Map的key值}形式占位
     * @param countSql   统计总数语句sql
     * @param page       第几页
     * @param size       页大小
     * @param parameters Map类型参数集合
     * @return 分页Page类型结果
     */
    public Page findPageBySQL(String sql, String countSql, int page, int size, Map<String, Object> parameters) {
        return findPageBySQL(SqlUtil.parserSQL(sql, parameters), SqlUtil.parserSQL(countSql, parameters), page, size, null, null);
    }

    /**
     * 分页查询，自动生成条数汇总sql语句
     *
     * @param sql        原生sql，参数使用{Map的key值}形式占位
     * @param page       第几页
     * @param size       页大小
     * @param parameters Map类型参数集合
     * @return 分页Page类型结果
     */
    public Page findPageBySQL(String sql, int page, int size, Map<String, Object> parameters) {
        return findPageBySQL(SqlUtil.parserSQL(sql, parameters), SqlUtil.parserCountSQL(sql, parameters), page, size, null);
    }

    /**
     * 分页查询，自动生成条数汇总sql语句
     *
     * @param sql        原生sql，参数使用?形式占位
     * @param page       第几页
     * @param size       页大小
     * @param parameters 对象数组类型参数集合
     * @return 分页Page类型结果
     */
    public <T> Page<T> findPageBySQL(String sql, int page, int size, Object... parameters) {
        return findPageBySQL(sql, SqlUtil.parserCountSQL(sql), page, size, null, parameters);
    }

    /**
     * 分页查询，自动生成条数汇总sql语句
     *
     * @param sql        原生sql，参数使用{Map的key值}形式占位
     * @param page       第几页
     * @param size       页大小
     * @param parameters Map类型参数集合
     * @return 分页Page类型结果
     */
    public <T> Page<T> findPageBySQL(String sql, int page, int size, Class<T> clazz, Object... parameters) {
        return findPageBySQL(sql, SqlUtil.parserCountSQL(sql), page, size, clazz, parameters);
    }

    /**
     * 分页查询，自动生成条数汇总sql语句
     *
     * @param sql        原生sql，参数使用{Map的key值}形式占位
     * @param page       第几页
     * @param size       页大小
     * @param parameters Map类型参数集合
     * @return 分页Page类型结果
     */
    public <T> Page<T> findPageBySQL(String sql, int page, int size, Class<T> clazz, Map<String, Object> parameters) {
        return findPageBySQL(SqlUtil.parserSQL(sql, parameters), SqlUtil.parserCountSQL(sql, parameters), page, size, clazz, null);
    }

    /**
     * 创建普通查询的Query对象
     *
     * @param sql        sql语句
     * @param parameters 对象数组形式参数集合
     * @return 完成设置参数的Query对象
     */
    private Query creatQuery(String sql, Object... parameters) {
        Query query = getEntityManager().createNativeQuery(sql);
        setParameter(query, parameters);
        return query;
    }

    /**
     * 创建返回结果为clazz的Query对象
     *
     * @param sql        sql语句
     * @param clazz      返回结果类型
     * @param parameters 对象数组形式参数集合
     * @return 完成设置参数的Query对象
     */
    private Query creatClassQuery(String sql, Class clazz, Object... parameters) {
        Query query = getEntityManager().createNativeQuery(sql, clazz);
        setParameter(query, parameters);
        return query;
    }

    /**
     * 设置查询参数
     *
     * @param query      query对象
     * @param parameters 参数集合
     */
    private void setParameter(Query query, Object... parameters) {
        if (parameters == null) {
            return;
        }
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }
    }

    /**
     * 根据sql语句查询列表，结果类型为List<Map<String, Object>>
     *
     * @param sql        查询的sql语句，参数使用？占位
     * @param parameters 对象数组形式参数集合
     * @return 结果类型为List套Map的查询结果
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> findAllBySQL(String sql, Object... parameters) {
        Query query = creatQuery(sql, parameters);
        ((NativeQueryImpl) query).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List result = query.getResultList();
        if (result != null) {
            return result;
        }
        return new ArrayList<>(0);
    }

    /**
     * 根据sql语句查询列表，结果类型为List<Map<String, Object>>
     *
     * @param sql        查询sql语句，参数使用{Map的key值}形式占位
     * @param parameters Map类型参数集合
     * @return 结果类型为List套Map的查询结果
     */
    public List<Map<String, Object>> findAllBySQL(String sql, Map<String, Object> parameters) {
        List result = findAllBySQL(SqlUtil.parserSQL(sql, parameters));
        if (result != null) {
            return result;
        }
        return new ArrayList<>(0);
    }

    /**
     * 查询结果预判为一个字段值
     *
     * @param sql        查询的sql语句，参数使用？占位
     * @param parameters 对象数组形式参数集合
     * @return 结果为一个查询字段值
     */
    @SuppressWarnings("unchecked")
    public Object findParameter(String sql, Object... parameters) {
        Query query = creatQuery(sql, parameters);
        return query.getSingleResult();
    }

    /**
     * 查询结果预判为一个字段值
     *
     * @param sql        查询sql语句，参数使用{Map的key值}形式占位
     * @param parameters Map类型参数集合
     * @return 结果为一个查询字段值
     */
    public Object findParameter(String sql, Map<String, Object> parameters) {
        return findParameter(SqlUtil.parserSQL(sql, parameters));
    }

    /**
     * sql形式写操作
     *
     * @param sql        查询的sql语句，参数使用？占位
     * @param parameters 对象数组形式参数集合
     * @return 影响条数
     */
    public int updateBySQL(String sql, Object... parameters) {
        Query query = creatQuery(sql, parameters);
        return query.executeUpdate();
    }

    /**
     * sql形式写操作
     *
     * @param sql        查询sql语句，参数使用{Map的key值}形式占位
     * @param parameters Map类型参数集合
     * @return 影响条数
     */
    public int updateBySQL(String sql, Map<String, Object> parameters) {
        return updateBySQL(SqlUtil.parserSQL(sql, parameters));
    }

    /**
     * 根据实体类型tableClass与主键值集合ids，查询实体列表
     *
     * @param tableClass 查询的目标表对应实体类型，Entity
     * @param ids        主键值集合
     * @param <T>        目标表对应实体类型
     * @param <ID>       主键类型
     * @return 返回查询出的实体列表
     */
    @SuppressWarnings("unchecked")
    public <T, ID> List<T> findAllById(Class<T> tableClass, Iterable<ID> ids) {
        List result = getRepository(tableClass).findAllById(ids);
        if (result != null) {
            return result;
        }
        return new ArrayList<>(0);
    }

    /**
     * 根据实体类型tableClass与主键值集合ids，查询实体列表
     *
     * @param tableClass 查询的目标表对应实体类型，Entity
     * @param ids        主键值集合，数组类型
     * @param <T>        目标表对应实体类型
     * @param <ID>       主键类型
     * @return 返回查询出的实体列表
     */
    @SuppressWarnings("unchecked")
    public <T, ID> List<T> findAllByArrayId(Class<T> tableClass, ID... ids) {
        List result = getRepository(tableClass).findAllById(ArrayUtil.asList(ids));
        if (result != null) {
            return result;
        }
        return new ArrayList<>(0);
    }

    /**
     * 查询指定tableClass对应表的全表分页
     *
     * @param tableClass 查询的目标表对应实体类型，Entity
     * @param page       第几页
     * @param size       页大小
     * @param <T>        目标表对应实体类型
     * @return 内容为实体的Page类型分页结果
     */
    @SuppressWarnings("unchecked")
    public <T> Page<T> findAll(Class<T> tableClass, int page, int size) {
        return getRepository(tableClass).findAll(PageRequest.of(page, size));
    }

    /**
     * 指定tableClass对应表的全表查询
     *
     * @param tableClass 查询的目标表对应实体类型，Entity
     * @param <T>        目标表对应实体类型
     * @return 内容为实体的List类型结果集
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Class<T> tableClass) {
        List result = getRepository(tableClass).findAll();
        if (result != null) {
            return result;
        }
        return new ArrayList<>(0);
    }

    /**
     * 指定tableClass对应表的全表查询,并排序
     *
     * @param tableClass 查询的目标表对应实体类型，Entity
     * @param sort       排序信息
     * @param <T>        目标表对应实体类型
     * @return 内容为实体的List类型结果集
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Class<T> tableClass, Sort sort) {
        List result = getRepository(tableClass).findAll(sort);
        if (result != null) {
            return result;
        }
        return new ArrayList<>(0);
    }

    /**
     * 查询指定tableClass对应表的总数
     *
     * @param tableClass 查询的目标表对应实体类型，Entity
     * @return 查询条数
     */
    @SuppressWarnings("unchecked")
    public long count(Class tableClass) {
        return getRepository(tableClass).count();
    }
}
