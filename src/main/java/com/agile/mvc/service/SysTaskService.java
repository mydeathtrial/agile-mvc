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
import com.agile.mvc.entity.SysTaskEntity;

/**
 * @author agile generator
 */
@Api(description = "[系统管理]定时任务")
@Mapping("/api/SysTaskService")
@Service
public class SysTaskService extends BusinessService<SysTaskEntity> {
    @ApiOperation(value = "新增[系统管理]定时任务", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysTaskEntity")
    })
    @Models({SysTaskEntity.class})
    @Validate(beanClass = SysTaskEntity.class, validateGroups = Insert.class)
    @Mapping(value = "/save", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @ApiOperation(value = "删除[系统管理]定时任务", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Validate(beanClass = SysTaskEntity.class, validateGroups = Delete.class)
    @Mapping(path = "/{id}/delete", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @ApiOperation(value = "更新[系统管理]定时任务", httpMethod = "UPDATE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysTaskEntity"),
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysTaskEntity.class})
    @Validate(beanClass = SysTaskEntity.class, validateGroups = Update.class)
    @Mapping(value = "/{id}/update", method = RequestMethod.POST)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @ApiOperation(value = "[系统管理]定时任务分页查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysTaskId", value = "主键", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "定时任务名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "state", value = "状态", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "cron", value = "定时表达式", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sync", value = "是否同步", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "updateTime", value = "更新时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "createTime", value = "创建时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "query", dataType = "String")
    })
    @Models({SysTaskEntity.class})
    @Mapping(path = "/pageQuery")
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @ApiOperation(value = "[系统管理]定时任务查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysTaskId", value = "主键", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "定时任务名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "state", value = "状态", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "cron", value = "定时表达式", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sync", value = "是否同步", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "updateTime", value = "更新时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "createTime", value = "创建时间", paramType = "query", dataType = "String"),
    })
    @Models({SysTaskEntity.class})
    @Mapping(path = "/query")
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @ApiOperation(value = "根据主键查询[系统管理]定时任务", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysTaskEntity.class})
    @Mapping(path = "/{id}")
    public RETURN customQueryById() {
        return super.queryById();
    }
}
