package com.springboot.admin.mapper.ext;

import com.springboot.admin.mapper.auto.SysMenuMapper;
import com.springboot.admin.model.entity.sys.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper Ext接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysMenuExtMapper extends SysMenuMapper {

    /**
     * 根据用户ID查询菜单列表
     *
     */
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}
