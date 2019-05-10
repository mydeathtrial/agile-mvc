package com.agile.mvc.service;

import com.agile.common.mvc.service.BusinessService;
import com.agile.common.task.Target;
import com.agile.common.task.Task;
import com.agile.common.task.TaskManager;
import com.agile.common.util.IdUtil;
import com.agile.mvc.entity.SysBtTaskTargetEntity;
import com.agile.mvc.entity.SysTaskEntity;
import com.agile.mvc.entity.SysTaskTargetEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2019/5/9 16:04
 * 描述 定时任务持久层操作
 * @version 1.0
 * @since 1.0
 */
@Service
public class TargetManagerImpl extends BusinessService<SysTaskEntity> implements TaskManager {

    @Override
    public List<Task> getTask() {
        List<SysTaskEntity> list = dao.findAll(SysTaskEntity.class);
        return new ArrayList<>(list);
    }

    @Override
    public List<Target> getTaskTarget() {
        List<SysTaskTargetEntity> list = dao.findAll(SysTaskTargetEntity.class);
        return new ArrayList<>(list);
    }

    @Override
    public List<Target> getTaskTargetByTaskCode(String code) {
        List<SysTaskTargetEntity> list = dao.findAll(
                "select a.* from sys_task_target a left join sys_bt_task_target b on b.sys_task_target_id = a.sys_task_target_id where b.sys_task_id = ? order by b.order",
                SysTaskTargetEntity.class, code);
        return new ArrayList<>(list);
    }

    @Override
    public String save(String methodGenericString) {
        SysTaskTargetEntity entity = dao.saveOrUpdate(
                SysTaskTargetEntity
                        .builder()
                        .sysTaskTargetId(methodGenericString)
                        .build()
        );
        return entity.getCode();
    }

    @Override
    public void save(Task task, Target target) {
        String taskCode = save(task);
        String targetCode = save(target.getCode());
        dao.saveAndReturn(SysBtTaskTargetEntity.builder()
                .sysTaskId(IdUtil.generatorId().toString())
                .sysTaskId(taskCode)
                .sysTaskTargetId(targetCode)
                .build()
        );
    }

    @Override
    public String save(Task task) {
        if (task instanceof SysTaskEntity && task.getCode() == null) {
            ((SysTaskEntity) task).setSysTaskId(IdUtil.generatorId().toString());
        }
        task = dao.saveOrUpdate(task);
        return task.getCode();
    }

    @Override
    public Target target(Method method) {
        return SysTaskTargetEntity.builder()
                .sysTaskTargetId(method.toGenericString())
                .build();
    }
}
