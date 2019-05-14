package com.agile.mvc.service;

import com.agile.common.mvc.service.BusinessService;
import com.agile.common.task.RunDetail;
import com.agile.common.task.Target;
import com.agile.common.task.Task;
import com.agile.common.task.TaskManager;
import com.agile.common.util.ApiUtil;
import com.agile.common.util.IdUtil;
import com.agile.mvc.entity.SysApiEntity;
import com.agile.mvc.entity.SysBtTaskApiEntity;
import com.agile.mvc.entity.SysTaskDetailEntity;
import com.agile.mvc.entity.SysTaskEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2019/5/9 16:04
 * 描述 定时任务持久层操作
 * @version 1.0
 * @since 1.0
 */
@Service
public class TaskManagerImpl extends BusinessService<SysTaskEntity> implements TaskManager {
    /**
     * 任务缓存
     */
    private Map<String, SysApiEntity> cache = new HashMap<>();

    public void test() {
        System.out.println("执行");
    }

    @Override
    public List<Task> getTask() {
        List<SysTaskEntity> list = dao.findAll(SysTaskEntity.class);
        return new ArrayList<>(list);
    }

    @Override
    public List<Target> getApis() {
        if (cache.size() == 0) {
            List<SysApiEntity> list = dao.findAll(SysApiEntity.builder().type(false).build());
            for (SysApiEntity task : list) {
                cache.put(task.getName(), task);
            }
        }

        return new ArrayList<>(cache.values());
    }

    @Override
    public List<Target> getApisByTaskCode(Long code) {
        List<SysApiEntity> list = dao.findAll(
                "select a.* from sys_api a left join sys_bt_task_api b on b.sys_api_id = a.sys_api_id where b.sys_task_id = ? order by b.order",
                SysApiEntity.class, code);
        return new ArrayList<>(list);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long save(Method method) {
        String methodGenericString = method.toGenericString();
        if (cache.containsKey(methodGenericString)) {
            return cache.get(methodGenericString).getSysApiId();
        } else {
            SysApiEntity apiEntity = dao.findOne(SysApiEntity.builder().name(methodGenericString).build());
            if (apiEntity != null) {
                cache.put(apiEntity.getName(), apiEntity);
                return apiEntity.getSysApiId();
            }
        }

        SysApiEntity entity = dao.saveOrUpdate(
                SysApiEntity
                        .builder()
                        .type(ApiUtil.containsApiInfo(method.toGenericString()))
                        .sysApiId(IdUtil.generatorId())
                        .name(methodGenericString)
                        .build()
        );
        return entity.getSysApiId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Task task, Method method) {
        Long taskCode = save(task);
        Long targetCode = save(method);
        dao.saveAndReturn(SysBtTaskApiEntity.builder()
                .sysTaskId(IdUtil.generatorId())
                .sysTaskId(taskCode)
                .sysApiId(targetCode)
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
