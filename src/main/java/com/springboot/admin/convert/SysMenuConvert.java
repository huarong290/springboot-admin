package com.springboot.admin.convert;

import com.springboot.admin.model.dto.menu.SysMenuDTO;
import com.springboot.admin.model.entity.sys.SysMenu;
import com.springboot.admin.model.vo.menu.MetaVO;
import com.springboot.admin.model.vo.menu.SysMenuTreeVO;
import com.springboot.admin.model.vo.menu.SysMenuVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 菜单对象转换器
 * <p>
 * 使用 MapStruct 自动生成对象之间的转换代码。
 * 主要用于 SysMenuDTO、SysMenu 实体、SysMenuVO、SysMenuTreeVO 之间的转换。
 *
 * 设计目的：
 * - 保持分层清晰：DTO 用于接收前端数据，Entity 对应数据库表，VO 用于返回前端展示。
 * - 避免手写重复的转换代码，提高开发效率。
 * - 保证对象之间字段映射的一致性，减少人为错误。
 *
 * 使用场景：
 * - Controller 层接收前端传入的 DTO，调用 Service 层时转换为 Entity。
 * - Service 层查询数据库得到 Entity，返回给 Controller 时转换为 VO。
 * - 构建菜单树时，将平铺的 VO 转换为树形 VO。
 */
@Mapper(componentModel = "spring")
public interface SysMenuConvert {

    /**
     * DTO -> Entity
     * <p>
     * 用途：
     * - 新增菜单：前端传入 SysMenuDTO，转换为 SysMenu 实体保存到数据库。
     * - 更新菜单：前端传入 SysMenuDTO，转换为 SysMenu 实体更新数据库。
     *
     * @param dto 前端传入的菜单数据对象
     * @return SysMenu 数据库实体对象
     */
    SysMenu toEntity(SysMenuDTO dto);

    /**
     * Entity -> VO
     * <p>
     * 用途：
     * - 查询菜单详情：数据库返回 SysMenu 实体，转换为 SysMenuVO 返回给前端。
     * - 菜单列表展示：数据库返回 SysMenu 实体集合，逐个转换为 SysMenuVO。
     *
     * @param entity 数据库查询得到的菜单实体
     * @return SysMenuVO 前端展示对象
     */
    SysMenuVO toVO(SysMenu entity);

    /**
     * List<Entity> -> List<VO>
     * <p>
     * 用途：
     * - 批量查询菜单列表：数据库返回 SysMenu 实体集合，转换为 SysMenuVO 集合。
     * - 适合后台管理场景，例如菜单维护页面。
     *
     * @param entities 数据库查询得到的菜单实体集合
     * @return List<SysMenuVO> 前端展示对象集合
     */
    List<SysMenuVO> toVOList(List<SysMenu> entities);

    /**
     * VO -> TreeVO
     * <p>
     * 用途：
     * - 构建菜单树：将平铺的 SysMenuVO 转换为 SysMenuTreeVO。
     * - 同时填充 meta 信息，避免返回 null。
     *
     * @param vo 平铺的菜单展示对象 SysMenuVO
     * @return SysMenuTreeVO 树形菜单展示对象
     */
    default SysMenuTreeVO toTreeVO(SysMenuVO vo) {
        SysMenuTreeVO treeVO = new SysMenuTreeVO();
        treeVO.setId(vo.getId());
        treeVO.setMenuName(vo.getMenuName());
        treeVO.setMenuPath(vo.getMenuPath());
        treeVO.setMenuComponent(vo.getMenuComponent());
        treeVO.setMenuParentId(vo.getMenuParentId());
        treeVO.setMenuType(vo.getMenuType());
        treeVO.setMenuIcon(vo.getMenuIcon());
        treeVO.setMenuPermission(vo.getMenuPermission());
        treeVO.setMenuSort(vo.getMenuSort());
        treeVO.setMenuVisible(vo.getMenuVisible());
        treeVO.setMenuStatus(vo.getMenuStatus());
        treeVO.setCreateTime(vo.getCreateTime());
        treeVO.setUpdateTime(vo.getUpdateTime());

        // 构建 meta 信息
        MetaVO meta = new MetaVO();
        meta.setTitle(vo.getMenuName());
        meta.setIcon(vo.getMenuIcon());
        meta.setKeepAlive(true);
        meta.setHidden(vo.getMenuVisible() != null && vo.getMenuVisible() == 0);
        treeVO.setMeta(meta);

        return treeVO;
    }

    /**
     * Entity -> VO (带叶子节点标识)
     * <p>
     * 用途：
     * - 在懒加载菜单树场景下，前端需要知道某个菜单节点是否还有子节点。
     * - 数据库 SysMenu 实体本身没有 isLeaf 字段，因此需要在转换时动态补充。
     * - Service 层会调用 Repository 判断该菜单是否有子节点，然后传入 hasChildren。
     * - 根据 hasChildren 的值，设置 SysMenuVO.isLeaf：
     *   - hasChildren = true  → isLeaf = false（不是叶子节点）
     *   - hasChildren = false → isLeaf = true（是叶子节点）
     *
     * 使用场景：
     * - 懒加载菜单树：前端 el-tree 的 lazy 模式依赖 isLeaf 来判断是否继续加载。
     * - 菜单管理：在后台管理页面展示树形结构时，避免一次性加载所有节点，提升性能。
     *
     * @param entity      数据库查询得到的菜单实体对象
     * @param hasChildren 是否存在子节点（true 表示有子节点，false 表示没有子节点）
     * @return SysMenuVO  前端展示对象，包含 isLeaf 字段，用于懒加载树判断
     */
    default SysMenuVO toVOWithLeaf(SysMenu entity, boolean hasChildren) {
        SysMenuVO vo = toVO(entity);
        vo.setIsLeaf(!hasChildren); // 没有子节点时标记为叶子
        return vo;
    }


}
