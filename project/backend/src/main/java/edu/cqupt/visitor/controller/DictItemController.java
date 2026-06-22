package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.DictItem;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.DictItemService;
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

@Tag(name = "字典项管理")
@RestController
@RequestMapping("/api/dict-items")
@RequiredArgsConstructor
public class DictItemController {

    private final DictItemService dictItemService;

    @Operation(summary = "分页查询字典项")
    @GetMapping
    public ApiResponse<Page<DictItem>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(dictItemService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<DictItem> detail(@PathVariable Long id) {
        DictItem entity = dictItemService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增字典项")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody DictItem entity) {
        return ApiResponse.ok(dictItemService.save(entity));
    }

    @Operation(summary = "修改字典项")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody DictItem entity) {
        entity.setId(id);
        return ApiResponse.ok(dictItemService.updateById(entity));
    }

    @Operation(summary = "删除字典项")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(dictItemService.removeById(id));
    }
}
