package com.agile.mvc.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Basic;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.agile.common.annotation.Remark;
import java.util.Date;
import javax.persistence.Id;

/**

 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_login")
public class SysLoginEntity implements Serializable, Cloneable {

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

    @Column(name = "sys_login_id", nullable = false)
    @Id
    public String getSysLoginId() {
        return sysLoginId;
    }

    @Basic
    @Column(name = "sys_user_id")
    public String getSysUserId() {
        return sysUserId;
    }

    @Basic
    @Column(name = "login_time")
    public Date getLoginTime() {
        return loginTime;
    }

    @Column(name = "logout_time")
    @Basic
    public Date getLogoutTime() {
        return logoutTime;
    }

    @Basic
    @Column(name = "login_ip")
    public String getLoginIp() {
        return loginIp;
    }

    @Column(name = "token")
    @Basic
    public String getToken() {
        return token;
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
