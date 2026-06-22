package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.ScreenshotRecord;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.ScreenshotRecordService;
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

@Tag(name = "截图记录管理")
@RestController
@RequestMapping("/api/screenshot-records")
@RequiredArgsConstructor
public class ScreenshotRecordController {

    private final ScreenshotRecordService screenshotRecordService;

    @Operation(summary = "分页查询截图记录")
    @GetMapping
    public ApiResponse<Page<ScreenshotRecord>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(screenshotRecordService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<ScreenshotRecord> detail(@PathVariable Long id) {
        ScreenshotRecord entity = screenshotRecordService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增截图记录")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody ScreenshotRecord entity) {
        return ApiResponse.ok(screenshotRecordService.save(entity));
    }

    @Operation(summary = "修改截图记录")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody ScreenshotRecord entity) {
        entity.setId(id);
        return ApiResponse.ok(screenshotRecordService.updateById(entity));
    }

    @Operation(summary = "删除截图记录")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(screenshotRecordService.removeById(id));
    }
}
