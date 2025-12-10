package com.springboot.admin.convert;

import com.springboot.admin.model.dto.permission.SysPermissionDTO;
import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 权限对象转换器
 * <p>
 * 使用 MapStruct 自动生成对象之间的转换代码。
 * 主要用于 SysPermissionDTO、SysPermission 实体、SysPermissionVO 之间的转换。
 *
 * 设计目的：
 * - 保持分层清晰：DTO 用于接收前端数据，Entity 对应数据库表，VO 用于返回前端展示。
 * - 避免手写重复的转换代码，提高开发效率。
 * - 保证对象之间字段映射的一致性。
 */
@Mapper(componentModel = "spring")
public interface SysPermissionConvert {

    /**
     * DTO -> Entity
     * <p>
     * 用于新增或更新权限时，将前端传入的 SysPermissionDTO 转换为数据库实体 SysPermission。
     * 典型场景：Service 层调用 repository 保存数据。
     *
     * @param dto 前端传入的权限数据
     * @return SysPermission 数据库实体对象
     */
    SysPermission toEntity(SysPermissionDTO dto);

    /**
     * Entity -> VO
     * <p>
     * 用于查询权限信息时，将数据库实体 SysPermission 转换为前端展示对象 SysPermissionVO。
     * 典型场景：Controller 层返回给前端。
     *
     * @param entity 数据库查询得到的权限实体
     * @return SysPermissionVO 前端展示对象
     */
    SysPermissionVO toVO(SysPermission entity);

    /**
     * List<Entity> -> List<VO>
     * <p>
     * 用于批量查询权限列表时，将数据库实体集合转换为前端展示对象集合。
     * 典型场景：返回权限列表接口。
     *
     * @param entities 数据库查询得到的权限实体集合
     * @return List<SysPermissionVO> 前端展示对象集合
     */
    List<SysPermissionVO> toVOList(List<SysPermission> entities);
}

