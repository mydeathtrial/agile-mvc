package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Created by 佟盟
 */
@Entity
@Table(name = "sys_bt_authorities_resources",  catalog = "agile_db")
@Remark("[系统管理]权限资源表")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysBtAuthoritiesResourcesEntity implements Serializable,Cloneable {

    //序列
    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String sysBtAuthoritiesResourcesId;
    @Remark("资源唯一标识")
    private String resourceId;
    @Remark("权限唯一标识")
    private String authorityId;

    //无参构造器
    public SysBtAuthoritiesResourcesEntity(){}

    //有参构造器
    public SysBtAuthoritiesResourcesEntity(String sysBtAuthoritiesResourcesId,String resourceId,String authorityId){
        this.sysBtAuthoritiesResourcesId = sysBtAuthoritiesResourcesId;
        this.resourceId = resourceId;
        this.authorityId = authorityId;
    }

    @Id
    @Column(name = "sys_bt_authorities_resources_id" , nullable = false )
    public String getSysBtAuthoritiesResourcesId() {
        return sysBtAuthoritiesResourcesId;
    }

    public void setSysBtAuthoritiesResourcesId(String sysBtAuthoritiesResourcesId) {
        this.sysBtAuthoritiesResourcesId = sysBtAuthoritiesResourcesId;
    }

    @Basic
    @Column(name = "resource_id" , nullable = false )
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Basic
    @Column(name = "authority_id" , nullable = false )
    public String getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof SysBtAuthoritiesResourcesEntity)) return false;
        SysBtAuthoritiesResourcesEntity that = (SysBtAuthoritiesResourcesEntity) object;
        return Objects.equals(getSysBtAuthoritiesResourcesId(), that.getSysBtAuthoritiesResourcesId()) &&
            Objects.equals(getResourceId(), that.getResourceId()) &&
            Objects.equals(getAuthorityId(), that.getAuthorityId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSysBtAuthoritiesResourcesId(), getResourceId(), getAuthorityId());
    }

    @Override
    public String toString() {
        return "SysBtAuthoritiesResourcesEntity{" +
        "sysBtAuthoritiesResourcesId='" + sysBtAuthoritiesResourcesId + '\'' +
        ",resourceId='" + resourceId + '\'' +
        ",authorityId='" + authorityId + '\'' +
        '}';
    }

    private SysBtAuthoritiesResourcesEntity(Builder builder){
        this.sysBtAuthoritiesResourcesId = builder.sysBtAuthoritiesResourcesId;
        this.resourceId = builder.resourceId;
        this.authorityId = builder.authorityId;
    }

    public static class Builder{
        private String sysBtAuthoritiesResourcesId;
        private String resourceId;
        private String authorityId;

        public Builder setSysBtAuthoritiesResourcesId(String sysBtAuthoritiesResourcesId) {
            this.sysBtAuthoritiesResourcesId = sysBtAuthoritiesResourcesId;
            return this;
        }
        public Builder setResourceId(String resourceId) {
            this.resourceId = resourceId;
            return this;
        }
        public Builder setAuthorityId(String authorityId) {
            this.authorityId = authorityId;
            return this;
        }
        public SysBtAuthoritiesResourcesEntity build(){
            return new SysBtAuthoritiesResourcesEntity(this);
        }
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
