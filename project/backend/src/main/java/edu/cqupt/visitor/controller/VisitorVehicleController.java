package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.VisitorVehicle;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.VisitorVehicleService;
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

@Tag(name = "访客车辆管理")
@RestController
@RequestMapping("/api/visitor-vehicles")
@RequiredArgsConstructor
public class VisitorVehicleController {

    private final VisitorVehicleService visitorVehicleService;

    @Operation(summary = "分页查询访客车辆")
    @GetMapping
    public ApiResponse<Page<VisitorVehicle>> list(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(visitorVehicleService.page(new Page<>(current, size)));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public ApiResponse<VisitorVehicle> detail(@PathVariable Long id) {
        VisitorVehicle entity = visitorVehicleService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增访客车辆")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody VisitorVehicle entity) {
        return ApiResponse.ok(visitorVehicleService.save(entity));
    }

    @Operation(summary = "修改访客车辆")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody VisitorVehicle entity) {
        entity.setId(id);
        return ApiResponse.ok(visitorVehicleService.updateById(entity));
    }

    @Operation(summary = "删除访客车辆")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(visitorVehicleService.removeById(id));
    }
}
