package edu.cqupt.visitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

public interface VisitorWorkflowService {

    VisitApply submitApplication(VisitApplySubmitRequest request);

    Page<VisitApply> myApplications(long current, long size, String phone, String idNumber);

    VisitApply updateUnapproved(Long id, VisitApplyUpdateRequest request);

    VisitApply cancelUnapproved(Long id, String reason);

    VisitApply detail(Long id);

    Page<VisitApply> pendingHostApplications(long current, long size);

    VisitApply hostConfirm(Long id, ApprovalDecisionRequest request);

    VisitApply hostReject(Long id, ApprovalDecisionRequest request);

    Page<VisitApply> pendingDepartmentApplications(long current, long size);

    VisitApply departmentApprove(Long id, ApprovalDecisionRequest request);

    VisitApply departmentReject(Long id, ApprovalDecisionRequest request);

    PassCode getPassCode(Long applyId, String applyNo);

    GateVerifyResponse verifyAtGate(GateVerifyRequest request);

    AccessRecord registerEntry(EntryRegisterRequest request);

    AccessRecord registerExit(ExitRegisterRequest request);

    Page<AccessRecord> overdueVisitors(long current, long size, boolean mark);
}