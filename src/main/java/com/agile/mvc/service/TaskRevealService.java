package com.agile.mvc.service;

import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.exception.NotFoundTaskException;
import com.agile.common.mvc.service.BusinessService;
import com.agile.common.mvc.service.TaskService;
import com.agile.mvc.entity.SysTaskEntity;
import org.springframework.stereotype.Service;

/**
 * @author 佟盟
 * 日期 2019/5/9 19:37
 * 描述 定时任务对外暴露操作API
 * @version 1.0
 * @since 1.0
 */
@Service
public class TaskRevealService extends BusinessService<SysTaskEntity> {
    private final TaskService taskService;

    public TaskRevealService(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 根据定时任务对象添加定时任务
     *
     * @return 是否添加成功
     */
    public RETURN addTask() throws IllegalAccessException, NoSuchIDException, NoSuchMethodException {

        SysTaskEntity task = super.saveAndReturn();
        if (task != null) {
            taskService.addTask(task);
        }
        return RETURN.SUCCESS;
    }

    /**
     * 删除定时任务
     *
     * @return 是否成功
     */
    public RETURN removeTask() throws NotFoundTaskException {
        String id = this.getInParam("id", String.class);
        taskService.removeTask(id);
        this.dao.deleteById(SysTaskEntity.class, id);
        return RETURN.SUCCESS;
    }

    /**
     * 停止定时任务
     *
     * @return 是否成功
     */
    public RETURN stopTask() throws NotFoundTaskException {
        String id = this.getInParam("id", String.class);
        taskService.stopTask(id);

        //同步状态到数据库
        SysTaskEntity entity = dao.findOne(SysTaskEntity.class, id);
        entity.setState(false);
        dao.update(entity);

        return RETURN.SUCCESS;
    }


    /**
     * 开启定时任务
     *
     * @return 是否成功
     */
    public RETURN startTask() throws NotFoundTaskException {
        String id = this.getInParam("id", String.class);
        taskService.startTask(id);

        //同步状态到数据库
        SysTaskEntity entity = dao.findOne(SysTaskEntity.class, id);
        entity.setState(true);
        dao.update(entity);

        return RETURN.SUCCESS;
    }

    /**
     * 更新定时任务
     *
     * @return 是否成功
     */
    public RETURN updateTask() throws NotFoundTaskException, IllegalAccessException, NoSuchIDException {
        SysTaskEntity entity = super.updateAndReturn();
        taskService.updateTask(entity);

        return RETURN.SUCCESS;
    }
}
