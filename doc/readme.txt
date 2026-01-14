用户 sys_user
   |
   |  (多对多)
   v
角色 sys_role
   |
   |  (多对多)
   +--------> 菜单 sys_menu
   |
   |  (多对多)
   +--------> 权限点 sys_permission

菜单 sys_menu
   |
   | (多对多)
   v
权限点 sys_permission


sys_user ──< sys_user_role >── sys_role ──< sys_role_menu >── sys_menu
                                      │
                                      └──< sys_role_permission >── sys_permission
sys_menu ──< sys_menu_permission >── sys_permission


    用户登录 → 获取用户角色。

    根据角色 → 查询菜单集合（决定前端路由）。

    根据角色 → 查询权限集合（决定接口能否调用）。

    根据菜单 → 查询菜单下的权限点（决定页面按钮是否显示）。

这样就实现了 前端可见性控制 + 后端接口校验 的完整闭环。


-n Testing 172.30.16.26:8848 (nacos_26_8848) ...
✅ Connected
-n Testing 172.30.17.18:2883 (鹊桥/cms_dev数据库_18_2883) ...
❌ Failed
-n Testing 172.30.17.50:4000 (tidb_50_4000) ...
❌ Failed
-n Testing 172.30.17.56:9200 (es_56_9200) ...
❌ Failed
-n Testing 172.30.17.57:9200 (es_57_9200) ...
❌ Failed
-n Testing 172.30.17.58:9200 (es_58_9200) ...
❌ Failed
-n Testing 172.30.17.59:9092 (kakfa_bigData_59_9092) ...
❌ Failed
-n Testing 172.30.17.60:9092 (kakfa_bigData_60_9092) ...
❌ Failed
-n Testing 172.30.17.61:9092 (kakfa_bigData_61_9092) ...
❌ Failed
-n Testing 172.30.17.62:7001 (redis_62_7001) ...
❌ Failed
-n Testing 172.30.17.63:7001 (redis_63_7001) ...
❌ Failed
-n Testing 172.30.17.64:7001 (redis_64_7001) ...
❌ Failed
-n Testing 172.30.17.62:7002 (redis_62_7002) ...
❌ Failed
-n Testing 172.30.17.63:7002 (redis_63_7002) ...
❌ Failed
-n Testing 172.30.17.64:7002 (redis_64_7002) ...
❌ Failed
-n Testing 172.30.50.14:9030 (starrocks_14_9030) ...
❌ Failed
-n Testing 43.198.68.123:9092 (kakfa_cw_123_9092) ...
✅ Connected
-n Testing 43.198.68.123:9092 (kakfa_cw_123_9093) ...
✅ Connected
-n Testing 43.198.68.123:9092 (kakfa_cw_123_9094) ...
✅ Connected
-n Testing 16.163.245.213:9092 (kakfa_cw_232_9092) ...
❌ Failed
-n Testing 16.163.245.213:9093 (kakfa_cw_232_9093) ...
❌ Failed
-n Testing 16.163.245.213:9094 (kakfa_cw_232_9094) ...
❌ Failed
    /**
     * ============================
     * 获取用户信息
     * ============================
     */
    @Override
    public Mono<UserInfoDTO> getUserInfoByToken(String token) {

        return jwtUtil.parseToken(token)
                .flatMap(claims -> {

                    String username = claims.getSubject();

                    return sysUserService.getUserByUsername(username)
                            .switchIfEmpty(Mono.error(new BusinessException("0100020","用户不存在")))
                            .flatMap(user -> {

                                String cacheKey = "user:info:" + user.getId();

                                // ============================
                                // 【优化新增】用户信息缓存
                                // ============================
                                return redisService.get(cacheKey)
                                        .switchIfEmpty(
                                                Mono.zip(
                                                                sysRoleService.listRolesByUserId(user.getId()).collectList(),
                                                                sysPermissionService.listPermissionsByUserId(user.getId()).collectList(),
                                                                sysMenuService.getMenuTreeByUserId(user.getId()).collectList()
                                                        ).map(tuple -> buildUserInfoDTO(user, tuple.getT1(), tuple.getT2(), tuple.getT3()))
                                                        .flatMap(dto -> redisService.set(cacheKey, dto, 30, TimeUnit.MINUTES).thenReturn(dto))
                                        );
                            });
                })
                // 【原逻辑调整】保留原始异常
                .onErrorMap(e -> new BusinessException("0100021","获取用户信息失败", e));
    }