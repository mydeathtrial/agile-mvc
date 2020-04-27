package com.agile.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.agile.common.util.string.StringUtil;
/**
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class TreeUtil {

    public static <T> List<T> createTree(List<T> list, String key, String parentKey, String childrenKey, String rootValue) throws NoSuchFieldException, IllegalAccessException {
        return createTree(list, key, parentKey, childrenKey, null, rootValue);
    }

    /**
     * 构建树形结构
     *
     * @param list 构建源数据
     * @return 树形结构数据集
     */
    public static <T> List<T> createTree(List<T> list, String key, String parentKey, String childrenKey, String sortKey, String rootValue) throws NoSuchFieldException, IllegalAccessException {
        List<T> roots = new ArrayList<>();
        Field keyField = null;
        Field parentKeyField = null;
        Field childrenKeyField = null;
        for (T entity : list) {
            if (keyField == null) {
                keyField = entity.getClass().getDeclaredField(key);
                keyField.setAccessible(true);
            }
            if (parentKeyField == null) {
                parentKeyField = entity.getClass().getDeclaredField(parentKey);
                parentKeyField.setAccessible(true);
            }
            if (childrenKeyField == null) {
                childrenKeyField = entity.getClass().getDeclaredField(childrenKey);
                childrenKeyField.setAccessible(true);
            }
            if (rootValue.equals(parentKeyField.get(entity))) {
                childrenKeyField.set(entity, createChildren(entity, list, keyField, parentKeyField, childrenKeyField, sortKey));
                roots.add(entity);
            }
        }
        if (StringUtil.isBlank(sortKey)) {
            return roots;
        }
        CollectionsUtil.sort(roots, sortKey);
        return roots;
    }

    private static <T> List<T> createChildren(T parent, List<T> list, Field keyField, Field parentKeyField, Field childrenKeyField, String sortKey) throws IllegalAccessException {
        List<T> children = new ArrayList<>();
        for (T entity : list) {
            if (keyField.get(parent).equals(parentKeyField.get(entity))) {
                childrenKeyField.set(entity, createChildren(entity, list, keyField, parentKeyField, childrenKeyField, sortKey));
                children.add(entity);
            }
        }
        if (StringUtil.isBlank(sortKey)) {
            return children;
        }
        CollectionsUtil.sort(children, sortKey);
        return children;
    }

}
