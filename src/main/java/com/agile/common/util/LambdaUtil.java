package com.agile.common.util;

/**
 * @author 佟盟
 * 日期 2019/6/28 13:36
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class LambdaUtil {
    /**
     * 处理lambda异常抛出问题
     *
     * @param e   异常
     * @param <E> 泛型
     * @throws E 泛型
     */
    static <E extends Throwable> void doThrow(Throwable e) throws E {
        throw (E) e;
    }
}
