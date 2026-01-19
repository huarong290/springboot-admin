package com.springboot.admin.convert;

import com.springboot.admin.model.dto.user.SysUserDTO;
import com.springboot.admin.model.entity.sys.SysUser;
import com.springboot.admin.model.vo.user.SysUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统用户转换器
 * <p>
 * 使用 MapStruct 实现 SysUser / SysUserDTO / SysUserVO 之间的相互转换
 * <ul>
 *     <li>DTO：用于 Service 层业务逻辑处理，保留敏感字段（如 password）</li>
 *     <li>VO：用于接口层返回给前端，自动忽略敏感字段（password）</li>
 *     <li>Entity：数据库持久化对象</li>
 * </ul>
 */
@Mapper(componentModel = "spring")
public interface SysUserConvert {

    /**
     * 单例实例
     */
    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    // =================== Entity → DTO ===================

    /**
     * 将 SysUser 实体对象转换为 SysUserDTO
     * <p>
     * 保留密码、状态等业务字段，用于 Service 层逻辑处理
     *
     * @param sysUser 实体对象
     * @return DTO 对象
     */
    SysUserDTO entityToDTO(SysUser sysUser);

    /**
     * 批量转换：List<SysUser> → List<SysUserDTO>
     *
     * @param list 实体对象列表
     * @return DTO 列表
     */
    List<SysUserDTO> entityListToDTOList(List<SysUser> list);

    // =================== DTO → Entity ===================

    /**
     * 将 SysUserDTO 转换为 SysUser 实体对象
     * <p>
     * 适用于新增或更新用户时，将 DTO 转为实体
     *
     * @param dto DTO 对象
     * @return 实体对象
     */
    SysUser dtoToEntity(SysUserDTO dto);

    /**
     * 批量转换：List<SysUserDTO> → List<SysUser>
     *
     * @param dtos DTO 列表
     * @return 实体列表
     */
    List<SysUser> toEntityList(List<SysUserDTO> dtos);

    // =================== Entity → VO ===================

    /**
     * 将 SysUser 实体对象转换为 SysUserVO
     * <p>
     * VO 用于接口返回，自动忽略敏感字段（password）
     *
     * @param entity 实体对象
     * @return VO 对象
     */
    SysUserVO toVO(SysUser entity);

    /**
     * 批量转换：List<SysUser> → List<SysUserVO>
     *
     * @param entities 实体对象列表
     * @return VO 列表
     */
    List<SysUserVO> toVOList(List<SysUser> entities);

    // =================== DTO → VO ===================

    /**
     * 将 SysUserDTO 转换为 SysUserVO
     * <p>
     * 去掉密码等敏感信息，提供给前端返回
     *
     * @param dto DTO 对象
     * @return VO 对象
     */
    SysUserVO dtoToVO(SysUserDTO dto);

    /**
     * 批量转换：List<SysUserDTO> → List<SysUserVO>
     *
     * @param list DTO 列表
     * @return VO 列表
     */
    List<SysUserVO> dtoListToVOList(List<SysUserDTO> list);
}
