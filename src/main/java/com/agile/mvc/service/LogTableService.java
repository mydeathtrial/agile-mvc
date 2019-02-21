package com.agile.mvc.service;

import com.agile.common.mvc.service.BusinessService;
import org.springframework.stereotype.Service;
import io.swagger.annotations.Api;
import com.agile.mvc.entity.LogTableEntity;

/**
 * @author agile generator
 */
@Api(value = "[系统管理]日志相关表变动信息")
@Service
public class LogTableService extends BusinessService<LogTableEntity> {

}
