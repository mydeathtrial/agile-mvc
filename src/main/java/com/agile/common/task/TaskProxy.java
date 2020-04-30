package com.agile.common.task;

import com.agile.common.util.FactoryUtil;
import com.agile.common.util.clazz.ClassUtil;
import com.agile.common.util.clazz.TypeReference;
import com.agile.common.util.object.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author 佟盟
 * 日期 2019/6/25 15:37
 * 描述 定时任务调用代理
 * @version 1.0
 * @since 1.0
 */
public class TaskProxy {
    @Transactional(rollbackFor = Exception.class)
    public void invoke(Method method, Task task) throws InvocationTargetException, IllegalAccessException {

        method.setAccessible(true);
        Class<?>[] parameterTypes = method.getParameterTypes();

        Object bean = FactoryUtil.getBean(method.getDeclaringClass());
        if (bean == null) {
            bean = ClassUtil.newInstance(method.getDeclaringClass());
        }
        if (parameterTypes.length == 1) {
            method.invoke(bean, ObjectUtil.to(task.getCode(), new TypeReference<>(parameterTypes[0])));
        } else {
            method.invoke(bean);
        }
    }
}
