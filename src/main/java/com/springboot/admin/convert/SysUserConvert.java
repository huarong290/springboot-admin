package com.springboot.admin.convert;

import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.model.vo.user.SysUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统用户转换器
 * <p>
 * 使用 MapStruct 实现 SysUser / SysUserDTO / SysUserVO 之间的相互转换
 * - DTO 用于 Service 层业务逻辑处理
 * - VO 用于接口层返回前端，避免敏感信息泄露
 */
@Mapper(componentModel = "spring")
public interface SysUserConvert {

    /**
     * 单例实例
     */
    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    /**
     * SysUser 实体 → SysUserDTO
     * <p>
     * 保留密码、状态等业务字段，用于 Service 层处理
     *
     * @param sysUser 实体对象
     * @return SysUserDTO
     */
    SysUserDTO entityToDTO(SysUser sysUser);

    /**
     * SysUserDTO → SysUser 实体
     * <p>
     * 适用于新增或更新用户时，将 DTO 转为实体
     *
     * @param dto DTO 对象
     * @return SysUser 实体
     */
    SysUser dtoToEntity(SysUserDTO dto);

    /**
     * SysUserDTO → SysUserVO
     * <p>
     * 去掉密码等敏感信息，提供给前端返回
     *
     * @param dto DTO 对象
     * @return SysUserVO
     */
    @Mapping(target = "password", ignore = true)
    SysUserVO dtoToVO(SysUserDTO dto);

    /**
     * 批量转换：List<SysUser> → List<SysUserDTO>
     *
     * @param list 实体列表
     * @return DTO 列表
     */
    List<SysUserDTO> entityListToDTOList(List<SysUser> list);

    /**
     * 批量转换：List<SysUserDTO> → List<SysUserVO>
     *
     * @param list DTO 列表
     * @return VO 列表
     */
    List<SysUserVO> dtoListToVOList(List<SysUserDTO> list);
}
