package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.SysUserRole;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.SysUserRoleService;
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

@Tag(name = "用户角色关联管理")
@RestController
@RequestMapping("/api/sys-user-roles")
@RequiredArgsConstructor
public class SysUserRoleController {

    private final SysUserRoleService sysUserRoleService;

    @Operation(summary = "分页查询用户角色关联")
    @GetMapping
    public ApiResponse<Page<SysUserRole>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(sysUserRoleService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<SysUserRole> detail(@PathVariable Long id) {
        SysUserRole entity = sysUserRoleService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增用户角色关联")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody SysUserRole entity) {
        return ApiResponse.ok(sysUserRoleService.save(entity));
    }

    @Operation(summary = "修改用户角色关联")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody SysUserRole entity) {
        entity.setId(id);
        return ApiResponse.ok(sysUserRoleService.updateById(entity));
    }

    @Operation(summary = "删除用户角色关联")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(sysUserRoleService.removeById(id));
    }
}
