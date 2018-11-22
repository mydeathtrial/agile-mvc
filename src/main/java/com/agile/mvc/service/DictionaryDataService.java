package com.agile.mvc.service;

import com.agile.common.exception.NoSuchIDException;
import com.agile.common.mvc.service.BusinessService;
import com.agile.common.util.RandomStringUtil;
import com.agile.mvc.entity.DictionaryDataEntity;
import com.agile.mvc.entity.SysUsersEntity;
import org.springframework.stereotype.Service;

/**
 * Created by 佟盟
 */
@Service
public class DictionaryDataService extends BusinessService<DictionaryDataEntity> {
    public Object test() throws NoSuchIDException {
        return dao.saveAndReturn(SysUsersEntity.builder().setName("tudou").setSysUsersId(RandomStringUtil.getRandom(8, RandomStringUtil.Random.MIX_2)).setSaltKey("111").setSaltValue("111").build());
    }
}
