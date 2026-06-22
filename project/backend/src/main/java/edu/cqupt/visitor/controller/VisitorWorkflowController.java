package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.dto.workflow.ApprovalDecisionRequest;
import edu.cqupt.visitor.dto.workflow.EntryRegisterRequest;
import edu.cqupt.visitor.dto.workflow.ExitRegisterRequest;
import edu.cqupt.visitor.dto.workflow.GateVerifyRequest;
import edu.cqupt.visitor.dto.workflow.GateVerifyResponse;
import edu.cqupt.visitor.dto.workflow.VisitApplySubmitRequest;
import edu.cqupt.visitor.dto.workflow.VisitApplyUpdateRequest;
import edu.cqupt.visitor.entity.AccessRecord;
import edu.cqupt.visitor.entity.PassCode;
import edu.cqupt.visitor.entity.VisitApply;
import edu.cqupt.visitor.service.VisitorWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "访客预约审批与出入校核心流程")
@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class VisitorWorkflowController {

    private final VisitorWorkflowService visitorWorkflowService;

    @Operation(summary = "提交访客预约申请")
    @PostMapping("/visit-applies")
    public ApiResponse<VisitApply> submitApplication(@Valid @RequestBody VisitApplySubmitRequest request) {
        return ApiResponse.ok("预约申请已提交，等待被访人确认", visitorWorkflowService.submitApplication(request));
    }

    @Operation(summary = "查询我的预约")
    @GetMapping("/visit-applies/my")
    public ApiResponse<Page<VisitApply>> myApplications(@RequestParam(defaultValue = "1") long current,
                                                        @RequestParam(defaultValue = "10") long size,
                                                        @RequestParam(required = false) String phone,
                                                        @RequestParam(required = false) String idNumber) {
        return ApiResponse.ok(visitorWorkflowService.myApplications(current, size, phone, idNumber));
    }

    @Operation(summary = "查询预约详情")
    @GetMapping("/visit-applies/{id}")
    public ApiResponse<VisitApply> detail(@PathVariable Long id) {
        return ApiResponse.ok(visitorWorkflowService.detail(id));
    }

    @Operation(summary = "修改未审批预约")
    @PutMapping("/visit-applies/{id}")
    public ApiResponse<VisitApply> updateUnapproved(@PathVariable Long id,
                                                    @Valid @RequestBody VisitApplyUpdateRequest request) {
        return ApiResponse.ok("未审批预约已修改", visitorWorkflowService.updateUnapproved(id, request));
    }

    @Operation(summary = "取消未审批预约")
    @PostMapping("/visit-applies/{id}/cancel")
    public ApiResponse<VisitApply> cancelUnapproved(@PathVariable Long id,
                                                    @RequestParam(required = false) String reason) {
        return ApiResponse.ok("预约已取消", visitorWorkflowService.cancelUnapproved(id, reason));
    }

    @Operation(summary = "查询被访人待确认预约")
    @GetMapping("/host/pending")
    public ApiResponse<Page<VisitApply>> pendingHostApplications(@RequestParam(defaultValue = "1") long current,
                                                                 @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(visitorWorkflowService.pendingHostApplications(current, size));
    }

    @Operation(summary = "被访人确认预约")
    @PostMapping("/host/{id}/confirm")
    public ApiResponse<VisitApply> hostConfirm(@PathVariable Long id,
                                               @RequestBody(required = false) ApprovalDecisionRequest request) {
        return ApiResponse.ok("被访人已确认，等待部门审批", visitorWorkflowService.hostConfirm(id, request));
    }

    @Operation(summary = "被访人拒绝预约")
    @PostMapping("/host/{id}/reject")
    public ApiResponse<VisitApply> hostReject(@PathVariable Long id,
                                              @RequestBody(required = false) ApprovalDecisionRequest request) {
        return ApiResponse.ok("被访人已拒绝预约", visitorWorkflowService.hostReject(id, request));
    }

    @Operation(summary = "查询部门待审批预约")
    @GetMapping("/department/pending")
    public ApiResponse<Page<VisitApply>> pendingDepartmentApplications(@RequestParam(defaultValue = "1") long current,
                                                                       @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.ok(visitorWorkflowService.pendingDepartmentApplications(current, size));
    }

    @Operation(summary = "部门审批通过")
    @PostMapping("/department/{id}/approve")
    public ApiResponse<VisitApply> departmentApprove(@PathVariable Long id,
                                                     @RequestBody(required = false) ApprovalDecisionRequest request) {
        return ApiResponse.ok("部门审批通过，通行凭证已生成", visitorWorkflowService.departmentApprove(id, request));
    }

    @Operation(summary = "部门审批拒绝")
    @PostMapping("/department/{id}/reject")
    public ApiResponse<VisitApply> departmentReject(@PathVariable Long id,
                                                    @RequestBody(required = false) ApprovalDecisionRequest request) {
        return ApiResponse.ok("部门审批已拒绝", visitorWorkflowService.departmentReject(id, request));
    }

    @Operation(summary = "查询通行凭证")
    @GetMapping("/pass-codes")
    public ApiResponse<PassCode> getPassCode(@RequestParam(required = false) Long applyId,
                                             @RequestParam(required = false) String applyNo) {
        return ApiResponse.ok(visitorWorkflowService.getPassCode(applyId, applyNo));
    }

    @Operation(summary = "门岗核验预约或通行码")
    @PostMapping("/gate/verify")
    public ApiResponse<GateVerifyResponse> verifyAtGate(@RequestBody GateVerifyRequest request) {
        return ApiResponse.ok(visitorWorkflowService.verifyAtGate(request));
    }

    @Operation(summary = "入校登记")
    @PostMapping("/access/entry")
    public ApiResponse<AccessRecord> registerEntry(@Valid @RequestBody EntryRegisterRequest request) {
        return ApiResponse.ok("入校登记成功", visitorWorkflowService.registerEntry(request));
    }

    @Operation(summary = "离校登记")
    @PostMapping("/access/exit")
    public ApiResponse<AccessRecord> registerExit(@Valid @RequestBody ExitRegisterRequest request) {
        return ApiResponse.ok("离校登记成功", visitorWorkflowService.registerExit(request));
    }

    @Operation(summary = "查询超时未离校访客")
    @GetMapping("/access/overtime")
    public ApiResponse<Page<AccessRecord>> overdueVisitors(@RequestParam(defaultValue = "1") long current,
                                                           @RequestParam(defaultValue = "10") long size,
                                                           @RequestParam(defaultValue = "false") boolean mark) {
        return ApiResponse.ok(visitorWorkflowService.overdueVisitors(current, size, mark));
    }
}