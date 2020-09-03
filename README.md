
<img src="https://images.gitee.com/uploads/images/2019/0912/201757_6d887de5_451899.jpeg" width="20%" height="20%" />

# agile-mvc
[![](https://img.shields.io/badge/Spring--mvc-LATEST-green)](https://img.shields.io/badge/Spring--mvc-LATEST-green)
[![CircleCI](https://circleci.com/gh/mydeathtrial/agile/tree/master.svg?style=svg)](https://circleci.com/gh/mydeathtrial/agile/tree/master)
[![Maven Central](https://img.shields.io/badge/maven-build-green)](https://img.shields.io/badge/maven-build-green)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

-------
Agile-Framework 系列脚手架致力于帮助开发人员从繁重的代码当中解脱出来，致力于”用最简单的代码勾画出最美的艺术“这一不变的理念。Spring-Agile提供Web开发一站式解决方案。此项目包含Web开发相关的各种必要组件及辅助工具集，方便开发者通过 Agile 编程模型轻松使用这些组件来开发单体应用及分布式应用。
agile-mvc作为agileframework系列框架最初的发展根基，不断开枝散叶，目前已成功衍生出十几个独立、特色化功能开发组件，`cloud.agileframework`值得您的期待。

参考文档 请查看 [WIKI](https://gitee.com/mydeathtrial/agile/wikis)正在编写中 。

## 主要功能
* **几乎为零的代码入侵**
agile-mvc组件几乎做到了零规范要求，甚至无感知的开发效果，除声明@AgileService注解（Agile服务层托管）、@Mapping注解（地址映射，也可省略）几乎不需要在我们的代码中出现特殊的Agile代码痕迹，保证了用最原生的java方法，去实现传统mvc三层的所有能力。

* **抽象化控制层**
从传统mvc三层模式中，agile-mvc最突出的特点是提供了抽象控制器`MainController`，解决了参数判断、请求跳转、视图组装三部分的功能。省略了大部分控制层代码的冗余，达到一个原生JAVA方法，实现一个API服务的目的。同时由于
0代码入侵的设计理念，也保留了复杂业务逻辑场景下对自定义控制器及服务层复用的支持。所以该功能大大降低了Web项目开发的代码量，以及让其学习成本降至最低，基本达到了
会写java方法，就会编写规范化API的效果。

* **自动化API地址映射**
在spring-mvc中往往需要大量应用诸如RequestMapping、GetMapping之类的地址映射注释，agile-mvc则提供了对@AgileService服务默认的（类名+方法名+模糊化）地址映射规则，所以在非Restful风格api设计的项目当中，甚至可以省略了所有地址映射声明。同时agile-mvc提供了
@Mapping注解，通过Agile内置的AgileMappingHandlerMapping实现在传统Service层中实现@RequestMapping的效果。使用方式无任何差别，确保开发人员零学习成本。

* **强大的参数解析器**
参数解析器由agile系列套件common-util提供参数解析，为spring-mvc扩展了超乎想象的参数解析能力，注入逗号分隔字符串转集合、下划线属性json转pojo对象、多层对象嵌套、不同类型集合数据参数识别、字符串时间语句分析、文件上传、文件输入流等等能力。
将开发人员对参数的复杂类型转换直接省略掉，进一步缩减非业务代码量。

* **可定制化统一响应模板**
agile-mvc提供可自定义响应模板入口，在不同项目中可以定义个性化的统一响应格式，响应头、体信息均可定制。

* **集成agile-validate参数验证控件**：
借助agile系列套件agile-validate方法参数验证组件，实现对springmvc原生控制层方法、@AgileService服务层方法的参数验证注解解析，弥补原生springmvc在参数验证注解方面能力的不足，其中包括了自定义逻辑的业务参数验证的支持。

* **自动化配置文件掃描**
借助common-util的PropertiesUtil工具实现对环境内的任意配置文件扫描，避免了硬编码方式指定配置文件，并在功能初始化阶段以由低到高的优先级对扫描到的配置文件进行打印。该功能解开了在多模块开发过程中配置文件规范的限制，确保模块的扩展过程可携带默认配置。
且配置扫描原则为外层>内层 同层按照命名顺序排列优先级。

* **统一异常处理**
对全局任何异常均做了统一拦截、格式化响应、日志打印

* **异常、国际化响应报文关联**
所有异常均支持国际化配置文件关联，当拦截到异常时，优先获取国际化配置，组装响应报文，以此种方式省略了开发人员对异常情况的处理。可以在程序任何位置直接将业务错误提示以java throw的方式直接返回给前端。

* **全局异步请求**
AgileService代理服务全部使用异步请求处理，开发人员只需编写原生JAVA方法实现异步请求，过期时间灵活可配

* **CORS跨域**
内置Cors过滤器，解决前端跨域访问问题。

-------
![龙江银行](https://images.gitee.com/uploads/images/2019/0912/192435_6f8ca194_451899.gif "logo.gif")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/193812_fcfe31ca_451899.jpeg "timg 拷贝.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/193722_c45d1a93_451899.jpeg "timg2.jpeg")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/193918_e3c779ca_451899.jpeg "jtw.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/194129_179b6342_451899.png "logo1.png")

-------
## 快速入门
开始你的第一个项目是非常容易的。

#### 步骤 1: 下载包
您可以从[最新稳定版本]下载包(https://github.com/mydeathtrial/agile-mvc/releases).
该包已上传至maven中央仓库，可在pom中直接声明引用

以版本agile-mvc-0.1.jar为例。
#### 步骤 2: 添加maven依赖
```xml
<!--声明中央仓库-->
<repositories>
    <repository>
        <id>cent</id>
        <url>https://repo1.maven.org/maven2/</url>
    </repository>
</repositories>

<!--声明组件依赖-->
<dependency>
    <groupId>cloud.agileframework</groupId>
    <artifactId>agile-mvc</artifactId>
    <version>0.1</version>
</dependency>
```
#### 步骤 3: 开箱即用
声明agile-mvc依赖后，组件通过spring-boot-starter方式自动加载生效，无需额外配置。组件额外配置，启动后控制台会出现Agile启动标识：
```

_____/\\\\\\\\\________/\\\\\\\\\\\\__/\\\\\\\\\\\__/\\\______________/\\\\\\\\\\\\\\\_ 
 ___/\\\///////\\\____/\\\//////////__\/////\\\///__\/\\\_____________\/\\\///////////__
  __/\\\_______\/\\\__/\\\_________________\/\\\_____\/\\\_____________\/\\\_____________
   _\/\\\_______\/\\\_\/\\\____/\\\\\\\_____\/\\\_____\/\\\_____________\/\\\\\\\\\\\_____
    _\/\\\\\\\\\\\\\\\_\/\\\___\/////\\\_____\/\\\_____\/\\\_____________\/\\\///////______
     _\/\\\/////////\\\_\/\\\_______\/\\\_____\/\\\_____\/\\\_____________\/\\\_____________
      _\/\\\_______\/\\\_\/\\\_______\/\\\_____\/\\\_____\/\\\_____________\/\\\_____________
       _\/\\\_______\/\\\_\//\\\\\\\\\\\\/___/\\\\\\\\\\\_\/\\\\\\\\\\\\\\\_\/\\\\\\\\\\\\\\\_
        _\///________\///___\////////////____\///////////__\///////////////__\///////////////__

 :: 敏捷开发框架 Agile Framework ::  (version:1.0)
```
#### 步骤 4: 声明AgileService代理服务
`AgileService代理服务`是对`传统Service层`的功能增强，为传统Service层扩展了直接通过抽象控制器暴漏服务的能力，提高参数验证
控制跳转、组装视图部分的复用程度。并保留传统Service层的能力。以改造前后对比的方式举例：
```java
//传统Service层
@Service
public class MyService {
    public Object myBusinessMethod(String param){
        //业务编码及持久层能力调用...
    }
}

//AgileService代理服务，该注解声明类，仍会被在ComponentScan扫描范围内加载到spring容器中
@AgileService
public class MyService {
    //该方法默认生成API http://host:port/my-service/my-business-method
    //该映射地址可以通过@Mapping注解覆盖定义，其用法步骤5中讲解
    //该方法入参将由抽象控制器直接提供请求参数解析装配，并提供@Validation注解校验
    //该方法默认提供spring事务包裹
    //该方法return返回数据，将自动被抽象控制器包装、格式化成统一响应视图，直接返回前端
    public Object myBusinessMethod(String param){
        //业务编码及持久层能力调用...
    }
}
```
效果对比
```
传统Service层:仅提供控制层调用能力

AgileService代理服务:1、提供控制层调用能力；2、提供客户端API调用能力；3、参数验证能力；4、默认事务；5、自动包装、格式化成统一响应视图；
```

#### 步骤 5: AgileService代理服务API地址映射
组件为`AgileService代理服务`内`public方法`提供了默认的`API地址映射`方案，也可以通过其他注解进行覆盖、禁用、更改，如
```java
//AgileService代理服务
@AgileService
public class MyService {
    //禁止暴露，该注解也可直接应用于类注解，应用于类注解时，组件将不对声明类内部任何public方法提供能力扩展
    @NotAPI
    public Object myBusinessMethod(String param){
        //业务编码及持久层能力调用...
    }
    
    //限制访问方法，默认不限制请求访问方法，可以通过该注解限定访问方法种类
    @ApiMethod({RequestMethod.POST,RequestMethod.GET})
    public Object myBusinessMethod(String param){
        //业务编码及持久层能力调用...
    }

    //覆盖默认地址映射，当声明@Mapping注解后，组件将不提供给该方法默认地址映射方案
    //该注解使用方法同@RequestMapping完全一致，支持声明地址传参、多地址映射、访问方法等等内容
    @Mapping(path = "/test2/{id}")
    public Object myBusinessMethod(String id){
        //业务编码及持久层能力调用...
    }
}
```
警告：默认情况下组件会拦截除静态资源以外的所有请求路径，所以需要静态资源地址头应该与正常的API地址区分开，默认使用`/static/**`作为静态资源访问头。
更改配置方式如下：
```yaml
spring:
  mvc:
    static-path-pattern: /static/**
```
#### 步骤 6: 参数解析
组件为`控制层`与`AgileService代理服务`提供统一的参数解析能力，该参数解析方案弥补了spring-mvc中对复杂参数方面解析能力的不足
参数解析分`声明式`、`直调式`两种，`声明式`优点是无代码入侵，`直调式`优点则是更加灵活。
+ `声明式`
入参
```
//body入参为例
{
    "id":111,
    "param":"第二个参数",
    "pojo":{
        "attr1":1,
        "attr2":2
    },
    "collect":[
        {
            "attr1":1,
            "attr2":2
        },
        {
            "attr1":1,
            "attr2":2
        }
    ]
}
```
声明解析
```java
//AgileService代理服务
@AgileService
public class MyService {
    //参数解析过程将依据方法参数名，如下方法的id、param、pojo为key值，于入参集合中提取后做类型转换并填充
    //其中多层嵌套参数提取，需要通过参数命名中使用下划线区分参数层级，如pojo下的attr1参数，需要使用pojo_attr1命名
    //拍平取参，支持将任意层级参数进行集合拍平处理，如collect_attr1参数
    @Mapping(path = "/test2/{id:[\\d]+}")
    public Object myBusinessMethod(int id,
                            String param,
                            YourPojo pojo,
                            int pojo_attr1,
                            List<Integer> collect_attr1){
        //业务编码及持久层能力调用...
    }
}
```
直调解析(可于请求线程内任意位置调用AgileParam)
```java
public class MyService {
   
    public Object myBusinessMethod(){
        Integer id = AgileParam.getInParam("id",Integer.class);
        String param = AgileParam.getInParam("param",String.class);
        YourPojo pojo = AgileParam.getInParam("param",YourPojo.class);
        Integer attr1 = AgileParam.getInParam("pojo.attr1",Integer.class);
        List<Integer> attr1s = AgileParam.getInParam("collect.attr1",new TypeReference<List<Integer>>(){});
    }
}
```
文件上传参数
```java
public class MyService {
    //file为前端传递的文件key值
    public Object myBusinessMethod(MultipartFile file){
    }

    //直接以流方式声明
    public Object myBusinessMethod(InputStream file){
    }

    //多文件
    public Object myBusinessMethod(MultipartFile[] file){
    }

    //可以是MultipartFile的任意集合类型
    public Object myBusinessMethod(Set<MultipartFile> file){
    }
}
```
警告：
1. `AgileService代理服务`中，未增加对原生springmvc中的参数类注解如`@PathVariabale`、`@RequestHeader`、`@Validated`等等一类的注解解析。后续会根据
用户反馈考虑增加该部分能力。
2. AgileParam与请求线程绑定，无法跨线程访问，多线程场景需要先提取参数再自行使用
3. TypeReference由common-util提供，包为`cloud.agileframework.common.util.clazz`，使用时一般为匿名内部类方式，具体方式请参照`common-util`组件
4. @Mapping注解是由Agile提供，`cloud.agileframework.mvc.annotation.Mapping`

#### 步骤 7: 参数验证
该参数验证适用于`传统控制层`与`AgileService代理服务`，API方式访问时会调用参数验证注解，实现请求拦截。
注解支持声明业务代码方式参数验证，例：
```java
@Validate(value = "file", nullable = false)
@Validate(value = "id", customBusiness = {MyValidate.class})
@Mapping(path = "/test/{id}")
public Object test(int id, MultipartFile[] file) {
    //业务代码
}

//自定义业务验证
public static class MyValidate implements ValidateCustomBusiness {

    //此处params为id值，当验证失败后需要自行组装错误信息List<ValidateMsg>，当错误信息为空集合时，认为参数准确，不进行拦截
    @Override
    public List<ValidateMsg> validate(Object params) {
        return null;
    }
} 
```
响应报文
```
{
    "head": {
        "ip": "192.168.101.42",
        "code": "100002",
        "msg": "参数错误",
        "status": "OK"
    },
    "result": [
        {
            "message": "不允许为空值",
            "state": false,
            "item": "file",
            "itemValue": null
        }
    ]
}
```
#### 步骤 8: 统一响应视图
默认统一响应视图分为头（head）体（result）两部分组成，组件对全局异常以及超范围请求均做了拦截，
并且将最终的拦截结果与国际化配置结合组装成统一响应视图
```
{
    "head": {
        "ip": "172.29.128.1",
        "code": "100000",
        "msg": "操作失败",
        "status": "INTERNAL_SERVER_ERROR"
    },
    "result": null
}
```
定制化响应报文
1. 声明继承于`cloud.agileframework.mvc.base.AbstractResponseFormat`的模板类
2. 将其注入到spring容器当中
```
@Component
public class CommonResponseFormate extends AbstractResponseFormat {
    @Override
    public Map<String, Object> buildResponseData(Head head, Object result) {
        //head为组件整理后的响应头部信息
        //result为响应体信息
        //自行定制返回结果状态为Map格式返回，该Map将作为ModelAndView中的Model组装成视图写入response
        return null;
    }
}
```
为保证原有`控制层`的灵活性，以及控制层不存在相互调用的限制，所以统一视图处理器针对`控制层`与`AgileService代理服务`有所不同。`AgileService代理服务`中方法的
`return部分`将被包裹成响应报文中的`result`部分返回，头部信息则由是否捕获异常或是否手动设置决定。此处分`控制层`与`AgileService代理服务`
两种情况举例：
+ `控制层`
```java
@Controller
public class MyController{
    @RequestMapping("/test")
    public ModelAndView test(String a) {
        //业务代码...
        //设置响应头head
        AgileReturn.setHead(RETURN.SUCCESS);
        //设置响应体result
        AgileReturn.add("a",a);
        //构建返回视图
        return AgileReturn.build();
    }
}
```
+ `AgileService代理服务`
```java
@AgileService
public class MyService {
    //任何return结果都将组装成result部分
    //头部信息则会根据是否捕获Exception异常界定head内部信息，未捕获情况下均使用正常success头
    public Object myBusinessMethod(String param){
        //业务编码及持久层能力调用...
        return xx;
    }

    //手动设置响应头，返回类型设为RETURN，该方式存在代码入侵，尽在该方法不存在复用情况下使用为好
    public RETURN myBusinessMethod(String param){
        //业务代码...
        
        //构建返回视图
        return RETURN.SUCCESS;
    }

    //利用国际化构造响应头head，更多RETURN用法请参照wiki
    public RETURN myBusinessMethod(String param){
        //业务代码...
        
        //构建返回视图
        return RETURN.byMessage("国际化key","国际化参数1","国际化参数2...");
    }

    //手动设置响应体result
    public void myBusinessMethod(String param){
        //业务代码...
        //设置响应体result
        AgileReturn.add("a",param);
    }

    //通过异常声明响应头
    //统一一场拦截器捕捉到异常后，会根据异常类引用名去国际化配置文件中获取响应文与响应编码信息，可阅读步骤11统一异常处理
    public void myBusinessMethod(String param) throws YourException{
        //业务代码...
        //设置响应体result
        AgileReturn.add("a",param);
        throw new YourException();
    }
}
```
#### 步骤 9: 自动配置
借助agile系列套件common-util中PropertiesUtil的配置加载能力，实现在工程启动阶段自动扫描应用类所在包下以及编译路径下配置文件。
避免杂乱的配置文件位置声明。
+ 重点：配置文件加载优先级从编译路径开始计算，`层级越深，优先级越低，同层级则按照配置文件名顺序排列`。application配置文件保留最高优先级，优先级越高约被最后加载，覆盖低优先级配置
覆盖方式为内容覆盖，而不是文件覆盖，所以不同内容且文件名相同的配置文件，内容不会存在覆盖关系，仅对相同key值的配置项进行覆盖。项目在启动阶段会于控制台按照由低到高的优先级顺序打印加
载到的配置文件。例如
```
连接到目标VM, 地址: ''127.0.0.1:56967'，传输: '套接字'', 传输: '{1}'
/D:/workspace-agile/agile/target/classes/cloud/agileframework/message.properties
/D:/workspace-agile/agile/target/classes/cloud/agileframework/message_en.properties
/D:/workspace-agile/agile/target/classes/cloud/agileframework/message_zh.properties
/D:/workspace-agile/agile/target/classes/META-INF/additional-spring-configuration-metadata.json
/D:/workspace-agile/agile/target/classes/META-INF/spring-configuration-metadata.json
/D:/workspace-agile/agile/target/classes/META-INF/spring.factories
/D:/workspace-agile/agile/target/classes/static/favicon.ico
/framework/application-agile.yml
/framework/application-alibaba.yml
/framework/application-cache.yml
/framework/application-datasource.yml
/framework/application-druid.yml
/framework/application-ehcache.yml
/framework/application-es.yml
/framework/application-jpa.yml
/framework/application-kafka.yml
/framework/application-kaptcha.yml
/framework/application-log.yml
/framework/application-mvc.yml
/framework/application-redis.yml
/framework/bootstrap.yml
/framework/message.properties
/framework/message_en.properties
/framework/message_zh.properties
/D:/workspace-agile/agile/target/classes/application-agile.yml
```
配置加载过程不依赖spring，加载后会回填至spring容器，后续PropertiesUtil配置参数提取则优先使用spring容器中有效的environment，
所以该加载过程不影响微服务情况下动态配置管理的使用

#### 步骤 10: 国际化配置
借助Agile套件spring-util工具包中的MessageUtil，为spring的国际化配置文件扫描增加了Ant风格配置文件路径在家支持，并兼容原生配置方式。
原生spring中不支持spring.messages.basename不支持`*`匹配，agile为其扩展了该功能，并
保留对英文逗号`,`的多basename拆分。以message.properties作为国际化配置文件举例，该配置情况下组件将加载任意多层级路径下的message_x_x.properties
作为国际化配置文件，避免了多模块开发场景中复杂的配置文件路径声明。
```yaml
spring:
  messages:
    encoding: UTF-8
    basename: '**/message'
```

#### 步骤 11: 统一异常处理
组件通过@ControllerAdvice与HandlerExceptionResolver实现统一异常处理，会将捕获到的任何异常
加工为响应视图，开发者可以针对不同的异常类，定义不同的国际化响应信息。以自定义异常类`com.agile.YourException`
为例配置方式如下：
```properties
com.agile.YourException=100017:无效用户名或密码，请重新登录
```
其中`key`部分为异常引用名，`value`部分为`响应码`+`:`+`响应文`。最终生成的响应头信息如下：
```
{
    "head": {
        "ip": "192.168.101.42",
        "code": "100017",
        "msg": "无效用户名或密码，请重新登录",
        "status": "INTERNAL_SERVER_ERROR"
    },
    "result": null
}
```
其中的`status`部分会根据`响应码`的首位数字区分响应状态，默认2开头`响应码`与为配置国际化的异常为服务器异常，
会使用`HttpStatus.INTERNAL_SERVER_ERROR`并打印error日志，其余异常均视为后端正常业务异常，使用`HttpStatus.OK`
并且不打印error日志