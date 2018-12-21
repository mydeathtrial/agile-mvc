package com.agile.common.security;

import com.agile.common.exception.NoSuchIDException;
import com.agile.common.mvc.model.dao.Dao;
import com.agile.common.util.DateUtil;
import com.agile.common.util.ObjectUtil;
import com.agile.mvc.entity.SysAuthoritiesEntity;
import com.agile.mvc.entity.SysLoginEntity;
import com.agile.mvc.entity.SysUsersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 佟盟 on 2017/1/13
 * 从数据库中读入用户的密码，角色信息，是否锁定，账号是否过期等
 */
@Service
public class SecurityUserDetailsService implements UserDetailsService {
    private final Dao dao;

    @Autowired
    public SecurityUserDetailsService(Dao dao) {
        this.dao = dao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUsersEntity user = null;
        user = dao.findOne(SysUsersEntity.builder().setSaltKey(username).build());
        if (ObjectUtil.isEmpty(user)) {
            throw new UsernameNotFoundException(null);
        }
        String sql = "SELECT\n" +
                "\tsys_authorities.SYS_AUTHORITY_ID,\n" +
                "\tsys_authorities.MARK,\n" +
                "\tsys_authorities.NAME,\n" +
                "\tsys_authorities.DESC,\n" +
                "\tsys_authorities.`ENABLE`\n" +
                "FROM\n" +
                "\tsys_users\n" +
                "\tLEFT JOIN sys_bt_users_roles ON sys_users.SYS_USERS_ID = sys_bt_users_roles.USER_ID\n" +
                "\tLEFT JOIN sys_roles ON sys_roles.SYS_ROLES_ID = sys_bt_users_roles.ROLE_ID\n" +
                "\tLEFT JOIN sys_bt_roles_authorities ON sys_roles.SYS_ROLES_ID = sys_bt_roles_authorities.ROLE_ID\n" +
                "\tLEFT JOIN sys_authorities ON sys_authorities.SYS_AUTHORITY_ID = sys_bt_roles_authorities.AUTHORITY_ID \n" +
                "WHERE\n" +
                "\tsys_users.SALT_KEY = '%s'";
        sql = String.format(sql, username);
        List<SysAuthoritiesEntity> sysAuthoritiesEntities = dao.findAll(sql, SysAuthoritiesEntity.class);
        if (ObjectUtil.isEmpty(sysAuthoritiesEntities)) {
            return new SecurityUser(user, null);
        }
        return new SecurityUser(user, sysAuthoritiesEntities);
    }

    @Transactional
    public void addLoginInfo(SysLoginEntity user) {
        dao.save(user);
    }

    @Transactional
    public void updateLoginInfo(String token) throws NoSuchIDException {
        SysLoginEntity sysLoginEntity = dao.findOne(SysLoginEntity.builder().setToken(token).build());
        sysLoginEntity.setLogoutTime(DateUtil.getCurrentDate());
        dao.update(sysLoginEntity);
    }

    @Transactional
    public void updateLoginInfo(String oldToken, String newToken) throws NoSuchIDException {
        SysLoginEntity sysLoginEntity = dao.findOne(SysLoginEntity.builder().setToken(oldToken).build());
        sysLoginEntity.setToken(newToken);
        dao.update(sysLoginEntity);
    }

    public void validate(UserDetails securityUser) throws AuthenticationException {
        if (securityUser == null) {
            throw new UsernameNotFoundException(null);
        } else if (!securityUser.isEnabled()) {
            throw new DisabledException(null);
        } else if (!securityUser.isAccountNonExpired()) {
            throw new AccountExpiredException(null);
        } else if (!securityUser.isAccountNonLocked()) {
            throw new LockedException(null);
        } else if (!securityUser.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(null);
        }
    }
}
