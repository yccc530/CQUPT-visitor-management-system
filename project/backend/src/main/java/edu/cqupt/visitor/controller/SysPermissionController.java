package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.SysPermission;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.SysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "权限管理")
@RestController
@RequestMapping("/api/sys-permissions")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    @Operation(summary = "分页查询权限")
    @GetMapping
    public ApiResponse<Page<SysPermission>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(sysPermissionService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<SysPermission> detail(@PathVariable Long id) {
        SysPermission entity = sysPermissionService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增权限")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody SysPermission entity) {
        return ApiResponse.ok(sysPermissionService.save(entity));
    }

    @Operation(summary = "修改权限")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody SysPermission entity) {
        entity.setId(id);
        return ApiResponse.ok(sysPermissionService.updateById(entity));
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(sysPermissionService.removeById(id));
    }
}
