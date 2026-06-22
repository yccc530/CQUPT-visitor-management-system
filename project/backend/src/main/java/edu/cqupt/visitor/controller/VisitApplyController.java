package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.VisitApply;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.security.AuthContext;
import edu.cqupt.visitor.security.CurrentUser;
import edu.cqupt.visitor.service.VisitApplyService;
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

@Tag(name = "预约申请管理")
@RestController
@RequestMapping("/api/visit-applies")
@RequiredArgsConstructor
public class VisitApplyController {

    private final VisitApplyService visitApplyService;

    @Operation(summary = "分页查询访客预约申请")
    @GetMapping
    public ApiResponse<Page<VisitApply>> list(@RequestParam(defaultValue = "1") long current,
                                              @RequestParam(defaultValue = "10") long size) {
        LambdaQueryWrapper<VisitApply> wrapper = applyDataScope();
        wrapper.orderByDesc(VisitApply::getSubmitTime);
        return ApiResponse.ok(visitApplyService.page(new Page<>(current, size), wrapper));
    }

    @Operation(summary = "查询访客预约申请详情")
    @GetMapping("/{id}")
    public ApiResponse<VisitApply> detail(@PathVariable Long id) {
        VisitApply entity = visitApplyService.getOne(applyDataScope().eq(VisitApply::getId, id), false);
        if (entity == null) {
            throw new BusinessException(404, "预约申请不存在或无权访问");
        }
        return ApiResponse.ok(entity);
    }

    @Operation(summary = "新增访客预约申请")
    @PostMapping
    public ApiResponse<Boolean> create(@Valid @RequestBody VisitApply entity) {
        return ApiResponse.ok(visitApplyService.save(entity));
    }

    @Operation(summary = "修改访客预约申请")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody VisitApply entity) {
        entity.setId(id);
        return ApiResponse.ok(visitApplyService.updateById(entity));
    }

    @Operation(summary = "删除访客预约申请")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(visitApplyService.removeById(id));
    }

    private LambdaQueryWrapper<VisitApply> applyDataScope() {
        CurrentUser user = AuthContext.currentUser();
        LambdaQueryWrapper<VisitApply> wrapper = new LambdaQueryWrapper<VisitApply>().eq(VisitApply::getDeleted, 0);
        if (user.hasRole("ADMIN") || user.hasRole("SCHOOL_MANAGER")) {
            return wrapper;
        }
        if (user.hasRole("HOST")) {
            return wrapper.eq(VisitApply::getHostUserId, user.getId());
        }
        if (user.hasRole("DEPT_APPROVER")) {
            return wrapper.eq(VisitApply::getDepartmentId, user.getDepartmentId());
        }
        throw new BusinessException(403, "当前角色无权查看预约申请列表");
    }
}