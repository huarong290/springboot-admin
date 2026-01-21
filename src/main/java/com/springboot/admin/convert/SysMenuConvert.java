package com.springboot.admin.convert;

import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.model.vo.menu.MetaVO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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

    /**
     * 构建 Meta 信息（关键）
     */
    @AfterMapping
    default void buildMeta(SysMenu entity, @MappingTarget SysMenuTreeVO vo) {
        MetaVO meta = new MetaVO();
        meta.setTitle(entity.getMenuName());
        meta.setIcon(entity.getMenuIcon());
        meta.setKeepAlive(true); // 默认缓存
        meta.setHidden(entity.getMenuVisible() != null && entity.getMenuVisible() == 0);
        meta.setAffix(false); // 可后续扩展

        vo.setMeta(meta);
    }
}

