package com.springboot.admin.service.impl;

import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.vo.role.SysRoleVO;
import com.springboot.admin.service.ISysRoleService;
import com.springboot.admin.service.ISysUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.security.core.userdetails.User.withUsername;

@Service
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final ISysUserService iSysUserService;
    private final ISysRoleService iSysRoleService;

    public CustomReactiveUserDetailsService(ISysUserService userService, ISysRoleService roleService) {
        this.iSysUserService = userService;
        this.iSysRoleService = roleService;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return iSysUserService.getUserByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("用户不存在")))
                .flatMap(user ->
                        iSysRoleService.listRolesByUserId(user.getId())
                                .map(SysRoleVO::getRoleCode)
                                .map(roleCode -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + roleCode))
                                .collectList()
                                .map(authorities -> buildUserDetails(user, authorities))
                );
    }

    private UserDetails buildUserDetails(SysUserDTO user, List<GrantedAuthority> authorities) {
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
