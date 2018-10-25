package com.agile.common.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by 佟盟 on 2018/10/25
 */
public class AnnotationRuntimeProcessor {


    public static void paramHandler(Method method, Map<String, Object> inParam) {
        Annotation[] vs = method.getDeclaredAnnotations();
        for (Annotation v:vs) {
            System.out.println();
        }
    }
}
