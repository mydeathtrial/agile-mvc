package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by 佟盟
 */
@Entity
@Table(name = "sys_login", catalog = "agile_db")
@Remark("")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysLoginEntity implements Serializable, Cloneable {

    //序列
    private static final long serialVersionUID = 1L;
    @Remark("主键")
    private String sysLoginId;
    @Remark("账号主键")
    private String sysUserId;
    @Remark("登陆时间")
    private Date loginTime;
    @Remark("退出时间")
    private Date logoutTime;
    @Remark("登陆IP地址")
    private String loginIp;
    @Remark("口令")
    private String token;

    //无参构造器
    public SysLoginEntity() {
    }

    //有参构造器
    public SysLoginEntity(String sysLoginId, String sysUserId, Date loginTime, Date logoutTime, String loginIp, String token) {
        this.sysLoginId = sysLoginId;
        this.sysUserId = sysUserId;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.loginIp = loginIp;
        this.token = token;
    }

    @Id
    @Column(name = "sys_login_id", nullable = false)
    public String getSysLoginId() {
        return sysLoginId;
    }

    public void setSysLoginId(String sysLoginId) {
        this.sysLoginId = sysLoginId;
    }

    @Basic
    @Column(name = "sys_user_id")
    public String getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(String sysUserId) {
        this.sysUserId = sysUserId;
    }

    @Basic
    @Column(name = "login_time")
    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    @Basic
    @Column(name = "logout_time")
    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    @Basic
    @Column(name = "login_ip")
    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    @Basic
    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SysLoginEntity)) {
            return false;
        }
        SysLoginEntity that = (SysLoginEntity) object;
        return Objects.equals(getSysLoginId(), that.getSysLoginId()) &&
                Objects.equals(getSysUserId(), that.getSysUserId()) &&
                Objects.equals(getLoginTime(), that.getLoginTime()) &&
                Objects.equals(getLogoutTime(), that.getLogoutTime()) &&
                Objects.equals(getLoginIp(), that.getLoginIp()) &&
                Objects.equals(getToken(), that.getToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysLoginId(), getSysUserId(), getLoginTime(), getLogoutTime(), getLoginIp(), getToken());
    }

    @Override
    public String toString() {
        return "SysLoginEntity{" +
                "sysLoginId='" + sysLoginId + '\'' +
                ",sysUserId='" + sysUserId + '\'' +
                ",loginTime=" + loginTime +
                ",logoutTime=" + logoutTime +
                ",loginIp='" + loginIp + '\'' +
                ",token='" + token + '\'' +
                '}';
    }

    private SysLoginEntity(Builder builder) {
        this.sysLoginId = builder.sysLoginId;
        this.sysUserId = builder.sysUserId;
        this.loginTime = builder.loginTime;
        this.logoutTime = builder.logoutTime;
        this.loginIp = builder.loginIp;
        this.token = builder.token;
    }

    public static class Builder {
        private String sysLoginId;
        private String sysUserId;
        private Date loginTime;
        private Date logoutTime;
        private String loginIp;
        private String token;

        public Builder setSysLoginId(String sysLoginId) {
            this.sysLoginId = sysLoginId;
            return this;
        }

        public Builder setSysUserId(String sysUserId) {
            this.sysUserId = sysUserId;
            return this;
        }

        public Builder setLoginTime(Date loginTime) {
            this.loginTime = loginTime;
            return this;
        }

        public Builder setLogoutTime(Date logoutTime) {
            this.logoutTime = logoutTime;
            return this;
        }

        public Builder setLoginIp(String loginIp) {
            this.loginIp = loginIp;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public SysLoginEntity build() {
            return new SysLoginEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SysLoginEntity clone() {
        try {
            return (SysLoginEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }

    }
}
