package ${servicePackageName};

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
import ${entityPackageName}.${entityName};

/**
 * @author agile generator
 */
@Api(description = "<#if (remarks?? && remarks!="")>${remarks}<#else>${serviceName}</#if>")
@Mapping("/api/${moduleName}")
@Service
public class ${serviceName} extends BusinessService<${entityName}> {
    @ApiOperation(value = "<#if (remarks?? && remarks!="")>新增${remarks}<#else>新增</#if>", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "${entityName}")
    })
    @Models({${entityName}.class})
    @Validate(beanClass = ${entityName}.class, validateGroups = Insert.class)
    @Mapping(value = "/${entityCenterLineName}", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @ApiOperation(value = "<#if (remarks?? && remarks!="")>删除${remarks}<#else>删除</#if>", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Validate(beanClass = ${entityName}.class, validateGroups = Delete.class)
    @Mapping(path = "/${entityCenterLineName}/{id}", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @ApiOperation(value = "<#if (remarks?? && remarks!="")>更新${remarks}<#else>更新</#if>", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "${entityName}")
    })
    @Models({${entityName}.class})
    @Validate(beanClass = ${entityName}.class, validateGroups = Update.class)
    @Mapping(value = "/${entityCenterLineName}", method = RequestMethod.PUT)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @ApiOperation(value = "<#if (remarks?? && remarks!="")>分页查询${remarks}<#else>分页查询</#if>", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "${entityName}"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "query", dataType = "String[]")
    })
    @Models({${entityName}.class})
    @Mapping(path = "/${entityCenterLineName}/list/query", method = RequestMethod.POST)
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @ApiOperation(value = "<#if (remarks?? && remarks!="")>查询${remarks}<#else>查询</#if>", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "${entityName}")
    })
    @Models({${entityName}.class})
    @Mapping(path = "/${entityCenterLineName}/query", method = RequestMethod.POST)
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @ApiOperation(value = "<#if (remarks?? && remarks!="")>根据主键查询${remarks}<#else>根据主键查询</#if>", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({${entityName}.class})
    @Mapping(path = "/${entityCenterLineName}/{id}", method = RequestMethod.GET)
    public RETURN customQueryById() {
        return super.queryById();
    }
}
