package com.agile.common.mvc.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.support.MappingElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * Created by 佟盟 on 2018/10/21
 */
@Component
public class ESDao {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public <T>ElasticsearchRepository getRepository(Class<T> entityClass){
        ElasticsearchPersistentEntity<?> entity = elasticsearchOperations.getElasticsearchConverter().getMappingContext().getRequiredPersistentEntity(entityClass);
        return new SimpleElasticsearchRepository<T>(new MappingElasticsearchEntityInformation(entity),template);
    }
}
