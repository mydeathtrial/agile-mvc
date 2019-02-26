package com.agile.mvc.service;

import com.agile.common.annotation.Mapping;
import com.agile.common.annotation.Models;
import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.mvc.service.BusinessService;
import org.springframework.stereotype.Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;
import com.agile.mvc.entity.SysBtUsersRolesEntity;

/**
 * @author agile generator
 */
@Api(description = "[系统管理]用户角色表")
@Mapping("/api/sys-bt-users-roles")
@Service
public class SysBtUsersRolesService extends BusinessService<SysBtUsersRolesEntity> {
    @ApiOperation(value = "新增[系统管理]用户角色表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysBtUsersRolesEntity")
    })
    @Models({SysBtUsersRolesEntity.class})
    @Validate(beanClass = SysBtUsersRolesEntity.class, validateGroups = Insert.class)
    @Mapping(value = "/sys-bt-users-roles", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @ApiOperation(value = "删除[系统管理]用户角色表", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Validate(beanClass = SysBtUsersRolesEntity.class, validateGroups = Delete.class)
    @Mapping(path = "/sys-bt-users-roles/{id}", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @ApiOperation(value = "更新[系统管理]用户角色表", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysBtUsersRolesEntity"),
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysBtUsersRolesEntity.class})
    @Validate(beanClass = SysBtUsersRolesEntity.class, validateGroups = Update.class)
    @Mapping(value = "/sys-bt-users-roles/{id}", method = RequestMethod.PUT)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @ApiOperation(value = "[系统管理]用户角色表分页查询", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysBtUsersRolesEntity"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "body", dataType = "String[]")
    })
    @Models({SysBtUsersRolesEntity.class})
    @Mapping(path = "/sys-bt-users-roles/{pageNum}/{pageSize}", method = RequestMethod.POST)
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @ApiOperation(value = "[系统管理]用户角色表查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysBtUsersRolesId", value = "唯一标识", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "roleId", value = "角色唯一标识", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户唯一标识", paramType = "query", dataType = "String"),
    })
    @Models({SysBtUsersRolesEntity.class})
    @Mapping(path = "/sys-bt-users-roles")
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @ApiOperation(value = "根据主键查询[系统管理]用户角色表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysBtUsersRolesEntity.class})
    @Mapping(path = "/sys-bt-users-roles/{id}", method = RequestMethod.GET)
    public RETURN customQueryById() {
        return super.queryById();
    }
}
