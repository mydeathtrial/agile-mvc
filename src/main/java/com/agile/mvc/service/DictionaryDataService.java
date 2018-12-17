package com.agile.mvc.service;

import com.agile.common.annotation.Mapping;
import com.agile.common.base.RETURN;
import com.agile.common.mvc.service.BusinessService;
import com.agile.common.util.RandomStringUtil;
import com.agile.mvc.entity.DictionaryDataEntity;
import com.agile.mvc.entity.SysUsersEntity;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

/**
 * Created by 佟盟
 */
@Service
@Api(value = "UserController", description = "用户相关api")
@Mapping("/apis")
public class DictionaryDataService extends BusinessService<DictionaryDataEntity> {
    @Mapping("/test")
    @ApiOperation(value = "查找用户", notes = "查找用户", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "blogArticleBeen", value = "文档对象", required = true, paramType = "body", dataType = "DictionaryDataEntity"),
            @ApiImplicitParam(name = "path", value = "url上的数据", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "query", value = "query类型参数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "apiKey", value = "header中的数据", required = true, paramType = "header", dataType = "String")
    })
    @ApiResponses({@ApiResponse(code = 200,message = "成功",response = DictionaryDataEntity.class)})
    public Object test() {
        return dao.saveAndReturn(SysUsersEntity.builder().setName("tudou").setSysUsersId(RandomStringUtil.getRandom(8, RandomStringUtil.Random.MIX_1)).setSaltKey("111").setSaltValue("111").build());
    }

    public Object test1(){
        this.logger.info("11111111111111111111111111");
        dao.findOne(SysUsersEntity.class,"1");
        respository.get("1");
        setOutParam("key",respository.get0("1"));
        return RETURN.SUCCESS;
    }

    @Autowired
    Respository respository;
}
