package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.SysUser;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.IntegrationViewService;
import edu.cqupt.visitor.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/sys-users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final IntegrationViewService integrationViewService;

    @Operation(summary = "分页查询系统用户")
    @GetMapping
    public ApiResponse<Page<SysUser>> list(@RequestParam(defaultValue = "1") long current,
                                           @RequestParam(defaultValue = "10") long size,
                                           @RequestParam(required = false) String username,
                                           @RequestParam(required = false) String realName,
                                           @RequestParam(required = false) String userType,
                                           @RequestParam(required = false) String status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeleted, 0);
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (StringUtils.hasText(realName)) {
            wrapper.like(SysUser::getRealName, realName);
        }
        if (StringUtils.hasText(userType)) {
            wrapper.eq(SysUser::getUserType, userType);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByAsc(SysUser::getId);
        return ApiResponse.ok(integrationViewService.enrichSysUserPage(sysUserService.page(new Page<>(current, size), wrapper)));
    }

    @Operation(summary = "查询系统用户详情")
    @GetMapping("/{id}")
    public ApiResponse<SysUser> detail(@PathVariable Long id) {
        SysUser entity = sysUserService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "系统用户不存在");
        }
        return ApiResponse.ok(integrationViewService.enrichSysUser(entity));
    }

    @Operation(summary = "新增系统用户")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody SysUser entity) {
        return ApiResponse.ok(sysUserService.save(entity));
    }

    @Operation(summary = "修改系统用户")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody SysUser entity) {
        entity.setId(id);
        return ApiResponse.ok(sysUserService.updateById(entity));
    }

    @Operation(summary = "删除系统用户")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(sysUserService.removeById(id));
    }
}