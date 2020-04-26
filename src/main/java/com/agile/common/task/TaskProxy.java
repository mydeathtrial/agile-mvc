package com.agile.common.task;

import com.agile.common.util.clazz.TypeReference;
import com.agile.common.util.object.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author 佟盟
 * 日期 2019/6/25 15:37
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class TaskProxy {
    @Transactional(rollbackFor = Exception.class)
    public void invoke(ApiBase apiInfo, Task task) throws InvocationTargetException, IllegalAccessException {
        Method method = apiInfo.getMethod();
        method.setAccessible(true);
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 1) {
            method.invoke(apiInfo.getBean(), ObjectUtil.to(task.getCode(), new TypeReference<>(parameterTypes[0])));
        } else {
            method.invoke(apiInfo.getBean());
        }
    }
}
