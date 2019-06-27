package com.agile.common.log;

import com.agile.common.annotation.NotAPI;
import com.agile.common.mvc.service.MainService;
import com.agile.common.util.IdUtil;
import com.agile.mvc.entity.LogMainEntity;
import com.agile.mvc.entity.LogTableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * 日志服务
 *
 * @author mydeathtrial on 2017/3/21
 */
@Service
@NotAPI
public class BusinessLogService extends MainService {
    /**
     * 业务日志标识
     */
    private ThreadLocal<BusinessLogDetail> currentBusinessLogCode = new ThreadLocal<>();

    /**
     * 当前线程主日志执行信息
     */
    @Data
    @AllArgsConstructor
    private static class BusinessLogDetail {
        /**
         * 主日志记录主键
         */
        private long mainLogId;
        /**
         * 第几次操作
         */
        private int step;

        int getStep() {
            return this.step++;
        }
    }

    public void initCurrentBusinessLogCode() {
        this.currentBusinessLogCode.set(new BusinessLogDetail(IdUtil.generatorId(), 0));
    }

    private Long getCurrentBusinessLogCode() {
        BusinessLogDetail detail = currentBusinessLogCode.get();
        if (detail == null) {
            return null;
        }
        return detail.getMainLogId();
    }

    private int getStep() {
        return currentBusinessLogCode.get().getStep();
    }

    public void clear() {
        currentBusinessLogCode.remove();
    }

    /**
     * 记录操作日志
     *
     * @param serviceExecutionInfo api执行信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void logging(ServiceExecutionInfo serviceExecutionInfo) {
        String apiName = serviceExecutionInfo.getMethod().toGenericString();

        String sql = "SELECT\n" +
                "sys_resources.SYS_RESOURCES_ID\n" +
                "FROM\n" +
                "sys_resources ,\n" +
                "sys_api\n" +
                "WHERE\n" +
                "sys_resources.RESOURCE_TYPE = '1000' AND\n" +
                "sys_resources.RESOURCE_ID = sys_api.sys_api_id AND\n" +
                "sys_resources.`ENABLE` = 1 AND\n" +
                "sys_resources.LOG_ENABLE = 1 AND\n" +
                "sys_api.`name` = ?";

        Object resourcesId = dao.findParameter(sql, apiName);

        if (resourcesId == null) {
            return;
        }

        initCurrentBusinessLogCode();
        LogMainEntity logMain = LogMainEntity.builder()
                .logMainId(getCurrentBusinessLogCode())
                .sysResourcesId(Long.parseLong(resourcesId.toString()))
                .inParam(serviceExecutionInfo.getInParamToJson())
                .outParam(serviceExecutionInfo.getOutParamToJson())
                .ip(serviceExecutionInfo.getIp())
                .status(serviceExecutionInfo.isStatus())
                .userAccountNumber(serviceExecutionInfo.getUserDetails() == null ? "anonymous" : serviceExecutionInfo.getUserDetails().getUsername())
                .createTime(serviceExecutionInfo.getExecutionDate())
                .timeConsuming(serviceExecutionInfo.getTimeConsuming())
                .build();
        dao.save(logMain);
    }

    public boolean needPrintBusinessLog() {
        Long mainLogId = getCurrentBusinessLogCode();
        return mainLogId != null;
    }

    /**
     * 记录操纵sql日志
     *
     * @param sql sql语句
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void printBusinessLog(String sql) {
        Long mainLogId = getCurrentBusinessLogCode();
        if (mainLogId == null) {
            return;
        }
        LogTableEntity logTable = LogTableEntity.builder().logMainId(mainLogId)
                .logTableId(IdUtil.generatorId())
                .sql(sql)
                .operationOrder(getStep())
                .build();
        dao.save(logTable);
    }
}
