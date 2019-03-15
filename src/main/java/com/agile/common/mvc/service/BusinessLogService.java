package com.agile.common.mvc.service;

import com.agile.common.annotation.NotAPI;
import com.agile.common.factory.PoolFactory;
import com.agile.common.util.ObjectUtil;
import com.agile.common.util.RandomStringUtil;
import com.agile.mvc.entity.LogMainEntity;
import com.agile.mvc.entity.LogTableEntity;
import com.agile.mvc.entity.LogValueEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 日志服务
 *
 * @author mydeathtrial on 2017/3/21
 */
@Service
@NotAPI
public class BusinessLogService extends MainService {
    private static final String LOG_MAIN_INDEX = "BUSINESS_LOG_INDEX";
    private static final String LOG_TABLE_ORDER = "LOG_TABLE_ORDER";
    private static final int LENGTH = 8;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_IMUM_POOL_SIZE = 30;
    private static final int KEEP_ALIVE_TIME = 1;
    /**
     * 日志线程池
     */
    private static ThreadPoolExecutor pool = PoolFactory.pool(CORE_POOL_SIZE, MAX_IMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.DiscardPolicy());

    /**
     * 操作类型
     */
    private enum OperationType {
        /**
         * 新增
         */
        Create,
        /**
         * 删除
         */
        Delete,
        /**
         * 修改
         */
        Update
    }

    /**
     * 创建操作日志
     *
     * @param businessCode 业务编码
     * @param oldObject    旧对象
     * @param newObject    新对象
     */
    public void createLog(String businessCode, Object oldObject, Object newObject) {
        pool.execute(new BusinessLogThread(businessCode, oldObject, newObject, null, null));
    }

    /**
     * 创建操作日志
     *
     * @param targetType   业务日志检索检索类型
     * @param targetCode   业务日志检索对象主键
     * @param businessCode 业务编码
     * @param oldObject    旧对象
     * @param newObject    新对象
     */
    public void createLog(String businessCode, Object oldObject, Object newObject, String targetType, String targetCode) {
        pool.execute(new BusinessLogThread(businessCode, oldObject, newObject, targetType, targetCode));
    }

    /**
     * 业务日志线程
     */
    private class BusinessLogThread implements Runnable {
        private String businessCode;
        private Object oldObject;
        private Object newObject;
        private String targetType;
        private String targetCode;

        BusinessLogThread(String businessCode, Object oldObject, Object newObject, String targetType, String targetCode) {
            this.businessCode = businessCode;
            this.oldObject = oldObject;
            this.newObject = newObject;
            this.targetType = targetType;
            this.targetCode = targetCode;
        }

        @Override
        public void run() {
            LogInfo logInfo = LogInfo.createLogInfo(businessCode, targetType, targetCode, oldObject, newObject, getUser().getUsername());
            if (logInfo.compliance) {

                if (logInfo.order == 1) {
                    dao.save(logInfo.logEntity);
                }

                dao.save(logInfo.logTableEntity);

                List<ObjectUtil.Different> propertiesList = logInfo.differenceColumns;
                for (ObjectUtil.Different map : propertiesList) {
                    LogValueEntity logValueEntity = LogValueEntity
                            .builder()
                            .logValueId(RandomStringUtil.getRandom(LENGTH, RandomStringUtil.Random.LETTER_UPPER))
                            .logTableId(logInfo.logTableEntity.getLogTableId())
                            .columnName(String.valueOf(map.getPropertyName()))
                            .columnType(String.valueOf(map.getPropertyType()))
                            .newValue(String.valueOf(map.getNewValue()))
                            .oldValue(String.valueOf(map.getOldValue()))
                            .build();
                    dao.save(logValueEntity);
                }
            }
        }
    }

    /**
     * 整理后的日志信息
     */
    private static final class LogInfo {
        private boolean compliance;
        private String tableName;
        private String dbName;
        private List<ObjectUtil.Different> differenceColumns;
        private LogMainEntity logEntity;
        private LogTableEntity logTableEntity;
        private Integer order;

        private LogInfo(String businessCode, String targetType, String targetCode, Object oldObject, Object newObject, String userId) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request;

            if (oldObject == null && newObject == null) {
                compliance = false;
                return;
            }
            if (requestAttributes == null) {
                compliance = false;
                return;
            }
            request = ((ServletRequestAttributes) requestAttributes).getRequest();
            logEntity = (LogMainEntity) request.getAttribute(LOG_MAIN_INDEX);
            if (ObjectUtil.isEmpty(logEntity)) {
                logEntity = LogMainEntity
                        .builder()
                        .businessCode(businessCode)
                        .createTime(new Date())
                        .userId(userId)
                        .targetType(targetType)
                        .targetCode(targetCode)
                        .logMainId(RandomStringUtil.getRandom(LENGTH, RandomStringUtil.Random.LETTER_UPPER))
                        .build();
            }
            order = (Integer) request.getAttribute(LOG_TABLE_ORDER);
            if (order == null) {
                order = 1;
            }

            Table table = oldObject == null ? newObject.getClass().getAnnotation(Table.class) : oldObject.getClass().getAnnotation(Table.class);

            if (ObjectUtil.compareClass(oldObject, newObject) && table != null) {
                compliance = true;
                tableName = table.name();
                dbName = table.catalog();
                try {
                    differenceColumns = ObjectUtil.getDifferenceProperties(oldObject, newObject);
                    if (differenceColumns != null && differenceColumns.size() > 0) {
                        OperationType operationType;
                        if (newObject == null) {
                            operationType = OperationType.Delete;
                        } else if (oldObject == null) {
                            operationType = OperationType.Create;
                        } else {
                            operationType = OperationType.Update;
                        }

                        logTableEntity = LogTableEntity
                                .builder()
                                .logMainId(logEntity.getLogMainId())
                                .logTableId(RandomStringUtil.getRandom(LENGTH, RandomStringUtil.Random.LETTER_UPPER))
                                .tableName(tableName)
                                .tableSchema(dbName)
                                .operationType(operationType.name())
                                .operationOrder(order)
                                .build();
                    }
                } catch (Exception ignored) {
                }
            } else {
                compliance = false;
            }
        }

        static LogInfo createLogInfo(String businessCode, String targetType, String targetCode, Object oldObject, Object newObject, String userId) {
            return new LogInfo(businessCode, targetType, targetCode, oldObject, newObject, userId);
        }
    }
}
