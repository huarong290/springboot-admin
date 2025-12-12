package com.springboot.admin.convert;

import com.springboot.admin.model.dto.userrole.SysUserRoleDTO;
import com.springboot.admin.model.entity.sys.SysUserRole;
import com.springboot.admin.model.vo.userrole.SysUserRoleVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 用户角色对象转换器
 * <p>
 * 使用 MapStruct 自动生成对象之间的转换代码。
 * 主要用于 SysUserRoleDTO、SysUserRole 实体、SysUserRoleVO 之间的转换。
 *
 * 设计目的：
 * - 保持分层清晰：DTO 用于接收前端数据，Entity 对应数据库表，VO 用于返回前端展示。
 * - 避免手写重复的转换代码，提高开发效率。
 * - 保证对象之间字段映射的一致性，减少人为错误。
 *
 * 使用场景：
 * - Controller 层接收前端传入的 DTO，调用 Service 层时转换为 Entity。
 * - Service 层查询数据库得到 Entity，返回给 Controller 时转换为 VO。
 */
@Mapper(componentModel = "spring")
public interface SysUserRoleConvert {

    /**
     * DTO -> Entity
     *
     * @param dto 前端传入的用户角色数据对象
     * @return SysUserRole 数据库实体对象
     */
    SysUserRole toEntity(SysUserRoleDTO dto);

    /**
     * Entity -> VO
     *
     * @param entity 数据库查询得到的用户角色实体
     * @return SysUserRoleVO 前端展示对象
     */
    SysUserRoleVO toVO(SysUserRole entity);

    /**
     * List<Entity> -> List<VO>
     *
     * @param entities 数据库查询得到的用户角色实体集合
     * @return List<SysUserRoleVO> 前端展示对象集合
     */
    List<SysUserRoleVO> toVOList(List<SysUserRole> entities);
}

