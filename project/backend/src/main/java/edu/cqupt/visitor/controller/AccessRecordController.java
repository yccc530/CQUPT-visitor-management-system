package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.AccessRecord;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.AccessRecordService;
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

@Tag(name = "出入校记录管理")
@RestController
@RequestMapping("/api/access-records")
@RequiredArgsConstructor
public class AccessRecordController {

    private final AccessRecordService accessRecordService;

    @Operation(summary = "分页查询出入校记录")
    @GetMapping
    public ApiResponse<Page<AccessRecord>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(accessRecordService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<AccessRecord> detail(@PathVariable Long id) {
        AccessRecord entity = accessRecordService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增出入校记录")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody AccessRecord entity) {
        return ApiResponse.ok(accessRecordService.save(entity));
    }

    @Operation(summary = "修改出入校记录")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody AccessRecord entity) {
        entity.setId(id);
        return ApiResponse.ok(accessRecordService.updateById(entity));
    }

    @Operation(summary = "删除出入校记录")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(accessRecordService.removeById(id));
    }
}
