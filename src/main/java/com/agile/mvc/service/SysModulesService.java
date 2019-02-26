package com.agile.mvc.service;

import com.agile.common.annotation.Mapping;
import com.agile.common.annotation.Models;
import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.base.Constant;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.mvc.service.BusinessService;
import com.agile.mvc.entity.SysModulesEntity;
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

import java.util.HashMap;
import java.util.List;

/**
 * @author agile generator
 */
@Api(description = "[系统管理]模块")
@Mapping("/api/sys")
@Service
public class SysModulesService extends BusinessService<SysModulesEntity> {
    @ApiOperation(value = "新增[系统管理]模块", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysModulesEntity")
    })
    @Models({SysModulesEntity.class})
    @Validate(beanClass = SysModulesEntity.class, validateGroups = Insert.class)
    @Mapping(value = "/modules", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @ApiOperation(value = "删除[系统管理]模块", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Validate(beanClass = SysModulesEntity.class, validateGroups = Delete.class)
    @Mapping(path = "/modules/{id}", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @ApiOperation(value = "更新[系统管理]模块", httpMethod = "UPDATE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysModulesEntity"),
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysModulesEntity.class})
    @Validate(beanClass = SysModulesEntity.class, validateGroups = Update.class)
    @Mapping(value = "/modules/{id}", method = RequestMethod.PUT)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @ApiOperation(value = "[系统管理]模块分页查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysModulesEntity"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "body", dataType = "String[]")
    })
    @Models({SysModulesEntity.class})
    @Mapping(path = "/modules/{pageNum}/{pageSize}", method = RequestMethod.POST)
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @ApiOperation(value = "[系统管理]模块查询", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysModulesId", value = "唯一标识", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "parentId", value = "模块上级", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "模块名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "desc", value = "模块说明", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "url", value = "模块地址", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "level", value = "级别", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "enable", value = "是否可用", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "order", value = "优先级", paramType = "query", dataType = "int"),
    })
    @Models({SysModulesEntity.class})
    @Mapping(path = "/modules", method = RequestMethod.GET)
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @ApiOperation(value = "根据主键查询[系统管理]模块", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({SysModulesEntity.class})
    @Mapping(path = "/modules/{id}", method = RequestMethod.GET)
    public RETURN customQueryById() {
        return super.queryById();
    }

    @ApiOperation(value = "查询菜单树", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Mapping(path = "/modules/tree", method = RequestMethod.GET)
    public Object menus() {
        String sql = "SELECT\n" +
                "sys_modules.SYS_MODULES_ID,\n" +
                "sys_modules.PARENT_ID,\n" +
                "sys_modules.`NAME`,\n" +
                "sys_modules.`DESC`,\n" +
                "sys_modules.URL,\n" +
                "sys_modules.`ENABLE`,\n" +
                "sys_modules.`ORDER`\n" +
                "FROM\n" +
                "sys_modules\n" +
                "INNER JOIN sys_bt_roles_moudles ON sys_bt_roles_moudles.MODULE_ID = sys_modules.SYS_MODULES_ID\n" +
                "INNER JOIN sys_roles ON sys_roles.SYS_ROLES_ID = sys_bt_roles_moudles.ROLE_ID\n" +
                "INNER JOIN sys_bt_users_roles ON sys_roles.SYS_ROLES_ID = sys_bt_users_roles.ROLE_ID\n" +
                "WHERE sys_bt_users_roles.USER_ID = ?";
        List<SysModulesEntity> list = dao.findAll(sql, SysModulesEntity.class, getUser().getSysUsersId());
        setOutParam("routers", SysModulesEntity.createTree(list));

        final int weight = 15;
        setOutParam("permission", new HashMap<String, Integer>(Constant.NumberAbout.ELEVEN) {{
            put("idss-insight-linkage", weight);
            put("idss-insight-data-import", weight);
            put("idss-insight-task-config", weight);
            put("manage-base-user", weight);
            put("manage-base-dept", weight);
            put("idss-insight-component-group", weight);
            put("manage-base-org", weight);
            put("idss-insight-dashboard", weight);
            put("idss-insight-component-config-edit", weight);
            put("manage-base-role", weight);
            put("idss-insight-component-config-add", weight);
        }});
        return RETURN.SUCCESS;
    }

}
