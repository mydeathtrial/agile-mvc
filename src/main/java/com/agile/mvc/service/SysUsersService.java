package com.agile.mvc.service;

import com.agile.common.annotation.Mapping;
import com.agile.common.annotation.Models;
import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.mvc.service.BusinessService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import io.swagger.annotations.Api;
import com.agile.mvc.entity.SysUsersEntity;
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
    @Validates({
            @Validate(value = "saltKey", nullable = false, validateMsgKey = "账号不能为空"),
            @Validate(value = "saltValue", nullable = false, validateMsgKey = "密码不能为空"),
            @Validate(value = "saltValue ", validateRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$", validateMsgKey = "密码由6-21字母和数字"),
            @Validate(value = "name", nullable = false, validateMsgKey = "用户姓名不能为空"),
    })
    public Object customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }
}
