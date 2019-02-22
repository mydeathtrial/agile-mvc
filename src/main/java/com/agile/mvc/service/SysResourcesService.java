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
import com.agile.mvc.entity.SysResourcesEntity;

/**
 * @author agile generator
 */
@Api(description = "[系统管理]资源")
@Mapping("/api/SysResourcesService")
@Service
public class SysResourcesService extends BusinessService<SysResourcesEntity> {
    @ApiOperation(value = "新增[系统管理]资源", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysResourcesEntity")
    })
    @Models({SysResourcesEntity.class})
    @Validate(beanClass = SysResourcesEntity.class, validateGroups = Insert.class)
    @Mapping(value = "/save", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @ApiOperation(value = "删除[系统管理]资源", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Validate(beanClass = SysResourcesEntity.class, validateGroups = Delete.class)
    @Mapping(path = "/{id}/delete", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @ApiOperation(value = "更新[系统管理]资源", httpMethod = "UPDATE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysResourcesEntity"),
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysResourcesEntity.class})
    @Validate(beanClass = SysResourcesEntity.class, validateGroups = Update.class)
    @Mapping(value = "/{id}/update", method = RequestMethod.POST)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @ApiOperation(value = "[系统管理]资源分页查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysResourcesId", value = "唯一标识", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "资源类型", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "资源名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "desc", value = "资源描述", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "path", value = "资源路径", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "priority", value = "优先级", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "enable", value = "是否可用", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "issys", value = "是否系统权限", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "moduleId", value = "模块", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "query", dataType = "String")
    })
    @Models({SysResourcesEntity.class})
    @Mapping(path = "/pageQuery")
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @ApiOperation(value = "[系统管理]资源查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysResourcesId", value = "唯一标识", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "资源类型", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "资源名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "desc", value = "资源描述", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "path", value = "资源路径", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "priority", value = "优先级", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "enable", value = "是否可用", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "issys", value = "是否系统权限", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "moduleId", value = "模块", paramType = "query", dataType = "String"),
    })
    @Models({SysResourcesEntity.class})
    @Mapping(path = "/query")
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @ApiOperation(value = "根据主键查询[系统管理]资源", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysResourcesEntity.class})
    @Mapping(path = "/{id}")
    public RETURN customQueryById() {
        return super.queryById();
    }
}
