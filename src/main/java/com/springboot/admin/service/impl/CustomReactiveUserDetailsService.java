package com.springboot.admin.service.impl;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.service.ISysRoleService;
import com.springboot.admin.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.security.core.userdetails.User.withUsername;

import java.util.List;

@Service
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    private ISysUserService iSysUserService;
    @Autowired
    private  ISysRoleService iSysRoleService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return iSysUserService.getUserByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("用户不存在")))
                .flatMap(user ->
                        iSysRoleService.listRolesByUserId(user.getId())
                                .map(SysRole::getRoleCode)
                                .map(roleCode -> (GrantedAuthority) new SimpleGrantedAuthority(roleCode))
                                .collectList()
                                .map(authorities -> buildUserDetails(user, authorities))
                );
    }

    private UserDetails buildUserDetails(SysUser user, List<GrantedAuthority> authorities) {
        return withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(user.getEnabled() != null && user.getEnabled() == 0)
                .build();
    }

}
