package com.springboot.admin.convert;

import com.springboot.admin.model.dto.role.SysRoleDTO;
import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.vo.role.SysRoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
/**
 * SysRole 转换器
 * <p>
 * MapStruct 会根据方法签名自动生成实现
 */
@Mapper(componentModel = "spring")
public interface SysRoleConvert {

    SysRoleConvert INSTANCE = Mappers.getMapper(SysRoleConvert.class);

    /**
     * Entity 转 VO
     */
    SysRoleVO toVo(SysRole entity);

    /**
     * 批量转换
     */
    List<SysRoleVO> toVoList(List<SysRole> entities);

    /**
     * DTO 转 Entity
     */
    SysRole dtoToEntity(SysRoleDTO dto);

    /**
     * 批量 DTO → Entity
     */
    List<SysRole> dtoToEntityList(List<SysRoleDTO> dtos);

    /**
     * Entity 转 DTO
     */
    SysRoleDTO entityToDto(SysRole entity);

    /**
     * 批量 Entity → DTO
     */
    List<SysRoleDTO> entityToDtoList(List<SysRole> entities);
}
