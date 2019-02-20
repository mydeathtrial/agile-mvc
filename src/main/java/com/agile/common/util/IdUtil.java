package com.agile.common.util;

import com.agile.common.base.Constant;
import com.agile.common.properties.ApplicationProperties;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/2/19 9:32
 * @Description 主键生成工具
 * @since 1.0
 */
public class IdUtil {
    public static long generatorId(long workerId, long dataCenterId) {
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(workerId, dataCenterId);
        return snowflakeIdWorker.nextId();
    }

    public static long generatorId() {
        ApplicationProperties properties = FactoryUtil.getBean(ApplicationProperties.class);
        if (properties == null) {
            return generatorId(Constant.NumberAbout.ONE, Constant.NumberAbout.ONE);
        } else {
            return generatorId(properties.getWorkerId(), properties.getDataCenterId());
        }
    }
}
