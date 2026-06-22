package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.SysRole;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.SysRoleService;
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

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/sys-roles")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Operation(summary = "分页查询角色")
    @GetMapping
    public ApiResponse<Page<SysRole>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(sysRoleService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<SysRole> detail(@PathVariable Long id) {
        SysRole entity = sysRoleService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody SysRole entity) {
        return ApiResponse.ok(sysRoleService.save(entity));
    }

    @Operation(summary = "修改角色")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody SysRole entity) {
        entity.setId(id);
        return ApiResponse.ok(sysRoleService.updateById(entity));
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(sysRoleService.removeById(id));
    }
}
