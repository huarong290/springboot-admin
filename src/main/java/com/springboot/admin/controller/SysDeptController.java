package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.dept.SysDeptDTO;
import com.springboot.admin.model.dto.dept.SysDeptQueryDTO;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.dept.SysDeptVO;
import com.springboot.admin.service.ISysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 部门管理 Controller
 * 提供部门的增删改查接口
 */
@RestController
@RequestMapping("/api/dept")
@Tag(name = "部门管理", description = "部门增删改查接口")
@Slf4j
public class SysDeptController {

    @Autowired
    private ISysDeptService deptService;

    /**
     * 分页查询部门列表
     */
    @GetMapping("/pageDeptList")
    @Operation(summary = "分页查询部门列表")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<PageResult<SysDeptVO>>> pageDeptList(SysDeptQueryDTO query) {
        return deptService.pageDeptList(query).map(ApiResult::successResult);
    }

    /**
     * 新增部门
     */
    @PostMapping("/addDept")
    @Operation(summary = "新增部门")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> addDept(@RequestBody SysDeptDTO sysDeptDTO) {
        return deptService.addDept(sysDeptDTO).map(ApiResult::successResult);
    }

    /**
     * 更新部门
     */
    @PutMapping("/updateDept")
    @Operation(summary = "更新部门信息")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> updateDept(@RequestBody SysDeptDTO sysDeptDTO) {
        return deptService.updateDept(sysDeptDTO).map(ApiResult::successResult);
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/deleteDept/{id}")
    @Operation(summary = "删除部门")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> deleteDept(@PathVariable Long id) {
        return deptService.deleteDept(id).map(rows -> ApiResult.successResult("删除成功", rows));
    }
}

