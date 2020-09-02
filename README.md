
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

* **强大的参数解析器**：
参数解析器由agile系列套件common-util提供参数解析，为spring-mvc扩展了超乎想象的参数解析能力，注入逗号分隔字符串转集合、下划线属性json转pojo对象、多层对象嵌套、不同类型集合数据参数识别、字符串时间语句分析、文件上传、文件输入流等等能力。
将开发人员对参数的复杂类型转换直接省略掉，进一步缩减非业务代码量。

* **可定制化统一响应模板**：
agile-mvc提供可自定义响应模板入口，在不同项目中可以定义个性化的统一响应格式，响应头、体信息均可定制。

* **集成agile-validate参数验证控件**：
借助agile系列套件agile-validate方法参数验证组件，实现对springmvc原生控制层方法、@AgileService服务层方法的参数验证注解解析，弥补原生springmvc在参数验证注解方面能力的不足，其中包括了自定义逻辑的业务参数验证的支持。

* **自动化配置文件掃描**：
借助common-util的PropertiesUtil工具实现对环境内的任意配置文件扫描，避免了硬编码方式指定配置文件，并在功能初始化阶段以由低到高的优先级对扫描到的配置文件进行打印。该功能解开了在多模块开发过程中配置文件规范的限制，确保模块的扩展过程可携带默认配置。
且配置扫描原则为外层>内层 同层按照命名顺序排列优先级。

* **统一异常处理**：
对全局任何异常均做了统一拦截、格式化响应、日志打印

* **异常、国际化响应报文关联**：
所有异常均支持国际化配置文件关联，当拦截到异常时，优先获取国际化配置，组装响应报文，以此种方式省略了开发人员对异常情况的处理。可以在程序任何位置直接将业务错误提示以java throw的方式直接返回给前端。

# 思想
### 精简
对传统MVC三层中的控制层、视图层以及持久层Dao工具、业务层做了统一封装，避免开发人员过多关注除业务层面以外的代码，把关注面缩小到Service层。并结合丰富的Util工具及代码生成器尽可能缩减代码量。

### 低成本
由于开发人员仅关注业务层代码，一个方法确定一个API，所以开发人员只要会写Java方法，就可以进行开发。框架提供统一yml、properties配置文件，供开发人员组织项目所需各种组件的配置。以此降低开发人员的学习成本，以及对开发人员自身能力的限制。

### 规范化
开发过程中一般要求开发人员对代码的书写要遵循各种规范，Agile通过统一控制层、统一Service基类、统一异常处理器、统一日志处理器、统一Dao工具、统一yml配置、工具包等方式，对API的URL设计、参数处理、格式化报文处理、日志、取传参数、响应码、Dao操作、yml属性配置等都做了统一实现，缩减代码量以减少违规代码的出现频率，配合checkstyle完成代码规范化。

### 灵活
脚手架的灵活与约束是一个必然存在的矛盾关系，Agile设计之初为适应大、中、小型项目，将所有web.xml、spring*.xml、log4j.xml、EntityMapper.xml、quartz.xml等等一系列近似硬性编码的配置类文件全部通过JavaConfig风格实现，去除任何xml或不必要的properties配置文件的依赖，如log4j2的LoggerFactory，可通过程序动态指定生成日志文件、定时任务可完全依赖于数据库配置，实现动态的定时任务更新、启停、新增。且各组件配置均设置启停开关，及各种参数的入口。

### 低耗
全工程尽可能的利用单例模式，减少Java对象的创建与回收。通过ThreadLocal+及时清理的方式避免唯一Controller并发访问问题。

### 无状态话
工程认证方面采用JWT + Redis + Spring Security Filter完成无状态话API认证，并且可以支持Token自动延时、Token失效、账号修改、强踢、多点、单点等功能

-------
![龙江银行](https://images.gitee.com/uploads/images/2019/0912/192435_6f8ca194_451899.gif "logo.gif")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/193812_fcfe31ca_451899.jpeg "timg 拷贝.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/193722_c45d1a93_451899.jpeg "timg2.jpeg")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/193918_e3c779ca_451899.jpeg "jtw.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/194129_179b6342_451899.png "logo1.png")

-------

##工程目录结构
```$xslt
└─com
    └─agile
        ├─common
        │  ├─annotation                 自定义注解
        │  ├─aop                        自定义且面
        │  ├─base                       基础类
        │  ├─cache                      缓存
        │  │  ├─ehcache
        │  │  └─redis
        │  ├─config                     配置
        │  ├─container                  容器
        │  ├─exception                  异常
        │  ├─factory                    工厂
        │  ├─filter                     过滤器
        │  ├─generator                  生成器
        │  ├─listener                   监听器
        │  ├─log                        日志
        │  ├─mvc                        公用mvc
        │  │  ├─controller              公用控制器
        │  │  ├─model                   持久层
        │  │  │  └─dao
        │  │  └─service                 公用服务
        │  ├─mybatis                    mybatis相关
        │  ├─properties                 属性配置
        │  ├─security                   安全
        │  ├─task                       定时任务
        │  ├─util                       工具
        │  ├─validate                   验证
        │  ├─view                       视图
        │  └─viewResolver               视图解析器
        └─mvc
            ├─entity                    业务POJO
            └─service                   服务层  
└─test                                  单元测试包
    └─main
        ├─java
        │  └─com
        │      └─agile
        │          └─mvc
        │              ├─main           主Junit测试类
        │              └─service        
        │                  └─xx         API测试类
        └─resources
            ├─data
            │   └─xx.json               入参脚本 
            └─application-test.yml      单元测试覆盖配置文件
pom                                     工程POM文件
```