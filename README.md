# Agile Framework 敏捷框架-后端单体架构版本
好的产品和技术架构会让开发人员从繁重的代码和业务当中解脱出来。代码就像文字，对了语法，写出来的句子才能正确的表意；项目就像文章，表意容易，写美了难；架构是提纲，知道每个段落该表什么，写出来的文章才思路清晰；规范是常用技巧的总结，告诉你如何把字写的漂亮如何把句子写的精妙。 每个产品、每个功能、乃至每一段代码都应该被视作一件艺术品，瓶子虽然都可以盛水，可青花瓷做的你不得不说贵的物有所值。

##思想
###精简
对传统MVC三层中的控制层、视图层以及持久层Dao工具、业务层Service做了统一封装，避免开发人员过多关注除业务层面以外的代码，把关注面缩小到Service层。并结合丰富的Util工具及代码生成器尽可能缩减代码量。

###低成本
由于开发人员仅关注业务层代码，一个方法确定一个API，所以开发人员只要会写Java Method，就可以进行开发。框架提供统一yml、properties配置文件，供开发人员组织项目所需各种组件的配置。以此降低开发人员的学习成本，以及对开发人员自身能力的限制。

###规范化
开发过程中一般要求开发人员对代码的书写要遵循各种规范，Agile通过统一控制层、统一Service基类、统一异常处理器、统一日志处理器、统一Dao工具、统一yml配置、工具包等方式，对API的URL设计、参数处理、格式化报文处理、日志、取传参数、响应码、Dao操作、yml属性配置等都做了统一实现，无需开发者关注，以此完成统一的代码规范化。

###灵活
脚手架的灵活与约束是一个必然存在的矛盾关系，Agile设计之初为适应大、中、小型项目，将所有web.xml、spring*.xml、log4j.xml、EntityMapper.xml、quartz.xml等等一系列近似硬性编码的配置类文件全部通过JavaConfig风格实现，去除任何xml或不必要的properties配置文件的依赖，如log4j2的LoggerFactory，可通过程序动态指定生成日志文件、定时任务可完全依赖于数据库配置，实现动态的定时任务更新、启停、新增。且各组件配置均设置启停开关，及各种参数的入口。

###低耗
全工程尽可能的利用单例模式，减少Java对象的创建与回收。通过ThreadLocal+及时清理的方式避免唯一Controller并发访问问题。

###无状态话
工程认证方面采用JWT + Redis + Spring Security Filter完成无状态话API认证，并且可以支持Token自动延时、Token失效、账号修改、强踢、多点、单点等功能

##工程目录结构
doc──文档包
src.main
    ├──java                                     #java源码包
            ├──com.agile.common                 #框架公共配置包
                            ├──annotation       #自定义注解
                            ├──aop              #切面配置
                            ├──base             #基础对象支撑
                            ├──cache            #缓存组件配置
                                ├──ehCache      #ehcache组件
                                ├──redis        #redis组件         
                            ├──config           #框架配置包
                            ├──container        #spring容器配置
                            ├──exception        #异常处理器
                            ├──factory          #工厂工具
                            ├──generator        #代码生成器
                            ├──interceptor      #hibernate拦截器
                            ├──kaptcha          #验证码
                            ├──listener         #监听器
                            ├──mvc              #基础mvc
                                ├──controller   #控制层
                                ├──model        #模型层
                                ├──service      #公共业务层
                            ├──properties       #框架属性配置类
                            ├──security         #认证组件配置
                            ├──util             #工具集
                            ├──view             #自定义视图
                            ├──viewResolver     #自定义视图处理器
            ├──com.agile.mvc                    #业务mvc
                            ├──entity           #表映射对象包
                            ├──service          #业务层
    ├──resources
            ├──agile.yml                        #系统配置文件
    ├──webapp
            ├──static                           #静态文件包
            ├──img                              #图片文件包
            ├──plus                             #前端组件包
                ├──jquery                       #jquery组件
                ├──swagger                      #swagger组件
test.main
    ├──java                                     #junit测试源码包
        ├──com.agile.mvc                        #junit业务mvc包
             ├──controller                      #junit模拟核心控制器
             ├──service                         #junit业务层
pom                                                #工程POM文件
agile-boot-parent                                  #Agile-Boot工程依赖POM文件
agile-parent                                       #Agile工程依赖POM文件