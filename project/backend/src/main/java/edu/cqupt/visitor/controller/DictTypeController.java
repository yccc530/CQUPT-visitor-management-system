package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.DictType;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.DictTypeService;
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

@Tag(name = "字典类型管理")
@RestController
@RequestMapping("/api/dict-types")
@RequiredArgsConstructor
public class DictTypeController {

    private final DictTypeService dictTypeService;

    @Operation(summary = "分页查询字典类型")
    @GetMapping
    public ApiResponse<Page<DictType>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(dictTypeService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<DictType> detail(@PathVariable Long id) {
        DictType entity = dictTypeService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增字典类型")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody DictType entity) {
        return ApiResponse.ok(dictTypeService.save(entity));
    }

    @Operation(summary = "修改字典类型")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody DictType entity) {
        entity.setId(id);
        return ApiResponse.ok(dictTypeService.updateById(entity));
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(dictTypeService.removeById(id));
    }
}
