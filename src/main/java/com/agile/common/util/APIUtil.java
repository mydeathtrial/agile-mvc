package com.agile.common.util;

import com.agile.common.annotation.Remark;
import com.agile.common.base.RETURN;
import com.agile.common.base.swagger.*;
import com.agile.common.container.MappingHandlerMapping;
import com.agile.common.mvc.service.BusinessService;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by 佟盟 on 2018/8/23
 */
public class APIUtil {
    private static Map<String, Object> serviceCache = new HashMap<>();
    private static Map<String, Defination> definitions = new HashMap<>();
    private static MappingHandlerMapping mappingHandlerMapping;

    public static HandlerMethod getApiCache(HttpServletRequest request) {
        if(mappingHandlerMapping == null)return null;
        try {
            HandlerExecutionChain handlerExecutionChain = getMappingHandlerMapping().getHandler(request);
            if(handlerExecutionChain!=null){
                return ((HandlerMethod) (handlerExecutionChain.getHandler()));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static void addMappingInfoCache(Object bean,Method method,Class clazz) {
        RequestMappingInfo requestMappingInfo = APIUtil.getMappingHandlerMapping().getMappingForMethod(method, clazz);
        if(requestMappingInfo!=null){
            getMappingHandlerMapping().registerHandlerMethod(bean,method,requestMappingInfo);
        }
    }

    public static MappingHandlerMapping getMappingHandlerMapping() {
        if(mappingHandlerMapping == null){
            mappingHandlerMapping = new MappingHandlerMapping();
            mappingHandlerMapping.afterPropertiesSet();
        }
        return mappingHandlerMapping;
    }

    public static Object getServiceCache(String key){
        return serviceCache.get(key);
    }
    public static void addServiceCache(String key, Object o){
        serviceCache.put(key, o);
    }

    public static SwaggerInfo getApi(){
        SwaggerInfo swaggerInfo = new SwaggerInfo();
        swaggerInfo.setInfo(getInfo());
        swaggerInfo.setTags(getTags());
        swaggerInfo.setPaths(getPaths());
//        swaggerInfo.setSecurityDefinitions(getSecurityDefinitions());
        swaggerInfo.setDefinitions(definitions);
        return swaggerInfo;
    }

    private static SwaggerSecutity getSecurityDefinitions() {
        HashMap<String, String> scopes = new HashMap<>();
        scopes.put("write:pets","modify pets in your account");
        scopes.put("read:pets","read:pets");
        return new SwaggerSecutity(new PetstoreAuth("oauth2","","implicit",scopes),new ApiKey());
    }

    private static Map<String, HashMap<String,Path>> getPaths() {
        Map<String, HashMap<String,Path>> map = new HashMap<>();
        Map<String, Object> annotationAttributes = FactoryUtil.getApplicationContext().getBeansWithAnnotation(Service.class);
        for (Map.Entry<String,Object> entity:annotationAttributes.entrySet()) {
            Class<?> serviceClass = AopUtils.getTargetClass(entity.getValue());
            Method[] methods = serviceClass.getDeclaredMethods();
            Class superClass = serviceClass.getSuperclass();
            String serviceName = serviceClass.getSimpleName();


            Map<String, Map<String,String>> responses = new HashMap<>();
            Field[] returns = RETURN.class.getDeclaredFields();
            for (int i = 0;i<returns.length;i++){
                try {
                    Field field = returns[i];
                    if(Modifier.isStatic(field.getModifiers())){
                        RETURN r = ((RETURN)field.get(null));
                        Map<String,String> description = new HashMap<>();
                        description.put("description",r.getMsg());
                        responses.put(r.getCode(),description);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            Map<String, Map<String, String>> saveR1 = createResponse("PARAMETER_ERROR","SUCCESS");
            Map<String, Map<String, String>> deleteR1 = createResponse("SUCCESS","PARAMETER_ERROR");
            Map<String, Map<String, String>> updateR1 = createResponse("PARAMETER_ERROR","HIBERNATE_EXPRESSION","SUCCESS");
            Map<String, Map<String, String>> queryR1 = createResponse("EXPRESSION","PARAMETER_ERROR","SUCCESS");

            List<Param> saveParamList = new ArrayList<>();
            List<Param> deleteParamList = new ArrayList<>();
            List<Param> updateParamList = new ArrayList<>();
            List<Param> queryParamList = new ArrayList<>();
            List<Param> paramListById = new ArrayList<>();
            List<Param> pageParamList = new ArrayList<>();
            Class entityClass;
            if(superClass == BusinessService.class){
                try {
                    entityClass = (Class)(((ParameterizedType) serviceClass.getGenericSuperclass()).getActualTypeArguments()[0]);
                    Map<String, Property> propertyMap = new HashMap<>();

                    Field[] fields = entityClass.getDeclaredFields();
                    for (Field field:fields) {
                        field.setAccessible(true);
                        String name = field.getName();
                        boolean required = false;
                        String description = null;
                        String type = ClassUtil.toSwaggerTypeFromName(field.getType().getSimpleName());
                        try {
                            Method getMethod = entityClass.getDeclaredMethod("get" + StringUtil.toUpperName(field.getName()));
                            Column column = getMethod.getAnnotation(Column.class);
                            if (column!=null){
                                required = !column.nullable();
                            }
                        } catch (NoSuchMethodException e) {
                            continue;
                        }
                        Remark remark = field.getAnnotation(Remark.class);
                        if(remark!=null){
                            description = remark.value();
                        }
                        saveParamList.add(new Param(name,"formData",description,required,type));

                        updateParamList.add(new Param(name,"query",description,required,type));

                        deleteParamList.add(new Param(name,"query",description,false,type));

                        queryParamList.add(new Param(name,"query",description,false,type));

                        pageParamList.add(new Param(name,"query",description,false,type));

                        paramListById.add(new Param(name,"query",description,false,type));

                        propertyMap.put(name,new Property(type,description));
                    }

                    Defination definition = new Defination();
                    definition.setProperties(propertyMap);
                    definitions.put(entityClass.getSimpleName(),definition);

                    HashMap<String, String> item = new HashMap<>();
                    item.put("type","string");
                    deleteParamList.add(new ArrayParam("id","query","主键数组",false,"array",item));
                    queryParamList.add(new ArrayParam("id","query","主键数组",false,"array",item));
                    updateParamList.add(new Param("id","path","主键",true,"string"));
                    paramListById.add(new Param("id","path","主键",true,"string"));
                    pageParamList.add(new Param("page","path","第几页",true,"integer"));
                    pageParamList.add(new Param("size","path","每页条数",true,"integer"));

                    String name = entityClass.getSimpleName();
                    String entityPrefix = StringUtil.toUpperName(PropertiesUtil.getProperty("agile.generator.entity_prefix"));
                    String entitySuffix = StringUtil.toUpperName(PropertiesUtil.getProperty("agile.generator.entity_suffix"));
                    if(name.startsWith(entityPrefix)){
                        name = name.replaceFirst(entityPrefix,"");
                    }
                    if(name.endsWith(entitySuffix)){
                        name = name.replaceFirst(entitySuffix,"");
                    }
                    name = StringUtil.toUpperName(PropertiesUtil.getProperty("agile.generator.service_prefix")) + StringUtil.toUpperName(name) + StringUtil.toUpperName(PropertiesUtil.getProperty("agile.generator.service_suffix"));
                    map.put("/"+name,createPath(
                            new Get(entity,queryParamList,queryR1,"模糊查询"+Path.setDescription(entity.getValue().getClass())+"数据列表"),
                            new Post(entity,saveParamList,saveR1,"新增一条"+Path.setDescription(entity.getValue().getClass())+"数据"),
                            new Delete(entity,deleteParamList,deleteR1,"批量删除"+Path.setDescription(entity.getValue().getClass())+"数据")
                    ));
                    map.put("/"+name+"/{id}",createPath(
                            new Delete(entity,paramListById,deleteR1,"删除指定id"+Path.setDescription(entity.getValue().getClass())+"数据"),
                            new Put(entity,updateParamList,updateR1,"更新指定id"+Path.setDescription(entity.getValue().getClass())+"数据"),
                            new Get(entity,paramListById,queryR1,"查询指定id"+Path.setDescription(entity.getValue().getClass())+"数据")
                    ));
                    map.put("/"+name+"/page/{page}/{size}",createPath(
                            new Get(entity,pageParamList,queryR1,"查询"+Path.setDescription(entity.getValue().getClass())+"第page页size条数据")
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0 ; i<methods.length;i++){
                Method method = methods[i];
                String methodName = method.getName();
                map.put("/api/"+serviceName+"/"+methodName,createPath(
                        new Get(entity,Arrays.asList(new Param[]{new Param("test","query","测试参数",false,"string")}),responses,null),
                        new Post(entity,null,null,null)));
            }

        }
        Map<String, HashMap<String,Path>> result = new LinkedHashMap<>();
        map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));;
        return result;
    }

    private static Map<String, Map<String,String>> createResponse(String... rName) {
        Map<String, Map<String,String>> responses = new HashMap<>();
        try {
            for (String name:rName){
                Field saveReturn = RETURN.class.getDeclaredField(name);
                Map<String,String> description = new HashMap<>();
                saveReturn.setAccessible(true);
                RETURN r = (RETURN)(saveReturn.get(null));
                description.put("description",r.getMsg());
                responses.put(r.getCode(),description);
            }
        }catch (Exception e){
            return null;
        }
        return responses;
    }

    private static HashMap<String,Path> createPath(Path... paths){
        HashMap<String,Path> map = new HashMap<>();
        for (int i = 0; i<paths.length; i++){
            Path path = paths[i];
            path.setOperationId(RandomStringUtil.getRandom(8, RandomStringUtil.Random.NUMBER));
            map.put(path.getClass().getSimpleName().toLowerCase(),path);
        }

        return map;
    }

    private static List<Tag> getTags() {
        List<Tag> list = new ArrayList<>();
        Map<String, Object> annotationAttributes = FactoryUtil.getApplicationContext().getBeansWithAnnotation(Service.class);
        for (Map.Entry<String,Object> entity:annotationAttributes.entrySet()) {
            Tag tag = new Tag();
            tag.setName(entity.getKey());
            tag.setDescription(Path.setDescription(entity.getValue().getClass()));
            list.add(tag);
        }
        return list;
    }

    private static Info getInfo() {
        Info info = new Info();
        info.setTitle(PropertiesUtil.getProperty("agile.title"));
        info.setVersion(PropertiesUtil.getProperty("agile.version"));
//        info.setContact();
        return info;
    }
}
