package com.springboot.admin.convert;

import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.model.vo.user.SysUserVO;
import org.mapstruct.Mapper;

import java.util.List;


/**
 * 用户对象转换器
 * 用于 Entity、DTO、VO 之间的转换
 */
@Mapper(componentModel = "spring")
public interface SysUserConvert {

    // DTO -> Entity
    SysUser toEntity(SysUserDTO dto);

    // Entity -> DTO
    SysUserDTO toDTO(SysUser sysUser);

    // Entity -> VO
    SysUserVO toVO(SysUser entity);


    // List<Entity> -> List<VO>
    List<SysUserVO> toVOList(List<SysUser> entities);
}
