package com.agile.common.mvc.service;

import com.agile.common.annotation.Init;
import com.agile.common.base.Constant;
import com.agile.common.properties.DictionaryProperties;
import com.agile.common.util.CollectionsUtil;
import com.agile.common.util.DictionaryUtil;
import com.agile.common.util.TreeUtil;
import com.agile.mvc.entity.DictionaryDataEntity;
import com.google.common.collect.Maps;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2019/3/18 18:30
 * 描述 字典服务
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(value = {DictionaryProperties.class})
@ConditionalOnProperty(name = "enable", prefix = "agile.dictionary", havingValue = "true")
public class DictionaryService extends MainService {
    @Init
    public void synchronousCache() throws NoSuchFieldException, IllegalAccessException {
        List<DictionaryDataEntity> list = dao.findAll(DictionaryDataEntity.class);
        TreeUtil.createTree(list,
                "dictionaryDataId",
                "parentId",
                "children",
                "code",
                "root",
                "fullName",
                "fullCode"
        );
        Map<String, DictionaryDataEntity> codeMap = Maps.newHashMap();
        for (DictionaryDataEntity entity : list) {
            coverCacheCode(entity);
            codeMap.put(entity.getFullCode(), entity);
        }
        DictionaryUtil.getCache().put("codeMap", codeMap);
    }

    /**
     * 递归处理所有字典的CacheCode
     *
     * @param entity 字典实体
     */
    private void coverCacheCode(DictionaryDataEntity entity) {
        List<DictionaryDataEntity> children = entity.getChildren();
        if (children == null || children.isEmpty()) {
            return;
        }
        for (DictionaryDataEntity child : children) {
            coverCacheCode(child);
            entity.addCodeCache(child.getCode(), child);
        }
    }

    private static List<DictionaryDataEntity> createChildren(DictionaryDataEntity parentNode, List<DictionaryDataEntity> list) throws IllegalAccessException {
        List<DictionaryDataEntity> children = new ArrayList<>();
        for (DictionaryDataEntity currentNode : list) {
            if (parentNode.getCode().equals(currentNode.getCode())) {
                children.add(currentNode);

                initFullField(parentNode, currentNode);
                currentNode.setChildren(createChildren(currentNode, list));
            }
        }
        if (children.isEmpty()) {
            return children;
        }
        CollectionsUtil.sort(children, "code");
        return children;
    }

    private static void initFullField(DictionaryDataEntity parentNode, DictionaryDataEntity currentNode) {
        String parentName = parentNode.getFullName();
        if (parentName == null) {
            return;
        }
        currentNode.setFullName(parentName + Constant.RegularAbout.SPOT + currentNode.getFullName());

        String parentCode = parentNode.getFullCode();
        if (parentCode == null) {
            return;
        }
        currentNode.setFullCode(parentCode + Constant.RegularAbout.SPOT + currentNode.getFullCode());
    }
}
