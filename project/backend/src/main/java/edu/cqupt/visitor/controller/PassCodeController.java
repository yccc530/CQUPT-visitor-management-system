package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.PassCode;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.PassCodeService;
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

@Tag(name = "通行凭证管理")
@RestController
@RequestMapping("/api/pass-codes")
@RequiredArgsConstructor
public class PassCodeController {

    private final PassCodeService passCodeService;

    @Operation(summary = "分页查询通行凭证")
    @GetMapping
    public ApiResponse<Page<PassCode>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(passCodeService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<PassCode> detail(@PathVariable Long id) {
        PassCode entity = passCodeService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增通行凭证")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody PassCode entity) {
        return ApiResponse.ok(passCodeService.save(entity));
    }

    @Operation(summary = "修改通行凭证")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody PassCode entity) {
        entity.setId(id);
        return ApiResponse.ok(passCodeService.updateById(entity));
    }

    @Operation(summary = "删除通行凭证")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(passCodeService.removeById(id));
    }
}
