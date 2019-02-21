package ${servicePackageName};

import com.agile.common.mvc.service.BusinessService;
import org.springframework.stereotype.Service;
import io.swagger.annotations.Api;
import org.hibernate.sql.Insert;
import ${entityPackageName}.${entityName};

/**
 * @author agile generator
 */
@Api(value = "<#if (remarks?? && remarks!="")>${remarks}<#else>${serviceName}</#if>")
@Mapping("/api/${serviceName}")
@Service
public class ${serviceName} extends BusinessService<${entityName}> {
  @ApiOperation(value = "<#if (remarks?? && remarks!="")>新增${remarks}<#else>新增</#if>", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "${entityName}")
  })
  @Models({SysUsersEntity.class})
  @Validate(beanClass = ${entityName}.class, validateGroups = Insert.class)
  @Mapping(value = "/save", method = RequestMethod.POST)
  public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
      return super.save();
  }

  @ApiOperation(value = "<#if (remarks?? && remarks!="")>删除${remarks}<#else>删除</#if>", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
  })
  @Validate(beanClass = ${entityName}.class, validateGroups = Delete.class)
  @Mapping(path = "/{id}/delete", method = RequestMethod.DELETE)
  public RETURN customDelete() throws NoSuchIDException {
      return super.delete();
  }

  @ApiOperation(value = "<#if (remarks?? && remarks!="")>更新${remarks}<#else>更新</#if>", httpMethod = "UPDATE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "${entityName}")
  })
  @Models({${entityName}.class})
  @Validate(beanClass = ${entityName}.class, validateGroups = Update.class)
  @Mapping(value = "/{id}/update", method = RequestMethod.UPDATE)
  public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
      return super.update();
  }

  @ApiOperation(value = "<#if (remarks?? && remarks!="")>更新${remarks}<#else>更新</#if>", httpMethod = "UPDATE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "${entityName}")
  })
  @Models({${entityName}.class})
  @Validate(beanClass = ${entityName}.class, validateGroups = Update.class)
  @Mapping(value = "/{id}/update", method = RequestMethod.UPDATE)
  public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
      return super.update();
  }
}
