package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.OperationLog;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.OperationLogService;
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

@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping
    public ApiResponse<Page<OperationLog>> list(@RequestParam(defaultValue = "1") long current,
                                                @RequestParam(defaultValue = "10") long size,
                                                @RequestParam(required = false) String operatorName,
                                                @RequestParam(required = false) String moduleName,
                                                @RequestParam(required = false) String operationType,
                                                @RequestParam(required = false) String operationResult) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>().eq(OperationLog::getDeleted, 0);
        if (StringUtils.hasText(operatorName)) {
            wrapper.like(OperationLog::getOperatorName, operatorName);
        }
        if (StringUtils.hasText(moduleName)) {
            wrapper.like(OperationLog::getModuleName, moduleName);
        }
        if (StringUtils.hasText(operationType)) {
            wrapper.like(OperationLog::getOperationType, operationType);
        }
        if (StringUtils.hasText(operationResult)) {
            wrapper.eq(OperationLog::getOperationResult, operationResult);
        }
        wrapper.orderByDesc(OperationLog::getOperationTime).orderByDesc(OperationLog::getCreateTime);
        return ApiResponse.ok(operationLogService.page(new Page<>(current, size), wrapper));
    }

    @Operation(summary = "查询操作日志详情")
    @GetMapping("/{id}")
    public ApiResponse<OperationLog> detail(@PathVariable Long id) {
        OperationLog entity = operationLogService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "操作日志不存在");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增操作日志")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody OperationLog entity) {
        return ApiResponse.ok(operationLogService.save(entity));
    }

    @Operation(summary = "修改操作日志")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody OperationLog entity) {
        entity.setId(id);
        return ApiResponse.ok(operationLogService.updateById(entity));
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(operationLogService.removeById(id));
    }
}