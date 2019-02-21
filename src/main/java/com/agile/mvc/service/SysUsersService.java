package com.agile.mvc.service;

import com.agile.common.annotation.Mapping;
import com.agile.common.annotation.Models;
import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.mvc.service.BusinessService;
import com.agile.mvc.entity.SysUsersEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hibernate.sql.Insert;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author agile generator
 */
@Api(value = "[系统管理]用户")
@Service
@Mapping("/api/sysuser")
public class SysUsersService extends BusinessService<SysUsersEntity> {
    @ApiOperation(value = "创建系统用户", notes = "创建系统用户", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysUsersEntity")
    })
    @Mapping(value = "/save", method = RequestMethod.POST)
    @Models({SysUsersEntity.class})
    @Validate(beanClass = SysUsersEntity.class, validateGroups = Insert.class)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @Mapping(path = "/delete", method = RequestMethod.DELETE)
    @Validate(value = "sysUsersId", nullable = false, validateMsgKey = "主键不能为空")
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @Mapping(path = "/update")
    @Validates({
        @Validate(value = "pageSize", nullable = false, validateMsgKey = "ye"),
        @Validate(value = "pageNum", nullable = false, validateMsgKey = "账号不能为空")
    })
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }
}
