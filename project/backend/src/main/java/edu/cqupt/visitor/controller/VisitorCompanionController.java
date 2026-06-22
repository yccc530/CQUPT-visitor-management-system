package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.VisitorCompanion;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.VisitorCompanionService;
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

@Tag(name = "随行人员管理")
@RestController
@RequestMapping("/api/visitor-companions")
@RequiredArgsConstructor
public class VisitorCompanionController {

    private final VisitorCompanionService visitorCompanionService;

    @Operation(summary = "分页查询随行人员")
    @GetMapping
    public ApiResponse<Page<VisitorCompanion>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(visitorCompanionService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<VisitorCompanion> detail(@PathVariable Long id) {
        VisitorCompanion entity = visitorCompanionService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增随行人员")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody VisitorCompanion entity) {
        return ApiResponse.ok(visitorCompanionService.save(entity));
    }

    @Operation(summary = "修改随行人员")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody VisitorCompanion entity) {
        entity.setId(id);
        return ApiResponse.ok(visitorCompanionService.updateById(entity));
    }

    @Operation(summary = "删除随行人员")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(visitorCompanionService.removeById(id));
    }
}
