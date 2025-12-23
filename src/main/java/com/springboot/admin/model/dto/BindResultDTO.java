package com.springboot.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * 统用绑定结果 DTO
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindResultDTO {
    /**
     * 新增的数量
     */
    private Long addedCount;
    /**
     * 删除的数量
     */
    private Long removedCount;
}
