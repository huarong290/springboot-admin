package com.springboot.admin.convert;

import com.springboot.admin.model.dto.menu.SysMenuDTO;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.model.vo.menu.MetaVO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.menu.SysMenuVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SysMenuConvert {
    /**
     * MapStruct 实例
     */
    SysMenuConvert INSTANCE = Mappers.getMapper(SysMenuConvert.class);

    /* ====================== DTO → Entity ====================== */

    /**
     * 将前端传来的菜单 DTO 转换为数据库实体
     *
     * @param dto SysMenuDTO 对象
     * @return SysMenu 实体对象
     */
    SysMenu toEntity(SysMenuDTO dto);

    /**
     * 批量转换 DTO → Entity
     *
     * @param dtoList DTO 列表
     * @return Entity 列表
     */
    List<SysMenu> toEntityList(List<SysMenuDTO> dtoList);

    /* ====================== Entity → VO ====================== */

    /**
     * Entity → SysMenuVO
     *
     * @param entity SysMenu 实体
     * @return SysMenuVO 对象
     */
    SysMenuVO toVO(SysMenu entity);

    /**
     * 批量转换 Entity → VO
     *
     * @param entityList 实体列表
     * @return VO 列表
     */
    List<SysMenuVO> toVOList(List<SysMenu> entityList);

    /* ====================== Entity → TreeVO ====================== */

    /**
     * Entity → SysMenuTreeVO（带 Meta 信息）
     *
     * @param entity SysMenu 实体
     * @return SysMenuTreeVO 对象
     */
    SysMenuTreeVO toTreeVo(SysMenu entity);

    /**
     * 批量转换 Entity → TreeVO
     *
     * @param entityList 实体列表
     * @return TreeVO 列表
     */
    List<SysMenuTreeVO> toTreeVOList(List<SysMenu> entityList);


    // ================= AfterMapping (自定义逻辑) =================

    /**
     * 构建 Meta 信息（Vue/React 路由关键信息）
     * 该方法会在 toTreeVO 映射完成后自动调用
     */
    @AfterMapping
    default void buildMeta(SysMenu entity, @MappingTarget SysMenuTreeVO vo) {
        MetaVO meta = new MetaVO();

        // 设置标题 (显示在侧边栏的文字)
        meta.setTitle(entity.getMenuName());

        // 设置图标
        meta.setIcon(entity.getMenuIcon());

        // 设置缓存 (通常从数据库字段读取，这里假设默认为 true)
        meta.setKeepAlive(true);

        // 设置隐藏 (路由存在但侧边栏不显示，例如按钮权限或详情页)
        // 0: 显示, 1: 隐藏
        meta.setHidden(entity.getMenuVisible() != null && entity.getMenuVisible() == 1);

        // 设置固定 (是否固定在 tagsView)
        meta.setAffix(false);

        // 将构建好的 Meta 注入 VO
        vo.setMeta(meta);
    }
}

