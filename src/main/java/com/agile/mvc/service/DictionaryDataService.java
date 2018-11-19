package com.agile.mvc.service;

import com.agile.common.annotation.Mapping;
import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.base.DemoModel;
import com.agile.common.validate.ValidateType;
import com.agile.common.base.RETURN;
import com.agile.common.mvc.service.BusinessService;
import com.agile.mvc.entity.DictionaryDataEntity;
import org.springframework.stereotype.Service;

/**
 * Created by 佟盟
 */
@Service
public class DictionaryDataService extends BusinessService<DictionaryDataEntity> {
    @Mapping("/test")
    @Validates({
            @Validate(value = "param1",validateType = ValidateType.EMAIL,max_size = 30,min_size = 10),
            @Validate(value = "param2",validateType = ValidateType.NUMBER,nullable = false,max = 100,min = 30),
            @Validate(value = "param3",beanClass = DemoModel.class),
            @Validate(value = "param4",validateRegex = "[123]",validateMsg = "参数错误啊"),
            @Validate(value = "param5",validateType = ValidateType.EMAIL,validateRegex = "[123]",validateMsg = "参数错误啊")
    })
    public Object test(){
        return RETURN.SUCCESS;
    }
}
