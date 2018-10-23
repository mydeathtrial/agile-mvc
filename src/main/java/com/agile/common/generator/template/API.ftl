{
"swagger" : "2.0",
    "info" : {
        "version" : "1.0.0",
        "title" : "Agile API平台",
        "contact" : {
            "email" : "mydeathtrial@163.com"
        }
    },
    "tags" : [
<#list list as property>
        {
            "name" : "${property.serviceClassName}",
            "description" : "${property.tableComment}"
        }<#if property_has_next>,</#if></#list>
    ],
    "schemes" : [ "http", "https" ],
    "paths" : {
<#list list as property>
        "/${property.serviceClassName}/save" :{
            "get" : {
                "tags":["${property.serviceClassName}"],
                "summary" : "${property.tableComment}",
                "operationId" : "${property.serviceClassName}_save",
                "consumes" : [ "multipart/form-data","application/x-www-form-urlencoded" ],
                "produces" : [ "application/json","application/xml" ],
                "parameters" : [
                <#list property.columnList as params><#if params.isPrimaryKey == "false">
                    {
                        "name" : "${params.propertyName}",
                        "in" : "formData",
                        "description" : "${params.remarks}",
                        "required" : ${params.nullable},
                        "type" : "string"
                    }<#if params_has_next>,</#if></#if></#list>
                ],
                "responses" : {
                    "200" : {
                        "description" : "成功"
                    },
                    "500" : {
                        "description" : "系统程序异常"
                    }
                },
                "description" : "保存${property.tableComment}"
            }
        },
        "/${property.entityClassName}" :{
            "post" : {
                "tags":["${property.serviceClassName}"],
                "summary" : "${property.tableComment}",
                "operationId" : "${property.entityClassName}_save",
                "consumes" : [ "multipart/form-data","application/x-www-form-urlencoded"],
                "produces" : [ "application/json","application/xml" ],
                "parameters" : [
    <#list property.columnList as params><#if params.isPrimaryKey == "false">
                    {
                        "name" : "${params.propertyName}",
                        "in" : "formData",
                        "description" : "${params.remarks}",
                        "required" : ${params.nullable},
                        "type" : "string"
                    }<#if params_has_next>,</#if></#if></#list>
                ],
                "responses" : {
                    "200" : {
                        "description" : "成功"
                    },
                    "500" : {
                        "description" : "系统程序异常"
                    }
                },
                "description" : "保存${property.tableComment}"
            }
        },
        "/${property.serviceClassName}/delete" :{
            "get" : {
                "tags":["${property.serviceClassName}"],
                "summary" : "${property.tableComment}",
                "operationId" : "${property.serviceClassName}_delete",
                "consumes" : [ "multipart/form-data","application/x-www-form-urlencoded" ],
                "produces" : [ "application/json","application/xml" ],
                "parameters" : [ {
                    "name" : "ids",
                    "in" : "formData",
                    "description" : "主键字符串",
                    "required" : true,
                    "type" : "string"
                } ],
                "responses" : {
                    "200" : {
                        "description" : "成功"
                    },
                    "500" : {
                        "description" : "系统程序异常"
                    }
                },
                "description" : "删除${property.tableComment}"
            }
        },
        "/${property.entityClassName}" :{
            "delete" : {
                "tags":["${property.serviceClassName}"],
                "summary" : "${property.tableComment}",
                "operationId" : "${property.entityClassName}_delete",
                "consumes" : [ "multipart/form-data","application/x-www-form-urlencoded" ],
                "produces" : [ "application/json","application/xml" ],
                "parameters" : [ {
                    "name" : "id",
                    "in" : "query",
                    "description" : "主键字符串",
                    "required" : true,
                    "type" : "array",
                    "items":{
                            "type": "string"
                        }

                } ],
                "responses" : {
                    "200" : {
                        "description" : "成功"
                    },
                    "500" : {
                        "description" : "系统程序异常"
                    }
                },
                "description" : "删除${property.tableComment}"
            }
        },
        "/${property.entityClassName}/{id}" :{
            "delete" : {
                "tags":["${property.serviceClassName}"],
                "summary" : "${property.tableComment}",
                "operationId" : "${property.entityClassName}_delete3",
                "consumes" : [ "multipart/form-data","application/x-www-form-urlencoded" ],
                "produces" : [ "application/json","application/xml" ],
                "parameters" : [ {
                    "name" : "id",
                    "in" : "path",
                    "description" : "主键字符串",
                    "required" : true,
                    "type" : "string"
                } ],
                "responses" : {
                    "200" : {
                        "description" : "成功"
                    },
                    "500" : {
                        "description" : "系统程序异常"
                    }
                },
                "description" : "删除${property.tableComment}"
            }
        },
        "/${property.serviceClassName}/update" :{
            "get" : {
                "tags":["${property.serviceClassName}"],
                "summary" : "${property.tableComment}",
                "operationId" : "${property.serviceClassName}_update",
                "consumes" : [ "multipart/form-data","application/x-www-form-urlencoded" ],
                "produces" : [ "application/json","application/xml" ],
                "parameters" : [
                <#list property.columnList as params>
                    {
                        "name" : "${params.propertyName}",
                        "in" : "formData",
                        "description" : "${params.remarks}",
                        "required" : ${params.nullable},
                        "type" : "string"
                    }<#if params_has_next>,</#if></#list>
                ],
                "responses" : {
                    "200" : {
                        "description" : "成功"
                    },
                    "500" : {
                        "description" : "系统程序异常"
                    }
                },
                "description" : "更新${property.tableComment}"
            }
        },
        "/${property.serviceClassName}" :{
            "put" : {
                "tags":["${property.serviceClassName}"],
                "summary" : "${property.tableComment}",
                "operationId" : "${property.serviceClassName}_update",
                "consumes" : [ "multipart/form-data","application/x-www-form-urlencoded" ],
                "produces" : [ "application/json","application/xml" ],
                "parameters" : [
    <#list property.columnList as params>
                    {
                        "name" : "${params.propertyName}",
                        "in" : "formData",
                        "description" : "${params.remarks}",
                        "required" : ${params.nullable},
                        "type" : "string"
                    }<#if params_has_next>,</#if></#list>
                ],
                "responses" : {
                    "200" : {
                        "description" : "成功"
                    },
                    "500" : {
                        "description" : "系统程序异常"
                    }
                },
                "description" : "更新${property.tableComment}"
            }
        },
        "/${property.serviceClassName}/query" :{
            "get" : {
                "tags":["${property.serviceClassName}"],
                "summary" : "${property.tableComment}",
                "operationId" : "${property.serviceClassName}_query",
                "consumes" : [ "application/json","application/xml" ],
                "produces" : [ "application/json","application/xml" ],
                "parameters" : [
                    {
                        "name" : "page",
                        "in" : "query",
                        "description" : "第几页",
                        "required" : false,
                        "type" : "integer"
                    },{
                        "name" : "size",
                        "in" : "query",
                        "description" : "每页条数",
                        "required" : false,
                        "type" : "integer"
                    }
                ],
                "responses" : {
                    "200" : {
                        "description" : "成功",
                        "schema" : {
                            "type" : "array",
                            "items" : {
                                "$ref" : "#/definitions/${property.entityClassName}"
                            }
                        }
                    },
                    "500" : {
                        "description" : "系统程序异常"
                    }
                },
                "description" : "查询${property.tableComment}"
            }
        },
        "/${property.serviceClassName}" :{
            "get" : {
                "tags":["${property.serviceClassName}"],
                "summary" : "${property.tableComment}",
                "operationId" : "${property.serviceClassName}_query",
                "consumes" : [ "application/json","application/xml" ],
                "produces" : [ "application/json","application/xml" ],
                "parameters" : [
                    {
                        "name" : "page",
                        "in" : "query",
                        "description" : "第几页",
                        "required" : false,
                        "type" : "integer"
                    },{
                        "name" : "size",
                        "in" : "query",
                        "description" : "每页条数",
                        "required" : false,
                        "type" : "integer"
                    }
                ],
                "responses" : {
                    "200" : {
                        "description" : "成功",
                        "schema" : {
                            "type" : "array",
                            "items" : {
                                "$ref" : "#/definitions/${property.entityClassName}"
                            }
                        }
                    },
                    "500" : {
                        "description" : "系统程序异常"
                    }
                },
                "description" : "查询${property.tableComment}"
            }
        },
        "/${property.serviceClassName}/{id}" :{
            "get" : {
                "tags":["${property.serviceClassName}"],
                "summary" : "${property.tableComment}",
                "operationId" : "${property.serviceClassName}_query",
                "consumes" : [ "application/json","application/xml" ],
                "produces" : [ "application/json","application/xml" ],
                "parameters" : [
                    {
                        "name" : "id",
                        "in" : "path",
                        "description" : "主键字符串",
                        "required" : true,
                        "type" : "string"
                    }
                ],
                "responses" : {
                    "200" : {
                        "description" : "成功",
                        "schema" : {
                            "type" : "array",
                            "items" : {
                                "$ref" : "#/definitions/${property.entityClassName}"
                            }
                        }
                    },
                    "500" : {
                        "description" : "系统程序异常"
                    }
                },
                "description" : "查询${property.tableComment}"
            }
        }
    <#if property_has_next>,</#if>
</#list>
    },
    "securityDefinitions" : {
        "petstore_auth" : {
            "type" : "oauth2",
            "authorizationUrl" : "http://petstore.swagger.io/oauth/dialog",
            "flow" : "implicit",
            "scopes" : {
                "write:pets" : "modify pets in your account",
                "read:pets" : "read your pets"
            }
        },
        "api_key" : {
            "type" : "apiKey",
            "name" : "api_key",
            "in" : "header"
        }
    },
    "definitions" : {
<#list list as property>
        "${property.entityClassName}" : {
            "type" : "object",
            "properties" : {
    <#list property.columnList as params>
                "${params.propertyName}" : {
                    "type" : "${params.propertyTypeOfSwagger}",
                    "description" : "${params.remarks}"
                }<#if params_has_next>,</#if>
    </#list>
            }
        }<#if property_has_next>,</#if>
</#list>
    },
    "externalDocs" : {
        "description" : "",
        "url" : "http://127.0.0.1:8080"
    }
}