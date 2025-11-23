package com.springboot.admin.service.impl;

import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.repository.SysUserRepository;
import com.springboot.admin.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户表 Service 实现类
 */
@Service
@Slf4j
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private  SysUserRepository userRepository;

    @Override
    public Mono<SysUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Mono<SysUser> addUser(SysUser user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<SysUser> updateUser(SysUser user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Flux<SysUser> listUsers() {
        return userRepository.findAll();
    }
}

