package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.cqupt.visitor.constant.WorkflowStatus;
import edu.cqupt.visitor.dto.workflow.ApprovalDecisionRequest;
import edu.cqupt.visitor.dto.workflow.CompanionRequest;
import edu.cqupt.visitor.dto.workflow.EntryRegisterRequest;
import edu.cqupt.visitor.dto.workflow.ExitRegisterRequest;
import edu.cqupt.visitor.dto.workflow.GateVerifyRequest;
import edu.cqupt.visitor.dto.workflow.GateVerifyResponse;
import edu.cqupt.visitor.dto.workflow.VisitApplySubmitRequest;
import edu.cqupt.visitor.dto.workflow.VisitApplyUpdateRequest;
import edu.cqupt.visitor.entity.AccessRecord;
import edu.cqupt.visitor.entity.ApprovalRecord;
import edu.cqupt.visitor.entity.Blacklist;
import edu.cqupt.visitor.entity.OperationLog;
import edu.cqupt.visitor.entity.PassCode;
import edu.cqupt.visitor.entity.VisitApply;
import edu.cqupt.visitor.entity.Visitor;
import edu.cqupt.visitor.entity.VisitorCompanion;
import edu.cqupt.visitor.entity.VisitorVehicle;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.security.AuthContext;
import edu.cqupt.visitor.security.CurrentUser;
import edu.cqupt.visitor.service.AccessRecordService;
import edu.cqupt.visitor.service.ApprovalRecordService;
import edu.cqupt.visitor.service.BlacklistService;
import edu.cqupt.visitor.service.OperationLogService;
import edu.cqupt.visitor.service.PassCodeService;
import edu.cqupt.visitor.service.VisitApplyService;
import edu.cqupt.visitor.service.VisitorCompanionService;
import edu.cqupt.visitor.service.VisitorService;
import edu.cqupt.visitor.service.VisitorVehicleService;
import edu.cqupt.visitor.service.VisitorWorkflowService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class VisitorWorkflowServiceImpl implements VisitorWorkflowService {

    private static final DateTimeFormatter NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VisitApplyService visitApplyService;
    private final VisitorService visitorService;
    private final VisitorVehicleService visitorVehicleService;
    private final VisitorCompanionService visitorCompanionService;
    private final ApprovalRecordService approvalRecordService;
    private final PassCodeService passCodeService;
    private final AccessRecordService accessRecordService;
    private final BlacklistService blacklistService;
    private final OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VisitApply submitApplication(VisitApplySubmitRequest request) {
        validateVisitTime(request.getPlanStartTime(), request.getPlanEndTime());
        Blacklist blacklist = findActiveBlacklist(request.getPhone(), request.getIdNumber(), null);
        if (blacklist != null) {
            throw new BusinessException(400, "访客手机号或证件号命中有效黑名单，不能提交预约申请");
        }
        Visitor visitor = findOrCreateVisitor(request);
        VisitorVehicle vehicle = createVehicleIfPresent(visitor.getId(), request.getVehiclePlateNo(), request.getVehicleType(),
                request.getVehicleColor(), request.getVehicleBrand());
        VisitApply apply = new VisitApply();
        apply.setApplyNo(generateApplyNo());
        apply.setVisitorId(visitor.getId());
        apply.setHostUserId(request.getHostUserId());
        apply.setDepartmentId(request.getDepartmentId());
        apply.setVehicleId(vehicle == null ? null : vehicle.getId());
        apply.setVisitReason(request.getVisitReason());
        apply.setPlanStartTime(request.getPlanStartTime());
        apply.setPlanEndTime(request.getPlanEndTime());
        apply.setApplyStatus(WorkflowStatus.APPLY_PENDING_HOST);
        apply.setAccessStatus(WorkflowStatus.ACCESS_NOT_ENTERED);
        apply.setCompanionCount(normalizedCompanions(request.getCompanions()).size());
        apply.setSubmitTime(LocalDateTime.now());
        apply.setRemark(request.getRemark());
        apply.setCreateTime(LocalDateTime.now());
        apply.setUpdateTime(LocalDateTime.now());
        apply.setDeleted(0);
        visitApplyService.save(apply);
        replaceCompanions(apply.getId(), request.getCompanions());
        writeLog("预约申请", "SUBMIT", "/api/workflow/visit-applies", "SUCCESS", "提交预约 " + apply.getApplyNo());
        return apply;
    }

    @Override
    public Page<VisitApply> myApplications(long current, long size, String phone, String idNumber) {
        CurrentUser user = AuthContext.currentUser();
        LambdaQueryWrapper<VisitApply> wrapper = new LambdaQueryWrapper<VisitApply>()
                .eq(VisitApply::getDeleted, 0)
                .orderByDesc(VisitApply::getSubmitTime);
        if (user.hasRole("ADMIN") || user.hasRole("SCHOOL_MANAGER")) {
            return visitApplyService.page(new Page<>(current, size), wrapper);
        }
        if (user.hasRole("HOST")) {
            return visitApplyService.page(new Page<>(current, size), wrapper.eq(VisitApply::getHostUserId, user.getId()));
        }
        if (user.hasRole("DEPT_APPROVER")) {
            return visitApplyService.page(new Page<>(current, size), wrapper.eq(VisitApply::getDepartmentId, user.getDepartmentId()));
        }
        if (!StringUtils.hasText(phone) && !StringUtils.hasText(idNumber) && StringUtils.hasText(user.getPhone())) {
            phone = user.getPhone();
        }
        if (!StringUtils.hasText(phone) && !StringUtils.hasText(idNumber)) {
            throw new BusinessException(400, "查询访客本人预约时需要提供手机号或证件号");
        }
        List<Long> visitorIds = findVisitorIds(phone, idNumber);
        if (visitorIds.isEmpty()) {
            return emptyVisitApplyPage(current, size);
        }
        return visitApplyService.page(new Page<>(current, size), wrapper.in(VisitApply::getVisitorId, visitorIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VisitApply updateUnapproved(Long id, VisitApplyUpdateRequest request) {
        VisitApply apply = requiredApply(id);
        ensureCanModifyVisitorApplication(apply);
        if (!WorkflowStatus.EDITABLE_APPLY_STATUSES.contains(apply.getApplyStatus())) {
            throw new BusinessException(400, "当前预约已审批结束，不能修改");
        }
        LocalDateTime start = request.getPlanStartTime() == null ? apply.getPlanStartTime() : request.getPlanStartTime();
        LocalDateTime end = request.getPlanEndTime() == null ? apply.getPlanEndTime() : request.getPlanEndTime();
        validateVisitTime(start, end);
        if (StringUtils.hasText(request.getVisitReason())) {
            apply.setVisitReason(request.getVisitReason());
        }
        apply.setPlanStartTime(start);
        apply.setPlanEndTime(end);
        if (request.getRemark() != null) {
            apply.setRemark(request.getRemark());
        }
        VisitorVehicle vehicle = updateVehicleIfPresent(apply, request.getVehiclePlateNo(), request.getVehicleType(),
                request.getVehicleColor(), request.getVehicleBrand());
        if (vehicle != null) {
            apply.setVehicleId(vehicle.getId());
        }
        if (request.getCompanions() != null) {
            replaceCompanions(apply.getId(), request.getCompanions());
            apply.setCompanionCount(normalizedCompanions(request.getCompanions()).size());
        }
        apply.setUpdateTime(LocalDateTime.now());
        visitApplyService.updateById(apply);
        writeLog("预约申请", "UPDATE", "/api/workflow/visit-applies/" + id, "SUCCESS", "修改未审批预约 " + apply.getApplyNo());
        return apply;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public VisitApply cancelUnapproved(Long id, String reason) {
        VisitApply apply = requiredApply(id);
        ensureCanModifyVisitorApplication(apply);
        if (!WorkflowStatus.CANCELABLE_APPLY_STATUSES.contains(apply.getApplyStatus())) {
            throw new BusinessException(400, "当前预约不能取消，仅待确认或待审批预约允许取消");
        }
        apply.setApplyStatus(WorkflowStatus.APPLY_CANCELED);
        apply.setAccessStatus(WorkflowStatus.ACCESS_NOT_ENTERED);
        apply.setCancelReason(StringUtils.hasText(reason) ? reason : "用户主动取消");
        apply.setCancelTime(LocalDateTime.now());
        apply.setUpdateTime(LocalDateTime.now());
        visitApplyService.updateById(apply);
        writeLog("预约申请", "CANCEL", "/api/workflow/visit-applies/" + id + "/cancel", "SUCCESS", "取消预约 " + apply.getApplyNo());
        return apply;
    }

    @Override
    public VisitApply detail(Long id) {
        VisitApply apply = requiredApply(id);
        ensureCanViewApplication(apply);
        return apply;
    }

    @Override
    public Page<VisitApply> pendingHostApplications(long current, long size) {
        CurrentUser user = AuthContext.currentUser();
        LambdaQueryWrapper<VisitApply> wrapper = new LambdaQueryWrapper<VisitApply>()
                .eq(VisitApply::getApplyStatus, WorkflowStatus.APPLY_PENDING_HOST)
                .eq(VisitApply::getDeleted, 0)
                .orderByDesc(VisitApply::getSubmitTime);
        if (!user.hasRole("ADMIN")) {
            wrapper.eq(VisitApply::getHostUserId, user.getId());
        }
        return visitApplyService.page(new Page<>(current, size), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VisitApply hostConfirm(Long id, ApprovalDecisionRequest request) {
        VisitApply apply = requiredApply(id);
        CurrentUser user = AuthContext.currentUser();
        if (!user.hasRole("ADMIN") && !Objects.equals(apply.getHostUserId(), user.getId())) {
            throw new BusinessException(403, "只能确认与本人相关的预约申请");
        }
        if (!WorkflowStatus.APPLY_PENDING_HOST.equals(apply.getApplyStatus())) {
            throw new BusinessException(400, "只有待被访人确认的预约可以确认");
        }
        recordApproval(apply.getId(), WorkflowStatus.APPROVAL_STEP_HOST, WorkflowStatus.APPROVAL_RESULT_APPROVED,
                defaultComment(request, "被访人确认接待"));
        apply.setApplyStatus(WorkflowStatus.APPLY_HOST_CONFIRMED);
        apply.setUpdateTime(LocalDateTime.now());
        visitApplyService.updateById(apply);
        writeLog("被访人确认", "CONFIRM", "/api/workflow/host/" + id + "/confirm", "SUCCESS", "被访人确认预约 " + apply.getApplyNo());
        return apply;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VisitApply hostReject(Long id, ApprovalDecisionRequest request) {
        VisitApply apply = requiredApply(id);
        CurrentUser user = AuthContext.currentUser();
        if (!user.hasRole("ADMIN") && !Objects.equals(apply.getHostUserId(), user.getId())) {
            throw new BusinessException(403, "只能拒绝与本人相关的预约申请");
        }
        if (!WorkflowStatus.APPLY_PENDING_HOST.equals(apply.getApplyStatus())) {
            throw new BusinessException(400, "只有待被访人确认的预约可以拒绝");
        }
        recordApproval(apply.getId(), WorkflowStatus.APPROVAL_STEP_HOST, WorkflowStatus.APPROVAL_RESULT_REJECTED,
                defaultComment(request, "被访人拒绝接待"));
        apply.setApplyStatus(WorkflowStatus.APPLY_HOST_REJECTED);
        apply.setAccessStatus(WorkflowStatus.ACCESS_NOT_ENTERED);
        apply.setUpdateTime(LocalDateTime.now());
        visitApplyService.updateById(apply);
        writeLog("被访人确认", "REJECT", "/api/workflow/host/" + id + "/reject", "SUCCESS", "被访人拒绝预约 " + apply.getApplyNo());
        return apply;
    }

    @Override
    public Page<VisitApply> pendingDepartmentApplications(long current, long size) {
        CurrentUser user = AuthContext.currentUser();
        LambdaQueryWrapper<VisitApply> wrapper = new LambdaQueryWrapper<VisitApply>()
                .in(VisitApply::getApplyStatus, WorkflowStatus.DEPARTMENT_PENDING_STATUSES)
                .eq(VisitApply::getDeleted, 0)
                .orderByDesc(VisitApply::getSubmitTime);
        if (!user.hasRole("ADMIN")) {
            wrapper.eq(VisitApply::getDepartmentId, user.getDepartmentId());
        }
        return visitApplyService.page(new Page<>(current, size), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VisitApply departmentApprove(Long id, ApprovalDecisionRequest request) {
        VisitApply apply = requiredApply(id);
        ensureCanApproveDepartment(apply);
        if (!WorkflowStatus.DEPARTMENT_PENDING_STATUSES.contains(apply.getApplyStatus())) {
            throw new BusinessException(400, "只有被访人已确认或待部门审批的预约可以审批通过");
        }
        recordApproval(apply.getId(), WorkflowStatus.APPROVAL_STEP_DEPARTMENT, WorkflowStatus.APPROVAL_RESULT_APPROVED,
                defaultComment(request, "部门审批通过"));
        apply.setApplyStatus(WorkflowStatus.APPLY_APPROVED);
        apply.setAccessStatus(WorkflowStatus.ACCESS_NOT_ENTERED);
        apply.setUpdateTime(LocalDateTime.now());
        visitApplyService.updateById(apply);
        generatePassCode(apply);
        writeLog("部门审批", "APPROVE", "/api/workflow/department/" + id + "/approve", "SUCCESS", "部门审批通过 " + apply.getApplyNo());
        return apply;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VisitApply departmentReject(Long id, ApprovalDecisionRequest request) {
        VisitApply apply = requiredApply(id);
        ensureCanApproveDepartment(apply);
        if (!WorkflowStatus.DEPARTMENT_PENDING_STATUSES.contains(apply.getApplyStatus())) {
            throw new BusinessException(400, "只有被访人已确认或待部门审批的预约可以审批拒绝");
        }
        recordApproval(apply.getId(), WorkflowStatus.APPROVAL_STEP_DEPARTMENT, WorkflowStatus.APPROVAL_RESULT_REJECTED,
                defaultComment(request, "部门审批拒绝"));
        apply.setApplyStatus(WorkflowStatus.APPLY_REJECTED);
        apply.setAccessStatus(WorkflowStatus.ACCESS_NOT_ENTERED);
        apply.setUpdateTime(LocalDateTime.now());
        visitApplyService.updateById(apply);
        writeLog("部门审批", "REJECT", "/api/workflow/department/" + id + "/reject", "SUCCESS", "部门审批拒绝 " + apply.getApplyNo());
        return apply;
    }

    @Override
    public PassCode getPassCode(Long applyId, String applyNo) {
        VisitApply apply = resolveApply(applyId, applyNo);
        if (!AuthContext.currentUser().hasRole("GATE_GUARD")) {
            ensureCanViewApplication(apply);
        }
        if (!WorkflowStatus.APPLY_APPROVED.equals(apply.getApplyStatus())) {
            throw new BusinessException(400, "预约未审批通过，不能查询或生成通行凭证");
        }
        PassCode passCode = findPassCodeByApplyId(apply.getId());
        if (passCode == null) {
            throw new BusinessException(404, "该预约尚未生成通行凭证");
        }
        return passCode;
    }

    @Override
    public GateVerifyResponse verifyAtGate(GateVerifyRequest request) {
        ResolvedGateVisit resolved = resolveGateVisit(request);
        if (resolved.apply == null) {
            return denied("未查询到预约申请或通行凭证");
        }
        return verifyResolvedVisit(resolved);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccessRecord registerEntry(EntryRegisterRequest request) {
        GateVerifyRequest verifyRequest = new GateVerifyRequest();
        verifyRequest.setApplyId(request.getApplyId());
        verifyRequest.setApplyNo(request.getApplyNo());
        verifyRequest.setPassCode(request.getPassCode());
        GateVerifyResponse response = verifyAtGate(verifyRequest);
        if (!Boolean.TRUE.equals(response.getAllowed())) {
            throw new BusinessException(400, response.getMessage());
        }
        VisitApply apply = requiredApply(response.getApplyId());
        PassCode passCode = requiredPassCode(response.getPassCodeId());

        AccessRecord accessRecord = new AccessRecord();
        accessRecord.setApplyId(apply.getId());
        accessRecord.setVisitorId(apply.getVisitorId());
        accessRecord.setPassCodeId(passCode.getId());
        accessRecord.setEntryGateId(request.getGateId());
        accessRecord.setEntryGuardId(AuthContext.currentUser().getId());
        accessRecord.setEntryTime(LocalDateTime.now());
        accessRecord.setAccessStatus(WorkflowStatus.ACCESS_ENTERED);
        accessRecord.setOvertimeFlag(0);
        accessRecord.setCreateTime(LocalDateTime.now());
        accessRecord.setUpdateTime(LocalDateTime.now());
        accessRecord.setDeleted(0);
        accessRecordService.save(accessRecord);

        apply.setAccessStatus(WorkflowStatus.ACCESS_ENTERED);
        apply.setUpdateTime(LocalDateTime.now());
        visitApplyService.updateById(apply);

        passCode.setPassStatus(WorkflowStatus.PASS_USED);
        passCode.setUsedTime(LocalDateTime.now());
        passCode.setVerifyCount(passCode.getVerifyCount() == null ? 1 : passCode.getVerifyCount() + 1);
        passCode.setUpdateTime(LocalDateTime.now());
        passCodeService.updateById(passCode);

        writeLog("门岗核验", "ENTRY", "/api/workflow/access/entry", "SUCCESS", "入校登记 " + apply.getApplyNo());
        return accessRecord;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccessRecord registerExit(ExitRegisterRequest request) {
        AccessRecord accessRecord = resolveAccessRecordForExit(request);
        if (accessRecord.getExitTime() != null || WorkflowStatus.ACCESS_EXITED.equals(accessRecord.getAccessStatus())) {
            throw new BusinessException(400, "该访客已登记离校，不能重复离校");
        }
        VisitApply apply = requiredApply(accessRecord.getApplyId());
        if (!WorkflowStatus.ACCESS_ENTERED.equals(apply.getAccessStatus())
                && !WorkflowStatus.ACCESS_OVERTIME.equals(apply.getAccessStatus())) {
            if (WorkflowStatus.ACCESS_EXITED.equals(apply.getAccessStatus())) {
                throw new BusinessException(400, "该访客已登记离校，不能重复离校");
            }
            throw new BusinessException(400, "该预约未登记入校，不能办理离校");
        }
        accessRecord.setExitGateId(request.getGateId());
        accessRecord.setExitGuardId(AuthContext.currentUser().getId());
        accessRecord.setExitTime(LocalDateTime.now());
        accessRecord.setAccessStatus(WorkflowStatus.ACCESS_EXITED);
        accessRecord.setUpdateTime(LocalDateTime.now());
        accessRecordService.updateById(accessRecord);

        apply.setAccessStatus(WorkflowStatus.ACCESS_EXITED);
        apply.setUpdateTime(LocalDateTime.now());
        visitApplyService.updateById(apply);

        writeLog("出入校登记", "EXIT", "/api/workflow/access/exit", "SUCCESS", "离校登记 " + apply.getApplyNo());
        return accessRecord;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page<AccessRecord> overdueVisitors(long current, long size, boolean mark) {
        List<Long> overdueApplyIds = visitApplyService.list(new LambdaQueryWrapper<VisitApply>()
                        .lt(VisitApply::getPlanEndTime, LocalDateTime.now())
                        .in(VisitApply::getAccessStatus, List.of(WorkflowStatus.ACCESS_ENTERED, WorkflowStatus.ACCESS_OVERTIME))
                        .eq(VisitApply::getDeleted, 0))
                .stream()
                .map(VisitApply::getId)
                .toList();
        if (overdueApplyIds.isEmpty()) {
            return emptyAccessRecordPage(current, size);
        }
        Page<AccessRecord> page = accessRecordService.page(new Page<>(current, size), new LambdaQueryWrapper<AccessRecord>()
                .in(AccessRecord::getApplyId, overdueApplyIds)
                .isNull(AccessRecord::getExitTime)
                .eq(AccessRecord::getDeleted, 0)
                .orderByDesc(AccessRecord::getEntryTime));
        if (mark) {
            for (AccessRecord record : page.getRecords()) {
                if (!WorkflowStatus.ACCESS_OVERTIME.equals(record.getAccessStatus())) {
                    record.setAccessStatus(WorkflowStatus.ACCESS_OVERTIME);
                    record.setOvertimeFlag(1);
                    record.setAbnormalReason("超过预约结束时间仍未登记离校");
                    record.setUpdateTime(LocalDateTime.now());
                    accessRecordService.updateById(record);
                    VisitApply apply = requiredApply(record.getApplyId());
                    apply.setAccessStatus(WorkflowStatus.ACCESS_OVERTIME);
                    apply.setUpdateTime(LocalDateTime.now());
                    visitApplyService.updateById(apply);
                    writeLog("超时未离校", "MARK_OVERTIME", "/api/workflow/access/overtime?mark=true", "SUCCESS",
                            "标记超时未离校 " + apply.getApplyNo());
                }
            }
        }
        return page;
    }

    private Visitor findOrCreateVisitor(VisitApplySubmitRequest request) {
        Visitor visitor = visitorService.getOne(new LambdaQueryWrapper<Visitor>()
                .eq(Visitor::getIdNumber, request.getIdNumber())
                .eq(Visitor::getDeleted, 0), false);
        if (visitor == null) {
            visitor = new Visitor();
            visitor.setVisitorName(request.getVisitorName());
            visitor.setIdType(request.getIdType());
            visitor.setIdNumber(request.getIdNumber());
            visitor.setPhone(request.getPhone());
            visitor.setCompany(request.getCompany());
            visitor.setGender(request.getGender());
            visitor.setVisitorLevel("NORMAL");
            visitor.setStatus("NORMAL");
            visitor.setCreateTime(LocalDateTime.now());
            visitor.setUpdateTime(LocalDateTime.now());
            visitor.setDeleted(0);
            visitorService.save(visitor);
            return visitor;
        }
        visitor.setVisitorName(request.getVisitorName());
        visitor.setPhone(request.getPhone());
        visitor.setCompany(request.getCompany());
        visitor.setGender(request.getGender());
        visitor.setUpdateTime(LocalDateTime.now());
        visitorService.updateById(visitor);
        return visitor;
    }

    private VisitorVehicle createVehicleIfPresent(Long visitorId, String plateNo, String vehicleType, String color, String brand) {
        if (!StringUtils.hasText(plateNo)) {
            return null;
        }
        VisitorVehicle vehicle = visitorVehicleService.getOne(new LambdaQueryWrapper<VisitorVehicle>()
                .eq(VisitorVehicle::getPlateNo, plateNo)
                .eq(VisitorVehicle::getDeleted, 0), false);
        if (vehicle == null) {
            vehicle = new VisitorVehicle();
            vehicle.setVisitorId(visitorId);
            vehicle.setPlateNo(plateNo);
            vehicle.setCreateTime(LocalDateTime.now());
            vehicle.setDeleted(0);
        }
        vehicle.setVehicleType(vehicleType);
        vehicle.setColor(color);
        vehicle.setBrand(brand);
        vehicle.setStatus("NORMAL");
        vehicle.setUpdateTime(LocalDateTime.now());
        if (vehicle.getId() == null) {
            visitorVehicleService.save(vehicle);
        } else {
            visitorVehicleService.updateById(vehicle);
        }
        return vehicle;
    }
    private VisitorVehicle updateVehicleIfPresent(VisitApply apply, String plateNo, String vehicleType, String color, String brand) {
        if (!StringUtils.hasText(plateNo) && apply.getVehicleId() == null) {
            return null;
        }
        VisitorVehicle vehicle = apply.getVehicleId() == null ? null : visitorVehicleService.getById(apply.getVehicleId());
        if (vehicle == null && StringUtils.hasText(plateNo)) {
            vehicle = new VisitorVehicle();
            vehicle.setVisitorId(apply.getVisitorId());
            vehicle.setCreateTime(LocalDateTime.now());
            vehicle.setDeleted(0);
        }
        if (vehicle == null) {
            return null;
        }
        if (StringUtils.hasText(plateNo)) {
            vehicle.setPlateNo(plateNo);
        }
        vehicle.setVehicleType(vehicleType);
        vehicle.setColor(color);
        vehicle.setBrand(brand);
        vehicle.setStatus("NORMAL");
        vehicle.setUpdateTime(LocalDateTime.now());
        if (vehicle.getId() == null) {
            visitorVehicleService.save(vehicle);
        } else {
            visitorVehicleService.updateById(vehicle);
        }
        return vehicle;
    }

    private void replaceCompanions(Long applyId, List<CompanionRequest> companions) {
        visitorCompanionService.remove(new LambdaQueryWrapper<VisitorCompanion>().eq(VisitorCompanion::getApplyId, applyId));
        List<CompanionRequest> normalized = normalizedCompanions(companions);
        if (normalized.isEmpty()) {
            return;
        }
        List<VisitorCompanion> entities = new ArrayList<>();
        for (CompanionRequest item : normalized) {
            VisitorCompanion companion = new VisitorCompanion();
            companion.setApplyId(applyId);
            companion.setCompanionName(item.getCompanionName());
            companion.setIdType(item.getIdType());
            companion.setIdNumber(item.getIdNumber());
            companion.setPhone(item.getPhone());
            companion.setRelationRemark(item.getRelationRemark());
            companion.setCreateTime(LocalDateTime.now());
            companion.setUpdateTime(LocalDateTime.now());
            companion.setDeleted(0);
            entities.add(companion);
        }
        visitorCompanionService.saveBatch(entities);
    }

    private List<CompanionRequest> normalizedCompanions(List<CompanionRequest> companions) {
        if (CollectionUtils.isEmpty(companions)) {
            return List.of();
        }
        return companions.stream()
                .filter(item -> item != null && StringUtils.hasText(item.getCompanionName()))
                .toList();
    }

    private void recordApproval(Long applyId, String step, String result, String comment) {
        ApprovalRecord record = new ApprovalRecord();
        record.setApplyId(applyId);
        record.setApprovalStep(step);
        record.setApproverUserId(AuthContext.currentUser().getId());
        record.setApprovalResult(result);
        record.setApprovalComment(comment);
        record.setApprovalTime(LocalDateTime.now());
        record.setSortOrder((int) approvalRecordService.count(new LambdaQueryWrapper<ApprovalRecord>()
                .eq(ApprovalRecord::getApplyId, applyId)) + 1);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setDeleted(0);
        approvalRecordService.save(record);
    }

    private PassCode generatePassCode(VisitApply apply) {
        PassCode exists = findPassCodeByApplyId(apply.getId());
        if (exists != null) {
            return exists;
        }
        PassCode passCode = new PassCode();
        passCode.setApplyId(apply.getId());
        passCode.setPassCode(generatePassCodeNo());
        passCode.setQrContent("CQUPT:PASS:" + passCode.getPassCode());
        passCode.setValidFrom(apply.getPlanStartTime().minusMinutes(30));
        passCode.setValidTo(apply.getPlanEndTime().plusMinutes(30));
        passCode.setPassStatus(WorkflowStatus.PASS_VALID);
        passCode.setVerifyCount(0);
        passCode.setCreateTime(LocalDateTime.now());
        passCode.setUpdateTime(LocalDateTime.now());
        passCode.setDeleted(0);
        passCodeService.save(passCode);
        return passCode;
    }

    private GateVerifyResponse verifyResolvedVisit(ResolvedGateVisit resolved) {
        VisitApply apply = resolved.apply;
        Visitor visitor = requiredVisitor(apply.getVisitorId());
        PassCode passCode = resolved.passCode == null ? findPassCodeByApplyId(apply.getId()) : resolved.passCode;
        GateVerifyResponse response = baseGateResponse(apply, visitor, passCode);
        Blacklist blacklist = findActiveBlacklist(visitor.getPhone(), visitor.getIdNumber(), visitor.getId());
        if (blacklist != null) {
            response.setAllowed(false);
            response.setMessage("访客命中有效黑名单，禁止入校");
            return response;
        }
        if (!WorkflowStatus.APPLY_APPROVED.equals(apply.getApplyStatus())) {
            response.setAllowed(false);
            response.setMessage("预约未审批通过，不能入校");
            return response;
        }
        if (passCode == null) {
            response.setAllowed(false);
            response.setMessage("审批通过但尚未生成通行凭证");
            return response;
        }
        if (WorkflowStatus.PASS_DISABLED.equals(passCode.getPassStatus())) {
            response.setAllowed(false);
            response.setMessage("通行凭证已停用");
            return response;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(passCode.getValidFrom()) || now.isAfter(passCode.getValidTo())) {
            response.setAllowed(false);
            response.setMessage("通行凭证不在有效期内");
            return response;
        }
        if (WorkflowStatus.ACCESS_EXITED.equals(apply.getAccessStatus())) {
            response.setAllowed(false);
            response.setMessage("该预约已完成离校，不能再次入校");
            return response;
        }
        if (WorkflowStatus.ACCESS_ENTERED.equals(apply.getAccessStatus()) || WorkflowStatus.ACCESS_OVERTIME.equals(apply.getAccessStatus())) {
            response.setAllowed(false);
            response.setMessage("访客已入校或超时未离校，不能重复入校");
            return response;
        }
        AccessRecord active = accessRecordService.getOne(new LambdaQueryWrapper<AccessRecord>()
                .eq(AccessRecord::getApplyId, apply.getId())
                .isNull(AccessRecord::getExitTime)
                .eq(AccessRecord::getDeleted, 0), false);
        if (active != null) {
            response.setAllowed(false);
            response.setMessage("该预约已有未完成出入校记录，不能重复入校");
            return response;
        }
        response.setAllowed(true);
        response.setMessage("核验通过，允许入校");
        return response;
    }

    private GateVerifyResponse baseGateResponse(VisitApply apply, Visitor visitor, PassCode passCode) {
        GateVerifyResponse response = new GateVerifyResponse();
        response.setApplyId(apply.getId());
        response.setApplyNo(apply.getApplyNo());
        response.setVisitorId(visitor.getId());
        response.setVisitorName(visitor.getVisitorName());
        response.setPhone(visitor.getPhone());
        response.setIdNumber(visitor.getIdNumber());
        response.setApplyStatus(apply.getApplyStatus());
        response.setAccessStatus(apply.getAccessStatus());
        response.setPlanStartTime(apply.getPlanStartTime());
        response.setPlanEndTime(apply.getPlanEndTime());
        if (passCode != null) {
            response.setPassCodeId(passCode.getId());
            response.setPassCode(passCode.getPassCode());
            response.setValidFrom(passCode.getValidFrom());
            response.setValidTo(passCode.getValidTo());
        }
        return response;
    }

    private ResolvedGateVisit resolveGateVisit(GateVerifyRequest request) {
        if (request == null) {
            return new ResolvedGateVisit(null, null);
        }
        if (StringUtils.hasText(request.getPassCode())) {
            PassCode passCode = passCodeService.getOne(new LambdaQueryWrapper<PassCode>()
                    .eq(PassCode::getPassCode, request.getPassCode())
                    .eq(PassCode::getDeleted, 0), false);
            if (passCode == null) {
                return new ResolvedGateVisit(null, null);
            }
            return new ResolvedGateVisit(requiredApply(passCode.getApplyId()), passCode);
        }
        if (request.getApplyId() != null || StringUtils.hasText(request.getApplyNo())) {
            return new ResolvedGateVisit(resolveApply(request.getApplyId(), request.getApplyNo()), null);
        }
        if (StringUtils.hasText(request.getPhone()) || StringUtils.hasText(request.getIdNumber())) {
            List<Long> visitorIds = findVisitorIds(request.getPhone(), request.getIdNumber());
            if (visitorIds.isEmpty()) {
                return new ResolvedGateVisit(null, null);
            }
            VisitApply apply = visitApplyService.getOne(new LambdaQueryWrapper<VisitApply>()
                    .in(VisitApply::getVisitorId, visitorIds)
                    .eq(VisitApply::getDeleted, 0)
                    .orderByDesc(VisitApply::getPlanStartTime)
                    .last("LIMIT 1"), false);
            return new ResolvedGateVisit(apply, null);
        }
        return new ResolvedGateVisit(null, null);
    }
    private AccessRecord resolveAccessRecordForExit(ExitRegisterRequest request) {
        if (request.getAccessRecordId() != null) {
            AccessRecord record = accessRecordService.getById(request.getAccessRecordId());
            if (record == null) {
                throw new BusinessException(404, "出入校记录不存在");
            }
            return record;
        }
        VisitApply apply = null;
        if (request.getApplyId() != null || StringUtils.hasText(request.getApplyNo())) {
            apply = resolveApply(request.getApplyId(), request.getApplyNo());
        }
        if (apply == null && StringUtils.hasText(request.getPassCode())) {
            PassCode passCode = passCodeService.getOne(new LambdaQueryWrapper<PassCode>()
                    .eq(PassCode::getPassCode, request.getPassCode())
                    .eq(PassCode::getDeleted, 0), false);
            if (passCode != null) {
                apply = requiredApply(passCode.getApplyId());
            }
        }
        if (apply == null) {
            throw new BusinessException(404, "未查询到可离校登记的预约");
        }
        AccessRecord record = accessRecordService.getOne(new LambdaQueryWrapper<AccessRecord>()
                .eq(AccessRecord::getApplyId, apply.getId())
                .eq(AccessRecord::getDeleted, 0)
                .orderByDesc(AccessRecord::getEntryTime)
                .last("LIMIT 1"), false);
        if (record == null) {
            if (WorkflowStatus.ACCESS_EXITED.equals(apply.getAccessStatus())) {
                throw new BusinessException(400, "该访客已登记离校，不能重复离校");
            }
            throw new BusinessException(400, "该预约尚未登记入校，不能办理离校");
        }
        return record;
    }

    private Blacklist findActiveBlacklist(String phone, String idNumber, Long visitorId) {
        if (!StringUtils.hasText(phone) && !StringUtils.hasText(idNumber) && visitorId == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        return blacklistService.getOne(new LambdaQueryWrapper<Blacklist>()
                .eq(Blacklist::getStatus, WorkflowStatus.BLACKLIST_ACTIVE)
                .le(Blacklist::getStartTime, now)
                .and(wrapper -> wrapper.isNull(Blacklist::getEndTime).or().gt(Blacklist::getEndTime, now))
                .and(wrapper -> {
                    boolean[] hasPrevious = {false};
                    if (visitorId != null) {
                        wrapper.eq(Blacklist::getVisitorId, visitorId);
                        hasPrevious[0] = true;
                    }
                    if (StringUtils.hasText(phone)) {
                        if (hasPrevious[0]) {
                            wrapper.or();
                        }
                        wrapper.eq(Blacklist::getPhone, phone);
                        hasPrevious[0] = true;
                    }
                    if (StringUtils.hasText(idNumber)) {
                        if (hasPrevious[0]) {
                            wrapper.or();
                        }
                        wrapper.eq(Blacklist::getIdNumber, idNumber);
                    }
                })
                .eq(Blacklist::getDeleted, 0)
                .last("LIMIT 1"), false);
    }

    private List<Long> findVisitorIds(String phone, String idNumber) {
        LambdaQueryWrapper<Visitor> wrapper = new LambdaQueryWrapper<Visitor>().eq(Visitor::getDeleted, 0);
        if (StringUtils.hasText(phone) && StringUtils.hasText(idNumber)) {
            wrapper.and(w -> w.eq(Visitor::getPhone, phone).or().eq(Visitor::getIdNumber, idNumber));
        } else if (StringUtils.hasText(phone)) {
            wrapper.eq(Visitor::getPhone, phone);
        } else {
            wrapper.eq(Visitor::getIdNumber, idNumber);
        }
        return visitorService.list(wrapper).stream().map(Visitor::getId).toList();
    }

    private void ensureCanModifyVisitorApplication(VisitApply apply) {
        CurrentUser user = AuthContext.currentUser();
        if (user.hasRole("ADMIN")) {
            return;
        }
        if (user.hasRole("VISITOR") && StringUtils.hasText(user.getPhone())) {
            Visitor visitor = requiredVisitor(apply.getVisitorId());
            if (Objects.equals(visitor.getPhone(), user.getPhone())) {
                return;
            }
        }
        throw new BusinessException(403, "只能修改或取消本人提交且未审批结束的预约");
    }

    private void ensureCanViewApplication(VisitApply apply) {
        CurrentUser user = AuthContext.currentUser();
        if (user.hasRole("ADMIN") || user.hasRole("SCHOOL_MANAGER")) {
            return;
        }
        if (user.hasRole("HOST") && Objects.equals(apply.getHostUserId(), user.getId())) {
            return;
        }
        if (user.hasRole("DEPT_APPROVER") && Objects.equals(apply.getDepartmentId(), user.getDepartmentId())) {
            return;
        }
        if (user.hasRole("VISITOR") && StringUtils.hasText(user.getPhone())) {
            Visitor visitor = requiredVisitor(apply.getVisitorId());
            if (Objects.equals(visitor.getPhone(), user.getPhone())) {
                return;
            }
        }
        throw new BusinessException(403, "无权访问该预约申请");
    }

    private void ensureCanApproveDepartment(VisitApply apply) {
        CurrentUser user = AuthContext.currentUser();
        if (user.hasRole("ADMIN")) {
            return;
        }
        if (user.hasRole("DEPT_APPROVER") && Objects.equals(apply.getDepartmentId(), user.getDepartmentId())) {
            return;
        }
        throw new BusinessException(403, "只能审批本部门相关预约申请");
    }

    private VisitApply requiredApply(Long id) {
        if (id == null) {
            throw new BusinessException(400, "预约编号不能为空");
        }
        VisitApply apply = visitApplyService.getById(id);
        if (apply == null) {
            throw new BusinessException(404, "预约申请不存在");
        }
        return apply;
    }

    private Visitor requiredVisitor(Long id) {
        Visitor visitor = visitorService.getById(id);
        if (visitor == null) {
            throw new BusinessException(404, "访客信息不存在");
        }
        return visitor;
    }

    private PassCode requiredPassCode(Long id) {
        PassCode passCode = passCodeService.getById(id);
        if (passCode == null) {
            throw new BusinessException(404, "通行凭证不存在");
        }
        return passCode;
    }

    private VisitApply resolveApply(Long applyId, String applyNo) {
        if (applyId != null) {
            return requiredApply(applyId);
        }
        if (!StringUtils.hasText(applyNo)) {
            throw new BusinessException(400, "预约主键或预约编号不能为空");
        }
        VisitApply apply = visitApplyService.getOne(new LambdaQueryWrapper<VisitApply>()
                .eq(VisitApply::getApplyNo, applyNo)
                .eq(VisitApply::getDeleted, 0), false);
        if (apply == null) {
            throw new BusinessException(404, "预约申请不存在");
        }
        return apply;
    }

    private PassCode findPassCodeByApplyId(Long applyId) {
        return passCodeService.getOne(new LambdaQueryWrapper<PassCode>()
                .eq(PassCode::getApplyId, applyId)
                .eq(PassCode::getDeleted, 0)
                .orderByDesc(PassCode::getCreateTime)
                .last("LIMIT 1"), false);
    }

    private void validateVisitTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new BusinessException(400, "计划开始时间和结束时间不能为空");
        }
        if (!end.isAfter(start)) {
            throw new BusinessException(400, "计划结束时间必须晚于计划开始时间");
        }
    }

    private String defaultComment(ApprovalDecisionRequest request, String defaultValue) {
        if (request != null && StringUtils.hasText(request.getComment())) {
            return request.getComment();
        }
        return defaultValue;
    }

    private String generateApplyNo() {
        String applyNo;
        do {
            applyNo = "VA" + LocalDateTime.now().format(NO_FORMATTER)
                    + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        } while (visitApplyService.count(new LambdaQueryWrapper<VisitApply>().eq(VisitApply::getApplyNo, applyNo)) > 0);
        return applyNo;
    }

    private String generatePassCodeNo() {
        String passCode;
        do {
            passCode = "PC" + LocalDateTime.now().format(NO_FORMATTER)
                    + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        } while (passCodeService.count(new LambdaQueryWrapper<PassCode>().eq(PassCode::getPassCode, passCode)) > 0);
        return passCode;
    }

    private GateVerifyResponse denied(String message) {
        GateVerifyResponse response = new GateVerifyResponse();
        response.setAllowed(false);
        response.setMessage(message);
        return response;
    }

    private Page<VisitApply> emptyVisitApplyPage(long current, long size) {
        Page<VisitApply> page = new Page<>(current, size);
        page.setRecords(List.of());
        page.setTotal(0);
        return page;
    }

    private Page<AccessRecord> emptyAccessRecordPage(long current, long size) {
        Page<AccessRecord> page = new Page<>(current, size);
        page.setRecords(List.of());
        page.setTotal(0);
        return page;
    }

    private void writeLog(String module, String operationType, String url, String result, String message) {
        CurrentUser user = AuthContext.currentUser();
        OperationLog log = new OperationLog();
        log.setOperatorUserId(user.getId());
        log.setOperatorName(user.getRealName());
        log.setModuleName(module);
        log.setOperationType(operationType);
        log.setRequestMethod("WORKFLOW");
        log.setRequestUrl(url);
        log.setOperationResult(result);
        log.setIpAddress("127.0.0.1");
        log.setOperationTime(LocalDateTime.now());
        log.setErrorMessage(message);
        log.setCreateTime(LocalDateTime.now());
        log.setUpdateTime(LocalDateTime.now());
        log.setDeleted(0);
        operationLogService.save(log);
    }

    private record ResolvedGateVisit(VisitApply apply, PassCode passCode) {
    }
}