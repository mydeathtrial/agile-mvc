package com.agile.mvc.service;

import com.agile.common.base.Constant;
import com.agile.common.mvc.service.BusinessService;
import com.agile.common.task.RunDetail;
import com.agile.common.task.Target;
import com.agile.common.task.Task;
import com.agile.common.task.TaskManager;
import com.agile.common.util.IdUtil;
import com.agile.mvc.entity.SysApiEntity;
import com.agile.mvc.entity.SysBtTaskApiEntity;
import com.agile.mvc.entity.SysTaskDetailEntity;
import com.agile.mvc.entity.SysTaskEntity;
import org.springframework.transaction.annotation.Transactional;

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
public class TaskManagerImpl extends BusinessService<SysTaskEntity> implements TaskManager {
    @Override
    public List<Task> getTask() {
        List<SysTaskEntity> list = dao.findAll(SysTaskEntity.builder().build());
        return new ArrayList<>(list);
    }

    @Override
    public List<Target> getApisByTaskCode(Long code) {
        List<SysApiEntity> list = dao.findAll(
                "select a.* from sys_api a left join sys_bt_task_api b on b.sys_api_id = a.sys_api_id where b.sys_task_id = ? order by b.order",
                SysApiEntity.class, code);
        return new ArrayList<>(list);
    }

    @Override
    public List<Task> getTasksByApiCode(String code) {
        List<SysTaskEntity> list = dao.findAll("SELECT\n" +
                "sys_task.sys_task_id,\n" +
                "sys_task.`name`,\n" +
                "sys_task.`status`,\n" +
                "sys_task.`enable`,\n" +
                "sys_task.cron,\n" +
                "sys_task.sync,\n" +
                "sys_task.update_time,\n" +
                "sys_task.create_time\n" +
                "FROM\n" +
                "sys_task\n" +
                "LEFT JOIN sys_bt_task_api ON sys_bt_task_api.sys_task_id = sys_task.sys_task_id\n" +
                "LEFT JOIN sys_api ON sys_bt_task_api.sys_api_id = sys_api.sys_api_id\n" +
                "WHERE\n" +
                "sys_api.`name` = ?\n", SysTaskEntity.class, code);
        return new ArrayList<>(list);
    }

    @Override
    public Long save(Method method, boolean type) {
        String methodGenericString = method.toGenericString();
        SysApiEntity api = dao.findOne(SysApiEntity.builder().name(methodGenericString).build());
        if (api != null) {
            return api.getSysApiId();
        } else {
            SysApiEntity entity = dao.saveOrUpdate(
                    SysApiEntity
                            .builder()
                            .type(type)
                            .sysApiId(IdUtil.generatorId())
                            .name(methodGenericString)
                            .build()
            );
            return entity.getSysApiId();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Task task, Method method) {
        Long taskCode = save(task);
        Long targetCode = save(method, false);
        dao.saveAndReturn(SysBtTaskApiEntity.builder()
                .sysBtTaskApiId(IdUtil.generatorId())
                .sysTaskId(taskCode)
                .sysApiId(targetCode)
                .order(Constant.NumberAbout.ONE)
                .build()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long save(Task task) {
        if (task instanceof SysTaskEntity && task.getCode() == null) {
            ((SysTaskEntity) task).setSysTaskId(IdUtil.generatorId());
        }
        task = dao.saveOrUpdate(task);
        return task.getCode();
    }

    @Override
    public void remove(Long id) {
        dao.deleteInBatch(dao.findAll(SysBtTaskApiEntity.builder().sysTaskId(id).build()));
        dao.deleteById(SysTaskEntity.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void run(Long taskCode) {
        SysTaskEntity task = dao.findOne(SysTaskEntity.class, taskCode);
        task.setStatus("2000");
        dao.update(task);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void finish(Long taskCode) {
        SysTaskEntity task = dao.findOne(SysTaskEntity.class, taskCode);
        task.setStatus("1000");
        dao.update(task);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void logging(RunDetail runDetail) {
        dao.save(SysTaskDetailEntity.builder()
                .sysTaskId(runDetail.getTaskCode())
                .sysTaskInfoId(IdUtil.generatorId())
                .ending(runDetail.isEnding())
                .startTime(runDetail.getStartTime())
                .endTime(runDetail.getEndTime())
                .log(runDetail.getLog().toString())
                .build()

        );
    }
}
