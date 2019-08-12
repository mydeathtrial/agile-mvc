package com.agile.common.mvc.service;

import com.agile.common.annotation.Init;
import com.agile.common.properties.DictionaryProperties;
import com.agile.common.util.DictionaryUtil;
import com.agile.common.util.TreeUtil;
import com.agile.mvc.entity.DictionaryDataEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author 佟盟
 * 日期 2019/3/18 18:30
 * 描述 TODO
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
        List<DictionaryDataEntity> tree = TreeUtil.createTree(list, "dictionaryDataId", "parentId", "children", "root");
        for (DictionaryDataEntity entity : tree) {
            coverCacheCode(entity);
            if ("root".equals(entity.getParentId())) {
                DictionaryUtil.getCache().put(entity.getCode(), entity);
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
