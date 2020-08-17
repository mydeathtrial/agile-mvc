package com.agile.mvc.service;

import cloud.agileframework.task.TaskManager;
import cloud.agileframework.task.exception.NotFoundTaskException;
import cloud.agileframework.validate.annotation.Validate;
import com.agile.common.annotation.AgileService;
import com.agile.common.annotation.Mapping;
import com.agile.common.base.RETURN;
import com.agile.mvc.entity.SysTaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.agile.common.base.RETURN.SUCCESS;

/**
 * @author 佟盟
 * 日期 2019/5/9 19:37
 * 描述 定时任务对外暴露操作API
 * @version 1.0
 * @since 1.0
 */
@AgileService
@Mapping("/api/task")
public class TaskRevealService {
    @Autowired(required = false)
    private TaskManager taskManager;


    /**
     * 根据定时任务对象添加定时任务
     *
     * @return 是否添加成功
     */
    @Validate(beanClass = SysTaskEntity.class)
    @Mapping(method = RequestMethod.POST)
    public RETURN updateTask(SysTaskEntity task) throws NoSuchMethodException, NotFoundTaskException {
        taskManager.updateTask(task);
        return SUCCESS;
    }

    /**
     * 删除定时任务
     *
     * @return 是否成功
     */
    @Mapping("/delete/{id}")
    public RETURN removeTask(Long id) throws NotFoundTaskException {
        taskManager.removeTask(id);
        return SUCCESS;
    }

    /**
     * 停止定时任务
     *
     * @return 是否成功
     */
    @Validate(value = "id", nullable = false, isBlank = false)
    @Mapping("/stop/{id}")
    public RETURN stopTask(Long id) throws NotFoundTaskException {
        taskManager.stopTask(id);
        return SUCCESS;
    }


    /**
     * 开启定时任务
     *
     * @return 是否成功
     */
    @Validate(value = "id", nullable = false, isBlank = false)
    @Mapping("/start/{id}")
    public RETURN startTask(Long id) throws NotFoundTaskException {
        taskManager.startTask(id);
        return SUCCESS;
    }


    public void test(String id){
        System.out.println("执行"+id);
    }

    public static void main(String[] args) throws NoSuchMethodException {
        System.out.println(TaskRevealService.class.getMethod("test",String.class).toGenericString());
    }
}
