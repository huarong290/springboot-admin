package com.springboot.admin.convert;

import com.springboot.admin.model.entity.sys.SysPermission;
import com.springboot.admin.model.vo.permission.SysPermissionVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SysPermissionConvert {

    SysPermissionConvert INSTANCE = Mappers.getMapper(SysPermissionConvert.class);

    /**
     * Entity 转 VO
     */
    SysPermissionVO toVo(SysPermission entity);

    /**
     * 批量转换
     */
    List<SysPermissionVO> toVoList(List<SysPermission> entities);
}

