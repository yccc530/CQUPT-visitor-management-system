package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.ReportRecord;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.ReportRecordService;
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

@Tag(name = "报告记录管理")
@RestController
@RequestMapping("/api/report-records")
@RequiredArgsConstructor
public class ReportRecordController {

    private final ReportRecordService reportRecordService;

    @Operation(summary = "分页查询报告记录")
    @GetMapping
    public ApiResponse<Page<ReportRecord>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(reportRecordService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<ReportRecord> detail(@PathVariable Long id) {
        ReportRecord entity = reportRecordService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增报告记录")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody ReportRecord entity) {
        return ApiResponse.ok(reportRecordService.save(entity));
    }

    @Operation(summary = "修改报告记录")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody ReportRecord entity) {
        entity.setId(id);
        return ApiResponse.ok(reportRecordService.updateById(entity));
    }

    @Operation(summary = "删除报告记录")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(reportRecordService.removeById(id));
    }
}
