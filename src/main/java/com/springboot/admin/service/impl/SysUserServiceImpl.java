package com.springboot.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.admin.convert.SysUserConvert;
import com.springboot.admin.mapper.ext.SysUserExtMapper;
import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.dto.user.SysUserQueryDTO;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.user.SysUserVO;
import com.springboot.admin.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 * 提供用户相关业务方法，例如根据用户名获取用户信息
 * 返回 DTO 用于业务逻辑处理，Controller 层可再转换为 VO
 *
 * @author system
 * @since 2026-01-18
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserExtMapper, SysUser> implements ISysUserService {
    @Autowired
    private SysUserExtMapper sysUserExtMapper;
    @Autowired
    private  SysUserConvert sysUserConvert;


    @Override
    public PageResult<SysUserVO> pageUserList(SysUserQueryDTO query) {
        // 1️⃣ 创建 LambdaQueryWrapper 条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeleteFlag, 0); // 逻辑未删除的记录

        if (StringUtils.isNotBlank(query.getUsername())) {
            wrapper.like(SysUser::getUsername, query.getUsername());
        }
        if (StringUtils.isNotBlank(query.getEmail())) {
            wrapper.like(SysUser::getEmail, query.getEmail());
        }
        if (StringUtils.isNotBlank(query.getPhone())) {
            wrapper.like(SysUser::getPhone, query.getPhone());
        }
        if (query.getDeptId() != null) {
            wrapper.eq(SysUser::getDeptId, query.getDeptId());
        }

        // 2️⃣ 分页查询
        int page = query.getPage() > 0 ? query.getPage() : 1;
        int size = query.getSize() > 0 ? query.getSize() : 10;

        // MyBatis-Plus 内置分页插件，使用 page 对象
        Page<SysUser> pageInfo =
                new Page<>(page, size);

        page(pageInfo, wrapper); // 调用 ServiceImpl 自带 page 方法

        // 3️⃣ Entity -> VO
        List<SysUserVO> voList = sysUserConvert.toVOList(pageInfo.getRecords());

        // 4️⃣ 封装 PageResult
        PageResult<SysUserVO> result = new PageResult<>();
        result.setRecords(voList);
        result.setTotal(pageInfo.getTotal());
        result.setPage(pageInfo.getCurrent());
        result.setSize(pageInfo.getSize());

        return result;
    }


    @Override
    public Long addUser(SysUserDTO sysUserDTO) {
        SysUser user = sysUserConvert.dtoToEntity(sysUserDTO);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        save(user);
        return user.getId();
    }

    @Override
    public Long updateUser(SysUserDTO sysUserDTO) {
        SysUser user = sysUserConvert.dtoToEntity(sysUserDTO);
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
        return user.getId();
    }
    /**
     * 单个删除（逻辑/物理）
     *
     * @param id 用户ID
     * @param logicalDelete 是否逻辑删除
     * @return 删除的记录数
     */
    public int deleteUser(Long id, boolean logicalDelete) {
        if (id == null) {
            return 0;
        }
        if (logicalDelete) {
            // 逻辑删除
            SysUser user = new SysUser();
            user.setId(id);
            user.setDeleteFlag(1);
            user.setUpdateTime(LocalDateTime.now());
            return updateById(user)?1:0;
        } else {
            // 物理删除
            return removeById(id) ? 1 : 0;
        }
    }

    /**
     * 批量删除（逻辑/物理）
     *
     * @param ids 用户ID集合
     * @param logicalDelete 是否逻辑删除
     * @return 删除的记录数
     */
    public int deleteUsers(Iterable<Long> ids, boolean logicalDelete) {
        if (ids == null) {
            return 0;
        }

        // 转成 List，便于操作和获取大小
        List<Long> idList = new ArrayList<>();
        ids.forEach(idList::add);

        if (idList.isEmpty()) {
            return 0;
        }

        if (logicalDelete) {
            // 批量逻辑删除：一次性更新 delete_flag = 1
            LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(SysUser::getId, idList)
                    .set(SysUser::getDeleteFlag, 1)
                    .set(SysUser::getUpdateTime, LocalDateTime.now());

            // update 返回 boolean，改用 update(wrapper) 可返回影响行数
            return this.baseMapper.update(null, updateWrapper);
        } else {
            // 批量物理删除：一次性删除
            return this.baseMapper.deleteBatchIds(idList);
        }
    }


    /**
     * 根据用户名查询用户信息
     * <p>
     * 1. 使用 MyBatis-Plus LambdaQueryWrapper 进行条件查询
     * 2. 返回 DTO，包含业务处理所需完整字段
     * 3. 如果用户不存在，返回 null
     *
     * @param username 用户名
     * @return SysUserDTO 用户信息 DTO，如果不存在返回 null
     */
    @Override
    public SysUserDTO getUserDTOByUsername(String username) {
        // 1️⃣ 防御性校验
        if (StringUtils.isBlank(username)) {
            return null;
        }

        // 2️⃣ 构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleteFlag, 0);

        // 3️⃣ 查询实体
        SysUser sysUser = this.getOne(queryWrapper);
        if (sysUser == null) {
            return null;
        }

        // 4️⃣ Entity → DTO（统一交给 Convert）
        return sysUserConvert.entityToDTO(sysUser);
    }

    @Override
    public SysUserDTO getSysUserDtoByUserId(long useId) {
        // 1️⃣  查询实体
        SysUser sysUser = this.getById(useId);
        // 2️⃣ Entity → DTO（统一交给 Convert）
        return sysUserConvert.entityToDTO(sysUser);
    }

    @Override
    public Long getUserOrgId(Long userId) {
        SysUser user = this.getById(userId);
        return user != null ? user.getOrgId() : null;
    }

    @Override
    public Long getUserDeptId(Long userId) {
        SysUser user = this.getById(userId);
        return user != null ? user.getDeptId() : null;
    }
}

