package com.agile.mvc.service;

import com.agile.common.annotation.Mapping;
import com.agile.common.annotation.Models;
import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.base.Constant;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.mvc.service.BusinessService;
import com.agile.common.util.ArgusStringUtil;
import com.agile.common.util.DateUtil;
import com.agile.common.util.IdUtil;
import com.agile.common.util.PasswordUtil;
import com.agile.common.util.StringUtil;
import com.agile.common.validate.ValidateType;
import com.agile.mvc.entity.SysBtUsersRolesEntity;
import com.agile.mvc.entity.SysUsersEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.domain.Page;
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
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysUsersEntity"),
            @ApiImplicitParam(name = "sysRole", value = "角色ids", paramType = "body", dataType = "list")
    })
    @Models({SysUsersEntity.class})
    @Validates({
            @Validate(beanClass = SysUsersEntity.class, validateGroups = Insert.class),
            @Validate(value = "email", validateType = ValidateType.EMAIL, validateMsg = "请输入正确的邮箱格式")
    })
    @Mapping(value = "/sys-users", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        SysUsersEntity usersEntity = getInParam(SysUsersEntity.class);
        List<SysUsersEntity> list = dao.findAll(SysUsersEntity.builder().saltKey(usersEntity.getSaltKey()).build());
        if (list.size() > 0) {
            return RETURN.getMessage("agile.exception.RepeatUser");
        }
        PasswordUtil.LEVEL level = PasswordUtil.getPasswordLevel(usersEntity.getSaltValue());
        if (level == PasswordUtil.LEVEL.SO_EASY) {
            return RETURN.getMessage("agile.exception.LowPasswordStrength");
        }
        usersEntity.setSaltValue(PasswordUtil.encryption(usersEntity.getSaltValue()));
        usersEntity.setSysUsersId(Long.toString(IdUtil.generatorId()));
        usersEntity = dao.saveAndReturn(usersEntity);
        saveUsersRoles(getInParamOfArray("sysRole"), usersEntity);
        return RETURN.SUCCESS;
    }

    @ApiOperation(value = "批量删除[系统管理]用户", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "数组格式", paramType = "query", dataType = "Integer", allowMultiple = true)
    })
    @Validates({
            @Validate(value = "ids", nullable = false, validateMsgKey = "唯一标识不能为空")
    })
    @Mapping(path = "/sys-users/delete", method = RequestMethod.POST)
    public RETURN customDelete() throws NoSuchIDException {
        List<String> ids = getInParamOfArray("ids", String.class);
        dao.deleteInBatch(SysUsersEntity.class, ids.toArray());
        return RETURN.SUCCESS;
    }

    @ApiOperation(value = "更新[系统管理]用户", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysRole", value = "角色", paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysUsersEntity")
    })
    @Models({SysUsersEntity.class})
    @Validate(beanClass = SysUsersEntity.class, validateGroups = Update.class)
    @Mapping(value = "/sys-users", method = RequestMethod.PUT)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        SysUsersEntity usersEntity = getInParam(SysUsersEntity.class);
        dao.updateBySQL("delete from sys_bt_users_roles where user_id = ?", usersEntity.getSysUsersId());
        saveUsersRoles(getInParamOfArray("sysRole"), usersEntity);
        return super.update();
    }

    private void saveUsersRoles(List<String> roles, SysUsersEntity usersEntity) {
        if (null != roles && roles.size() > 0) {
            for (int i = 0; i < roles.size(); i++) {
                SysBtUsersRolesEntity sysBtUsersRolesEntity = new SysBtUsersRolesEntity();
                sysBtUsersRolesEntity.setRoleId(roles.get(i));
                sysBtUsersRolesEntity.setUserId(usersEntity.getSysUsersId());
                sysBtUsersRolesEntity.setSysBtUsersRolesId(Long.toString(IdUtil.generatorId()));
                dao.save(sysBtUsersRolesEntity);
            }
        }
    }

    @ApiOperation(value = "分页查询[系统管理]用户", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysRole", value = "角色", paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "saltKey", value = "账号", paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "sysDepart", value = "部门", paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "用户姓名", paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "userNumber", value = "用户工号", paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "isLocked", value = "是否锁", paramType = "body", dataType = "boolean"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "query", dataType = "String", allowMultiple = true)
    })
    @Models({SysUsersEntity.class})
    @Mapping(path = "/sys-users/list/query", method = RequestMethod.POST)
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        String sort = ArgusStringUtil.getSortString(getInParamOfArray("sorts"));
        if (null == sort) {
            sort = "";
        } else {
            if (sort.toLowerCase().indexOf("sys_role") != -1) {
                sort = sort.toLowerCase().replace("sys_role", "sysRole");
            }
            sort = " order by " + sort;
        }
        String sql = "SELECT b.sys_users_id AS sysUsersId," +
                "b.area_id AS areaId," +
                "b.create_time AS createTime," +
                "b.email AS email," +
                "b.enabled AS enabled," +
                "b.expired_time AS expiredTime," +
                "b.is_locked AS isLocked," +
                "b.leader AS leader," +
                "b. NAME AS name," +
                "b.on_line_strategy AS onLineStrategy," +
                "b.salt_key AS saltKey," +
                "b.salt_value AS saltValue," +
                "b.salt_value_old AS saltValueOld," +
                "b.sex AS sex," +
                "b.sys_depart_id AS sysDepartId," +
                "b.telephone AS telephone," +
                "b.update_time AS updateTime," +
                "b.user_number AS userNumber, GROUP_CONCAT(a.role_name) AS sysRole,b.sys_depart as sysDepart" +
                " FROM " +
                "( SELECT sys_users.*, sys_department.depart_name AS sys_depart FROM sys_users LEFT JOIN sys_department ON sys_department.sys_depart_id = sys_users.sys_depart_id " +
                "WHERE sys_users.`name` LIKE {name} AND sys_users.salt_key LIKE {saltKey} AND sys_users.user_number LIKE {userNumber} AND sys_users.is_locked = {isLocked} ) AS b " +
                "LEFT JOIN " +
                "( SELECT sys_roles.ROLE_NAME , sys_roles.SYS_ROLES_ID, sys_bt_users_roles.USER_ID FROM sys_roles, sys_bt_users_roles WHERE sys_bt_users_roles.ROLE_ID = sys_roles.SYS_ROLES_ID ) AS a" +
                " ON a.USER_ID = b.sys_users_id WHERE a.role_name LIKE {sysRole} AND b.sys_depart LIKE {sysDepart} GROUP BY b.sys_users_id" + sort;
        Map<String, Object> param = new HashMap<>();
        String name = getInParam("name", String.class);
        String saltKey = getInParam("saltKey", String.class);
        String userNumber = getInParam("userNumber", String.class);
        String sysRole = getInParam("sysRole", String.class);
        String sysDepart = getInParam("sysDepart", String.class);
        String isLocked = getInParam("isLocked", String.class);
        if (StringUtil.isNotEmpty(isLocked)) {
            param.put("isLocked", "true".equals(isLocked) ? 1 : 0);
        }
        param.put("name", StringUtil.isEmpty(name) ? null : "%" + name + "%");
        param.put("saltKey", StringUtil.isEmpty(saltKey) ? null : "%" + saltKey + "%");
        param.put("userNumber", StringUtil.isEmpty(userNumber) ? null : "%" + userNumber + "%");
        param.put("sysRole", StringUtil.isEmpty(sysRole) ? null : "%" + sysRole + "%");
        param.put("sysDepart", StringUtil.isEmpty(sysDepart) ? null : "%" + sysDepart + "%");
        Page page = dao.findPageBySQL(sql, getInParam("pageNum", Integer.class), getInParam("pageSize", Integer.class), param);
        setOutParam(Constant.ResponseAbout.RESULT, page);
        return RETURN.SUCCESS;
    }

    @ApiOperation(value = "查询[系统管理]用户", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "SysUsersEntity")
    })
    @Models({SysUsersEntity.class})
    @Mapping(path = "/sys-users/query", method = RequestMethod.POST)
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
        String sql = "SELECT a.*, de.depart_name AS sysDepart FROM " +
                "( SELECT b.sys_users_id AS sysUsersId, b.area_id AS areaId, b.create_time AS createTime, b.email AS email, " +
                "b.enabled AS enabled, b.expired_time AS expiredTime, b.is_locked AS isLocked, b.leader AS leader, b. NAME AS name, " +
                "b.on_line_strategy AS onLineStrategy, b.salt_key AS saltKey, b.salt_value AS saltValue, b.salt_value_old AS saltValueOld, " +
                "b.sex AS sex, b.sys_depart_id AS sysDepartId, b.telephone AS telephone, b.update_time AS updateTime, b.user_number AS userNumber, " +
                "t.* FROM sys_users AS b, " +
                "( SELECT GROUP_CONCAT(ROLE_NAME) AS sysRole FROM sys_roles WHERE SYS_ROLES_ID IN ( SELECT ROLE_ID FROM sys_bt_users_roles WHERE USER_ID = ? )) AS t " +
                "WHERE b.sys_users_id = ? ) AS a LEFT JOIN sys_department AS de ON a.sysDepartId = de.sys_depart_id";
        List<Map<String, Object>> list = dao.findAllBySQL(sql, getInParam("id"), getInParam("id"));
        if (null != list && list.size() > 0) {
            Map<String, Object> map = list.get(0);
            map.put("createTime", DateUtil.getTimeStamp((Date) map.get("createTime")));
            map.put("expiredTime", DateUtil.getTimeStamp((Date) map.get("expiredTime")));
            setOutParam(Constant.ResponseAbout.RESULT, list.get(0));
        }
        return RETURN.SUCCESS;
    }

    @ApiOperation(value = "更新[系统管理]用户密码", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "saltValue", value = "新密码", required = true, paramType = "query", dataType = "String")
    })
    @Validates({
            @Validate(value = "id", nullable = false, validateMsgKey = "唯一标识不能为空"),
            @Validate(value = "saltValue", nullable = false, validateMsgKey = "新密码不能为空")
    })
    @Mapping(path = "/sys-users/update-password", method = RequestMethod.POST)
    public RETURN customUpdatePassword() throws NoSuchIDException, IllegalAccessException {
        SysUsersEntity sysUsersEntity = new SysUsersEntity();
        sysUsersEntity.setSaltValue(PasswordUtil.encryption(getInParam("saltValue", String.class)));
        sysUsersEntity.setSysUsersId(getInParam("id", String.class));
        dao.updateOfNotNull(sysUsersEntity);
        return RETURN.SUCCESS;
    }
}
