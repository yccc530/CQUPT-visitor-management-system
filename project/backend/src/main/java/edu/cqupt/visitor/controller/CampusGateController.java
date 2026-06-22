package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.CampusGate;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.CampusGateService;
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

@Tag(name = "校门管理")
@RestController
@RequestMapping("/api/campus-gates")
@RequiredArgsConstructor
public class CampusGateController {

    private final CampusGateService campusGateService;

    @Operation(summary = "分页查询校门")
    @GetMapping
    public ApiResponse<Page<CampusGate>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(campusGateService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<CampusGate> detail(@PathVariable Long id) {
        CampusGate entity = campusGateService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增校门")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody CampusGate entity) {
        return ApiResponse.ok(campusGateService.save(entity));
    }

    @Operation(summary = "修改校门")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody CampusGate entity) {
        entity.setId(id);
        return ApiResponse.ok(campusGateService.updateById(entity));
    }

    @Operation(summary = "删除校门")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(campusGateService.removeById(id));
    }
}
