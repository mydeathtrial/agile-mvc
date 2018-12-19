package com.agile.common.mvc.service;

import java.util.Date;

/**
 * Created by 佟盟 on 2018/7/17
 */
interface TaskDetail {

    String getSysTaskId();

    void setSysTaskId(String sysTaskId);

    String getName();

    void setName(String name);

    Boolean getState();

    void setState(Boolean state);

    String getCron();

    void setCron(String cron);

    Boolean getSync();

    void setSync(Boolean sync);

    Date getUpdateTime();

    void setUpdateTime(Date updateTime);

    Date getCreateTime();

    void setCreateTime(Date createTime);
}
