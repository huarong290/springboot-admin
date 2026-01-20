package com.springboot.admin.convert;

import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SysMenuConvert {

    SysMenuConvert INSTANCE = Mappers.getMapper(SysMenuConvert.class);

    /**
     * Entity 转 VO
     */
    @Mapping(source = "menuParentId", target = "menuParentId") // 注意 source 和 target
    SysMenuTreeVO toVo(SysMenu entity);

    /**
     * 批量转换
     */
    List<SysMenuTreeVO> toVoList(List<SysMenu> entities);
}

