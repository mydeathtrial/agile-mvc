package com.agile.mvc.service;

import com.agile.common.annotation.Mapping;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.exception.NotFoundTaskException;
import com.agile.common.mvc.service.BusinessService;
import com.agile.common.mvc.service.TaskService;
import com.agile.mvc.entity.SysTaskEntity;

import static com.agile.common.base.RETURN.SUCCESS;

/**
 * @author 佟盟
 * 日期 2019/5/9 19:37
 * 描述 定时任务对外暴露操作API
 * @version 1.0
 * @since 1.0
 */
public class TaskRevealService extends BusinessService<SysTaskEntity> {
    private TaskService taskService;

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
        if (task == null) {
            return RETURN.PARAMETER_ERROR;
        }
        taskService.addTask(task);
        return SUCCESS;
    }

    /**
     * 删除定时任务
     *
     * @return 是否成功
     */
    public RETURN removeTask() throws NotFoundTaskException {
        Long id = this.getInParam("id", Long.class);
        if (id == null) {
            return RETURN.PARAMETER_ERROR;
        }
        taskService.removeTask(id);
        this.dao.deleteById(SysTaskEntity.class, id);
        return SUCCESS;
    }

    /**
     * 停止定时任务
     *
     * @return 是否成功
     */
    public RETURN stopTask() throws NotFoundTaskException {
        Long id = this.getInParam("id", Long.class);
        if (id == null) {
            return RETURN.PARAMETER_ERROR;
        }
        taskService.stopTask(id);

        //同步状态到数据库
        SysTaskEntity entity = dao.findOne(SysTaskEntity.class, id);
        entity.setEnable(false);
        dao.update(entity);

        return SUCCESS;
    }


    /**
     * 开启定时任务
     *
     * @return 是否成功
     */
    public RETURN startTask() throws NotFoundTaskException {
        Long id = this.getInParam("id", long.class);
        if (id == null) {
            return RETURN.PARAMETER_ERROR;
        }
        taskService.startTask(id);

        //同步状态到数据库
        SysTaskEntity entity = dao.findOne(SysTaskEntity.class, id);
        entity.setEnable(true);
        dao.update(entity);

        return SUCCESS;
    }

    /**
     * 更新定时任务
     *
     * @return 是否成功
     */
    public RETURN updateTask() throws NotFoundTaskException, IllegalAccessException, NoSuchIDException {
        SysTaskEntity entity = super.updateAndReturn();
        taskService.updateTask(entity);

        return SUCCESS;
    }

    /**
     * 描述：
     * @author 佟盟
    */
    @Mapping(path = "/test")
    public Object test() {
        logger.error("1231231231231231");
        return SUCCESS;
    }

}
