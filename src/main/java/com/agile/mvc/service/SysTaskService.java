package com.agile.mvc.service;

import com.agile.common.mvc.service.BusinessService;
import org.springframework.stereotype.Service;
import io.swagger.annotations.Api;
import com.agile.mvc.entity.SysTaskEntity;

/**
 * @author agile generator
 */
@Api(value = "[系统管理]定时任务")
@Service
public class SysTaskService extends BusinessService<SysTaskEntity> {

}
