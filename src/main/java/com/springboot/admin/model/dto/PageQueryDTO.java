package com.springboot.admin.model.dto;

import lombok.Data;

@Data
public class PageQueryDTO {
    /**
     * 当前页码，默认 1
     */
    private int page = 1;
    /**
     * 每页大小，默认 10
     */
    private int size = 10;
}
