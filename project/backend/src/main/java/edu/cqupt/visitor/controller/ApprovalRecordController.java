package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.ApprovalRecord;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.ApprovalRecordService;
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

@Tag(name = "审批记录管理")
@RestController
@RequestMapping("/api/approval-records")
@RequiredArgsConstructor
public class ApprovalRecordController {

    private final ApprovalRecordService approvalRecordService;

    @Operation(summary = "分页查询审批记录")
    @GetMapping
    public ApiResponse<Page<ApprovalRecord>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(approvalRecordService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<ApprovalRecord> detail(@PathVariable Long id) {
        ApprovalRecord entity = approvalRecordService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增审批记录")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody ApprovalRecord entity) {
        return ApiResponse.ok(approvalRecordService.save(entity));
    }

    @Operation(summary = "修改审批记录")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody ApprovalRecord entity) {
        entity.setId(id);
        return ApiResponse.ok(approvalRecordService.updateById(entity));
    }

    @Operation(summary = "删除审批记录")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(approvalRecordService.removeById(id));
    }
}
