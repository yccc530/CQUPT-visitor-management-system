package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.Blacklist;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.security.AuthContext;
import edu.cqupt.visitor.service.BlacklistService;
import edu.cqupt.visitor.service.IntegrationViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
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

@Tag(name = "黑名单管理")
@RestController
@RequestMapping("/api/blacklists")
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;
    private final IntegrationViewService integrationViewService;

    @Operation(summary = "分页查询黑名单")
    @GetMapping
    public ApiResponse<Page<Blacklist>> list(@RequestParam(defaultValue = "1") long current,
                                             @RequestParam(defaultValue = "10") long size,
                                             @RequestParam(required = false) String phone,
                                             @RequestParam(required = false) String idNumber,
                                             @RequestParam(required = false) String status) {
        LambdaQueryWrapper<Blacklist> wrapper = new LambdaQueryWrapper<Blacklist>().eq(Blacklist::getDeleted, 0);
        if (StringUtils.hasText(phone)) {
            wrapper.like(Blacklist::getPhone, phone);
        }
        if (StringUtils.hasText(idNumber)) {
            wrapper.like(Blacklist::getIdNumber, idNumber);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Blacklist::getStatus, status);
        }
        wrapper.orderByDesc(Blacklist::getCreateTime);
        return ApiResponse.ok(integrationViewService.enrichBlacklistPage(blacklistService.page(new Page<>(current, size), wrapper)));
    }

    @Operation(summary = "查询黑名单详情")
    @GetMapping("/{id}")
    public ApiResponse<Blacklist> detail(@PathVariable Long id) {
        Blacklist entity = blacklistService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "黑名单记录不存在");
        }
        return ApiResponse.ok(integrationViewService.enrichBlacklist(entity));
    }

    @Operation(summary = "新增黑名单")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody Blacklist entity) {
        if (entity.getStartTime() == null) {
            entity.setStartTime(LocalDateTime.now());
        }
        if (entity.getOperatorUserId() == null) {
            entity.setOperatorUserId(AuthContext.currentUser().getId());
        }
        if (!StringUtils.hasText(entity.getStatus())) {
            entity.setStatus("ACTIVE");
        }
        return ApiResponse.ok(blacklistService.save(entity));
    }

    @Operation(summary = "修改黑名单")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody Blacklist entity) {
        entity.setId(id);
        return ApiResponse.ok(blacklistService.updateById(entity));
    }

    @Operation(summary = "删除黑名单")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(blacklistService.removeById(id));
    }
}