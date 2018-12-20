package com.agile.mvc.service;

import com.agile.common.annotation.Mapping;
import com.agile.common.mvc.service.BusinessService;
import com.agile.mvc.entity.DictionaryDataEntity;
import org.springframework.stereotype.Service;

/**
 * Created by 佟盟
 */
@Service
public class DictionaryDataService extends BusinessService<DictionaryDataEntity> {
    @Mapping(name = "asd")
    public void test() {
        this.logger.error("出错了", new NullPointerException());
    }

    @Mapping(name = "asd1")
    public void test1() {
        this.logger.error("出错了", new NullPointerException());
    }
}
