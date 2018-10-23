package com.agile.common.security;

import com.agile.common.util.DateUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.mvc.entity.SysAuthoritiesEntity;
import com.agile.mvc.entity.SysUsersEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by 佟盟 on 2017/9/26
 */
public class SecurityUser extends SysUsersEntity implements UserDetails {
    private static final long serialVersionUID = 1L;

    private List<SysAuthoritiesEntity> sysAuthoritiesEntities;

    SecurityUser(SysUsersEntity user, List<SysAuthoritiesEntity> sysAuthoritiesEntities) {
        if(user != null)
        {
            ObjectUtil.copyProperties(user,this);
            this.sysAuthoritiesEntities = sysAuthoritiesEntities;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if(sysAuthoritiesEntities != null)
        {
            for (SysAuthoritiesEntity role : sysAuthoritiesEntities) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getName());
                authorities.add(authority);
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.getSaltValue();
    }

    @Override
    public String getUsername() {
        return this.getSaltKey();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !(this.getExpiredTime() != null && this.getExpiredTime().before(DateUtil.getCurrentDate()));
    }

    @Override
    public boolean isAccountNonLocked() {
        return !(this.getIsLocked() != null && this.getIsLocked());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.getEnabled() != null && this.getEnabled();
    }
}
