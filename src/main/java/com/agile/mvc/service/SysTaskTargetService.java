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
import com.agile.mvc.entity.SysTaskTargetEntity;

/**
 * @author agile generator
 */
@Api(description = "[系统管理]目标任务表")
@Mapping("/api/SysTaskTargetService")
@Service
public class SysTaskTargetService extends BusinessService<SysTaskTargetEntity> {
    @ApiOperation(value = "新增[系统管理]目标任务表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysTaskTargetEntity")
    })
    @Models({SysTaskTargetEntity.class})
    @Validate(beanClass = SysTaskTargetEntity.class, validateGroups = Insert.class)
    @Mapping(value = "/save", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @ApiOperation(value = "删除[系统管理]目标任务表", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Validate(beanClass = SysTaskTargetEntity.class, validateGroups = Delete.class)
    @Mapping(path = "/{id}/delete", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @ApiOperation(value = "更新[系统管理]目标任务表", httpMethod = "UPDATE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysTaskTargetEntity"),
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysTaskTargetEntity.class})
    @Validate(beanClass = SysTaskTargetEntity.class, validateGroups = Update.class)
    @Mapping(value = "/{id}/update", method = RequestMethod.POST)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @ApiOperation(value = "[系统管理]目标任务表分页查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysTaskTargetId", value = "唯一标识", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "方法含义名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "targetPackage", value = "包名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "targetClass", value = "类名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "targetMethod", value = "方法名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "remarks", value = "备注", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "query", dataType = "String")
    })
    @Models({SysTaskTargetEntity.class})
    @Mapping(path = "/pageQuery")
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @ApiOperation(value = "[系统管理]目标任务表查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysTaskTargetId", value = "唯一标识", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "方法含义名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "targetPackage", value = "包名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "targetClass", value = "类名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "targetMethod", value = "方法名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "remarks", value = "备注", paramType = "query", dataType = "String"),
    })
    @Models({SysTaskTargetEntity.class})
    @Mapping(path = "/query")
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @ApiOperation(value = "根据主键查询[系统管理]目标任务表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysTaskTargetEntity.class})
    @Mapping(path = "/{id}")
    public RETURN customQueryById() {
        return super.queryById();
    }
}
