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
import javax.persistence.Id;

/**
 * 描述：[系统管理]权限资源表
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_bt_authorities_resources")
@Remark("[系统管理]权限资源表")
public class SysBtAuthoritiesResourcesEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysBtAuthoritiesResourcesId;
    @Remark("资源唯一标识")
    private String resourceId;
    @Remark("权限唯一标识")
    private String authorityId;

    @Column(name = "SYS_BT_AUTHORITIES_RESOURCES_ID", nullable = false)
    @Id
    public String getSysBtAuthoritiesResourcesId() {
        return sysBtAuthoritiesResourcesId;
    }

    @Basic
    @Column(name = "RESOURCE_ID", nullable = false)
    public String getResourceId() {
        return resourceId;
    }

    @Basic
    @Column(name = "AUTHORITY_ID", nullable = false)
    public String getAuthorityId() {
        return authorityId;
    }


    @Override
    public SysBtAuthoritiesResourcesEntity clone() {
        try {
            return (SysBtAuthoritiesResourcesEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
