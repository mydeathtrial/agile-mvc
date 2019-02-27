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
import com.agile.mvc.entity.LogValueEntity;

/**
 * @author agile generator
 */
@Api(description = "[系统管理]日志相关字段值变动信息")
@Mapping("/api/sys")
@Service
public class LogValueService extends BusinessService<LogValueEntity> {
    @ApiOperation(value = "新增[系统管理]日志相关字段值变动信息", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "LogValueEntity")
    })
    @Models({LogValueEntity.class})
    @Validate(beanClass = LogValueEntity.class, validateGroups = Insert.class)
    @Mapping(value = "/log-value", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @ApiOperation(value = "删除[系统管理]日志相关字段值变动信息", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Validate(beanClass = LogValueEntity.class, validateGroups = Delete.class)
    @Mapping(path = "/log-value/{id}", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @ApiOperation(value = "更新[系统管理]日志相关字段值变动信息", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "LogValueEntity"),
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({LogValueEntity.class})
    @Validate(beanClass = LogValueEntity.class, validateGroups = Update.class)
    @Mapping(value = "/log-value/{id}", method = RequestMethod.PUT)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @ApiOperation(value = "[系统管理]日志相关字段值变动信息分页查询", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "LogValueEntity"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "body", dataType = "String[]")
    })
    @Models({LogValueEntity.class})
    @Mapping(path = "/log-value/{pageNum}/{pageSize}", method = RequestMethod.POST)
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @ApiOperation(value = "[系统管理]日志相关字段值变动信息查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logValueId", value = "唯一标识", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "logTableId", value = "日志相关表标识", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "columnName", value = "字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "columnType", value = "字段类型", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "newValue", value = "新值", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "oldValue", value = "旧值", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "columnInfo", value = "字段含义", paramType = "query", dataType = "String"),
    })
    @Models({LogValueEntity.class})
    @Mapping(path = "/log-value")
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @ApiOperation(value = "根据主键查询[系统管理]日志相关字段值变动信息", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({LogValueEntity.class})
    @Mapping(path = "/log-value/{id}", method = RequestMethod.GET)
    public RETURN customQueryById() {
        return super.queryById();
    }
}
