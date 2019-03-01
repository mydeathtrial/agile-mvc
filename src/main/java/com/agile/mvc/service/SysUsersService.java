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
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author agile generator
 */
@Api(description = "[系统管理]用户")
@Mapping("/api/sys")
@Service
public class SysUsersService extends BusinessService<SysUsersEntity> {
    @ApiOperation(value = "新增[系统管理]用户", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysUsersEntity")
    })
    @Models({SysUsersEntity.class})
    @Validate(beanClass = SysUsersEntity.class, validateGroups = Insert.class)
    @Mapping(value = "/sys-users", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @ApiOperation(value = "删除[系统管理]用户", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Validate(beanClass = SysUsersEntity.class, validateGroups = Delete.class)
    @Mapping(path = "/sys-users/{id}", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @ApiOperation(value = "更新[系统管理]用户", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysUsersEntity"),
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysUsersEntity.class})
    @Validate(beanClass = SysUsersEntity.class, validateGroups = Update.class)
    @Mapping(value = "/sys-users/{id}", method = RequestMethod.PUT)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @ApiOperation(value = "[系统管理]用户分页查询", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysUsersEntity"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "body", dataType = "String[]")
    })
    @Models({SysUsersEntity.class})
    @Mapping(path = "/sys-users/{pageNum}/{pageSize}", method = RequestMethod.POST)
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @ApiOperation(value = "[系统管理]用户查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysUsersId", value = "唯一标识", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sysDepartId", value = "部门主键", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "saltKey", value = "账号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "saltValueOld", value = "旧密码", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "saltValue", value = "密码", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "用户姓名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "areaId", value = "地区编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "expiredTime", value = "过期时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "isLocked", value = "用户是否锁定", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "onLineStrategy", value = "同时在线策略", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "enabled", value = "是否可用", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "leader", value = "直属领导", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sex", value = "员工性别 0:男 1:女", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "telephone", value = "联系电话", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "email", value = "电子邮箱", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "createTime", value = "创建时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "updateTime", value = "修改时间", paramType = "query", dataType = "String"),
    })
    @Models({SysUsersEntity.class})
    @Mapping(path = "/sys-users")
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @ApiOperation(value = "根据主键查询[系统管理]用户", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysUsersEntity.class})
    @Mapping(path = "/sys-users/{id}", method = RequestMethod.GET)
    public RETURN customQueryById() {
        return super.queryById();
    }

}
