package com.agile.mvc.service;

import com.agile.common.annotation.Mapping;
import com.agile.common.annotation.Models;
import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.mvc.service.BusinessService;
import com.agile.common.util.TreeUtil;
import com.agile.mvc.entity.SysRolesEntity;
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

import java.util.List;

/**
 * @author agile generator
 */
@Api(description = "[系统管理]角色")
@Mapping("/api/sys")
@Service
public class SysRolesService extends BusinessService<SysRolesEntity> {
    @ApiOperation(value = "新增[系统管理]角色", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysRolesEntity")
    })
    @Models({SysRolesEntity.class})
    @Validate(beanClass = SysRolesEntity.class, validateGroups = Insert.class)
    @Mapping(value = "/sys-roles", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @ApiOperation(value = "删除[系统管理]角色", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Validate(beanClass = SysRolesEntity.class, validateGroups = Delete.class)
    @Mapping(path = "/sys-roles/{id}", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @ApiOperation(value = "更新[系统管理]角色", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysRolesEntity"),
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysRolesEntity.class})
    @Validate(beanClass = SysRolesEntity.class, validateGroups = Update.class)
    @Mapping(value = "/sys-roles/{id}", method = RequestMethod.PUT)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @ApiOperation(value = "[系统管理]角色分页查询", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysRolesEntity"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "body", dataType = "String[]")
    })
    @Models({SysRolesEntity.class})
    @Mapping(path = "/sys-roles/{pageNum}/{pageSize}", method = RequestMethod.POST)
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @ApiOperation(value = "[系统管理]角色查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysRolesId", value = "角色唯一标识", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "roleName", value = "角色名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "roleDesc", value = "角色说明", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "enable", value = "是否可用", paramType = "query", dataType = "boolean"),
    })
    @Models({SysRolesEntity.class})
    @Mapping(path = "/sys-roles")
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @ApiOperation(value = "根据主键查询[系统管理]角色", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysRolesEntity.class})
    @Mapping(path = "/sys-roles/{id}", method = RequestMethod.GET)
    public RETURN customQueryById() {
        return super.queryById();
    }

    @ApiOperation(value = "查询角色树", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Mapping(path = "/sys-roles/tree", method = RequestMethod.GET)
    public Object roles() throws NoSuchFieldException, IllegalAccessException {
        List<SysRolesEntity> list = dao.findAll(SysRolesEntity.builder().enable(true).parentId(null).build());
        List<SysRolesEntity> tree = TreeUtil.createTree(list, "sysRolesId", "parentId", "children", "sort", "root");
        setOutParam("roles", tree);
        return RETURN.SUCCESS;
    }
}
