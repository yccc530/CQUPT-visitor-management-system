package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.Blacklist;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.BlacklistService;
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

@Tag(name = "黑名单管理")
@RestController
@RequestMapping("/api/blacklists")
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;

    @Operation(summary = "分页查询黑名单")
    @GetMapping
    public ApiResponse<Page<Blacklist>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(blacklistService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<Blacklist> detail(@PathVariable Long id) {
        Blacklist entity = blacklistService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增黑名单")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody Blacklist entity) {
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
