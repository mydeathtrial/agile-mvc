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
import com.agile.mvc.entity.SysBtTaskTargetEntity;

/**
 * @author agile generator
 */
@Api(description = "[系统管理]定时任务目标任务表")
@Mapping("/api/sys")
@Service
public class SysBtTaskTargetService extends BusinessService<SysBtTaskTargetEntity> {
    @ApiOperation(value = "新增[系统管理]定时任务目标任务表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysBtTaskTargetEntity")
    })
    @Models({SysBtTaskTargetEntity.class})
    @Validate(beanClass = SysBtTaskTargetEntity.class, validateGroups = Insert.class)
    @Mapping(value = "/sys-bt-task-target", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @ApiOperation(value = "删除[系统管理]定时任务目标任务表", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Validate(beanClass = SysBtTaskTargetEntity.class, validateGroups = Delete.class)
    @Mapping(path = "/sys-bt-task-target/{id}", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @ApiOperation(value = "更新[系统管理]定时任务目标任务表", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysBtTaskTargetEntity"),
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysBtTaskTargetEntity.class})
    @Validate(beanClass = SysBtTaskTargetEntity.class, validateGroups = Update.class)
    @Mapping(value = "/sys-bt-task-target/{id}", method = RequestMethod.PUT)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @ApiOperation(value = "[系统管理]定时任务目标任务表分页查询", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysBtTaskTargetEntity"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "body", dataType = "String[]")
    })
    @Models({SysBtTaskTargetEntity.class})
    @Mapping(path = "/sys-bt-task-target/{pageNum}/{pageSize}", method = RequestMethod.POST)
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @ApiOperation(value = "[系统管理]定时任务目标任务表查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysBtTaskTargetId", value = "主键", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sysTaskId", value = "定时任务标志", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sysTaskTargetId", value = "目标方法主键", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "order", value = "优先级", paramType = "query", dataType = "int"),
    })
    @Models({SysBtTaskTargetEntity.class})
    @Mapping(path = "/sys-bt-task-target")
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @ApiOperation(value = "根据主键查询[系统管理]定时任务目标任务表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysBtTaskTargetEntity.class})
    @Mapping(path = "/sys-bt-task-target/{id}", method = RequestMethod.GET)
    public RETURN customQueryById() {
        return super.queryById();
    }
}
