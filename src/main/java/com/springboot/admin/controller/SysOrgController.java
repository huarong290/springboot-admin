package com.springboot.admin.controller;

import com.springboot.admin.annotation.Logable;
import com.springboot.admin.common.ApiResult;
import com.springboot.admin.model.dto.org.SysOrgDTO;
import com.springboot.admin.model.dto.org.SysOrgQueryDTO;
import com.springboot.admin.model.vo.PageResult;
import com.springboot.admin.model.vo.org.SysOrgVO;
import com.springboot.admin.service.ISysOrgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 组织管理 Controller
 * 提供组织的增删改查接口
 */
@RestController
@RequestMapping("/api/org")
@Tag(name = "组织管理", description = "组织增删改查接口")
@Slf4j
public class SysOrgController {

    @Autowired
    private ISysOrgService orgService;

    /**
     * 分页查询组织列表
     */
    @GetMapping("/pageOrgList")
    @Operation(summary = "分页查询组织列表")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<PageResult<SysOrgVO>>> pageOrgList(SysOrgQueryDTO query) {
        return orgService.pageOrgList(query).map(ApiResult::successResult);
    }

    /**
     * 新增组织
     */
    @PostMapping("/addOrg")
    @Operation(summary = "新增组织")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> addOrg(@RequestBody SysOrgDTO sysOrgDTO) {
        return orgService.addOrg(sysOrgDTO).map(ApiResult::successResult);
    }

    /**
     * 更新组织
     */
    @PutMapping("/updateOrg")
    @Operation(summary = "更新组织信息")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> updateOrg(@RequestBody SysOrgDTO sysOrgDTO) {
        return orgService.updateOrg(sysOrgDTO).map(ApiResult::successResult);
    }

    /**
     * 删除组织
     */
    @DeleteMapping("/deleteOrg/{id}")
    @Operation(summary = "删除组织")
    @Logable(logRequest = true, logResponse = true)
    public Mono<ApiResult<Long>> deleteOrg(@PathVariable Long id) {
        return orgService.deleteOrg(id).map(rows -> ApiResult.successResult("删除成功", rows));
    }
}

