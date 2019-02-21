package com.agile.mvc.service;

import com.agile.common.mvc.service.BusinessService;
import org.springframework.stereotype.Service;
import io.swagger.annotations.Api;
import com.agile.mvc.entity.LogMainEntity;

/**
 * @author agile generator
 */
@Api(value = "[系统管理]日志表")
@Service
public class LogMainService extends BusinessService<LogMainEntity> {

}
