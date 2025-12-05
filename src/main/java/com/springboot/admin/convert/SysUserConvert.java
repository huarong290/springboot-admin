package com.springboot.admin.convert;

import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.model.vo.user.UserVO;
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

    // Entity -> VO
    UserVO toVO(SysUser entity);


    // List<Entity> -> List<VO>
    List<UserVO> toVOList(List<SysUser> entities);
}
