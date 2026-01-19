package com.springboot.admin.convert;

import com.springboot.admin.model.entity.sys.SysRole;
import com.springboot.admin.model.vo.role.SysRoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

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
}
