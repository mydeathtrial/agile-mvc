
<img src="https://images.gitee.com/uploads/images/2019/0912/201757_6d887de5_451899.jpeg" width="20%" height="20%" />

# Spring-Agile
[![CircleCI](https://circleci.com/gh/alibaba/spring-cloud-alibaba/tree/master.svg?style=svg)](https://circleci.com/gh/alibaba/spring-cloud-alibaba/tree/master)
[![Maven Central](https://img.shields.io/maven-central/v/com.alibaba.cloud/spring-cloud-alibaba-dependencies.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.alibaba.cloud%20AND%20a:spring-cloud-alibaba-dependencies)
[![Codecov](https://codecov.io/gh/alibaba/spring-cloud-alibaba/branch/master/graph/badge.svg)](https://codecov.io/gh/alibaba/spring-cloud-alibaba)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Gitter](https://badges.gitter.im/alibaba/nacos.svg)](https://gitter.im/alibaba/nacos?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)   [![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Gitter](https://travis-ci.org/alibaba/nacos.svg?branch=master)](https://travis-ci.org/alibaba/nacos)

-------
Agile-Framework 系列脚手架致力于帮助开发人员从繁重的代码当中解脱出来，用最简单的代码勾画出最美的艺术。Spring-Agile提供Web开发一站式解决方案。此项目包含Web开发相关的各种必要组件及辅助工具集，方便开发者通过 Agile 编程模型轻松使用这些组件来开发单体应用及分布式应用。

参考文档 请查看 [WIKI](https://gitee.com/mydeathtrial/agile/wikis) 。

## 主要功能
* **自动化API地址映射**：。
* **自动化统一参数解析**：。
* **统一报文模板**：。
* **通用持久层工具**：。
* **动态SQL解析**：。
* **认证链**：。
* **代码生成器**：。
* **动态日志**：。
* **动态任务调度**：。
* **参数验证控件**：。
* **动态逻辑层**：。
* **自动化配置掃描**：。
* **自动化国际化配置掃描**：。
* **统一异常处理**：。
* **...**

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
![龙江银行](https://images.gitee.com/uploads/images/2019/0912/192435_6f8ca194_451899.gif "logo.gif")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/193812_fcfe31ca_451899.jpeg "timg 拷贝.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/193722_c45d1a93_451899.jpeg "timg2.jpeg")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/193918_e3c779ca_451899.jpeg "jtw.jpg")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0912/194129_179b6342_451899.png "logo1.png")




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