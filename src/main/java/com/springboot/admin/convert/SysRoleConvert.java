package com.springboot.admin.convert;

import com.springboot.admin.model.dto.role.SysRoleDTO;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.vo.role.SysRoleVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 角色对象转换器
 * <p>
 * 使用 MapStruct 自动生成对象之间的转换代码。
 * 主要用于 SysRoleDTO、SysRole 实体、SysRoleVO 之间的转换。
 *
 * 设计目的：
 * - 保持分层清晰：DTO 用于接收前端数据，Entity 对应数据库表，VO 用于返回前端展示。
 * - 避免手写重复的转换代码，提高开发效率。
 * - 保证对象之间字段映射的一致性。
 */
@Mapper(componentModel = "spring")
public interface SysRoleConvert {

    /**
     * DTO -> Entity
     * <p>
     * 用于新增或更新角色时，将前端传入的 SysRoleDTO 转换为数据库实体 SysRole。
     * 典型场景：Service 层调用 repository 保存数据。
     *
     * @param dto 前端传入的角色数据
     * @return SysRole 数据库实体对象
     */
    SysRole toEntity(SysRoleDTO dto);

    /**
     * Entity -> VO
     * <p>
     * 用于查询角色信息时，将数据库实体 SysRole 转换为前端展示对象 SysRoleVO。
     * 典型场景：Controller 层返回给前端。
     *
     * @param entity 数据库查询得到的角色实体
     * @return SysRoleVO 前端展示对象
     */
    SysRoleVO toVO(SysRole entity);

    /**
     * List<Entity> -> List<VO>
     * <p>
     * 用于批量查询角色列表时，将数据库实体集合转换为前端展示对象集合。
     * 典型场景：返回角色列表接口。
     *
     * @param entities 数据库查询得到的角色实体集合
     * @return List<SysRoleVO> 前端展示对象集合
     */
    List<SysRoleVO> toVOList(List<SysRole> entities);
}
