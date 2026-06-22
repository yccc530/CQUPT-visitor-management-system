package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.SysRolePermission;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.SysRolePermissionService;
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

@Tag(name = "角色权限关联管理")
@RestController
@RequestMapping("/api/sys-role-permissions")
@RequiredArgsConstructor
public class SysRolePermissionController {

    private final SysRolePermissionService sysRolePermissionService;

    @Operation(summary = "分页查询角色权限关联")
    @GetMapping
    public ApiResponse<Page<SysRolePermission>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(sysRolePermissionService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<SysRolePermission> detail(@PathVariable Long id) {
        SysRolePermission entity = sysRolePermissionService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增角色权限关联")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody SysRolePermission entity) {
        return ApiResponse.ok(sysRolePermissionService.save(entity));
    }

    @Operation(summary = "修改角色权限关联")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody SysRolePermission entity) {
        entity.setId(id);
        return ApiResponse.ok(sysRolePermissionService.updateById(entity));
    }

    @Operation(summary = "删除角色权限关联")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(sysRolePermissionService.removeById(id));
    }
}
