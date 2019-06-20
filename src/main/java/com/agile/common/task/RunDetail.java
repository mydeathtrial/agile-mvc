package com.agile.common.task;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author 佟盟
 * 日期 2019/5/13 14:44
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Builder
@Data
public class RunDetail {
    private Long taskCode;
    private boolean ending;
    private Date startTime;
    private Date endTime;
    @Builder.Default
    private StringBuilder log = new StringBuilder();

    void addLog(String log) {
        this.log.append(log).append("\n");
    }
}
