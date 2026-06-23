package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.entity.AccessRecord;
import edu.cqupt.visitor.entity.VisitApply;
import edu.cqupt.visitor.entity.Visitor;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.service.AccessRecordService;
import edu.cqupt.visitor.service.IntegrationViewService;
import edu.cqupt.visitor.service.VisitApplyService;
import edu.cqupt.visitor.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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

@Tag(name = "出入校记录管理")
@RestController
@RequestMapping("/api/access-records")
@RequiredArgsConstructor
public class AccessRecordController {

    private final AccessRecordService accessRecordService;
    private final VisitApplyService visitApplyService;
    private final VisitorService visitorService;
    private final IntegrationViewService integrationViewService;

    @Operation(summary = "分页查询出入校记录")
    @GetMapping
    public ApiResponse<Page<AccessRecord>> list(@RequestParam(defaultValue = "1") long current,
                                                @RequestParam(defaultValue = "10") long size,
                                                @RequestParam(required = false) String applyNo,
                                                @RequestParam(required = false) String visitorName,
                                                @RequestParam(required = false) String phone,
                                                @RequestParam(required = false) String accessStatus) {
        LambdaQueryWrapper<AccessRecord> wrapper = new LambdaQueryWrapper<AccessRecord>().eq(AccessRecord::getDeleted, 0);
        if (StringUtils.hasText(accessStatus)) {
            wrapper.eq(AccessRecord::getAccessStatus, accessStatus);
        }
        List<Long> applyIds = applyIds(applyNo);
        if (StringUtils.hasText(applyNo) && applyIds.isEmpty()) {
            return ApiResponse.ok(emptyAccessRecordPage(current, size));
        }
        if (!applyIds.isEmpty()) {
            wrapper.in(AccessRecord::getApplyId, applyIds);
        }
        List<Long> visitorIds = visitorIds(visitorName, phone);
        if ((StringUtils.hasText(visitorName) || StringUtils.hasText(phone)) && visitorIds.isEmpty()) {
            return ApiResponse.ok(emptyAccessRecordPage(current, size));
        }
        if (!visitorIds.isEmpty()) {
            wrapper.in(AccessRecord::getVisitorId, visitorIds);
        }
        wrapper.orderByDesc(AccessRecord::getEntryTime).orderByDesc(AccessRecord::getCreateTime);
        return ApiResponse.ok(integrationViewService.enrichAccessRecordPage(accessRecordService.page(new Page<>(current, size), wrapper)));
    }

    @Operation(summary = "查询出入校记录详情")
    @GetMapping("/{id}")
    public ApiResponse<AccessRecord> detail(@PathVariable Long id) {
        AccessRecord entity = accessRecordService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "出入校记录不存在");
        }
        return ApiResponse.ok(integrationViewService.enrichAccessRecord(entity));
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

    private List<Long> applyIds(String applyNo) {
        if (!StringUtils.hasText(applyNo)) {
            return List.of();
        }
        return visitApplyService.list(new LambdaQueryWrapper<VisitApply>()
                .like(VisitApply::getApplyNo, applyNo)
                .eq(VisitApply::getDeleted, 0))
                .stream().map(VisitApply::getId).toList();
    }

    private List<Long> visitorIds(String visitorName, String phone) {
        if (!StringUtils.hasText(visitorName) && !StringUtils.hasText(phone)) {
            return List.of();
        }
        LambdaQueryWrapper<Visitor> wrapper = new LambdaQueryWrapper<Visitor>().eq(Visitor::getDeleted, 0);
        if (StringUtils.hasText(visitorName)) {
            wrapper.like(Visitor::getVisitorName, visitorName);
        }
        if (StringUtils.hasText(phone)) {
            wrapper.like(Visitor::getPhone, phone);
        }
        return visitorService.list(wrapper).stream().map(Visitor::getId).toList();
    }

    private Page<AccessRecord> emptyAccessRecordPage(long current, long size) {
        Page<AccessRecord> page = new Page<>(current, size);
        page.setRecords(List.of());
        page.setTotal(0);
        return page;
    }
}