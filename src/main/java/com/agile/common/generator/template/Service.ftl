package ${servicePackageName};

import com.agile.common.mvc.service.BusinessService;
import org.springframework.stereotype.Service;
import io.swagger.annotations.Api;
import ${entityPackageName}.${entityName};

/**
 * @author agile generator
 */
@Api(value = "<#if (remarks?? && remarks!="")>${remarks}<#else>${serviceName}</#if>")
@Service
public class ${serviceName} extends BusinessService<${entityName}> {

}
