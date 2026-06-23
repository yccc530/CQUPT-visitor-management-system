package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.constant.WorkflowStatus;
import edu.cqupt.visitor.entity.AccessRecord;
import edu.cqupt.visitor.entity.ApprovalRecord;
import edu.cqupt.visitor.entity.CampusGate;
import edu.cqupt.visitor.entity.Department;
import edu.cqupt.visitor.entity.VisitApply;
import edu.cqupt.visitor.service.AccessRecordService;
import edu.cqupt.visitor.service.ApprovalRecordService;
import edu.cqupt.visitor.service.CampusGateService;
import edu.cqupt.visitor.service.DepartmentService;
import edu.cqupt.visitor.service.VisitApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "统计报表")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    private final VisitApplyService visitApplyService;
    private final AccessRecordService accessRecordService;
    private final ApprovalRecordService approvalRecordService;
    private final DepartmentService departmentService;
    private final CampusGateService campusGateService;

    @Operation(summary = "统计驾驶舱数据")
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("overview", overviewData());
        data.put("trend", trendData(7));
        data.put("departmentRank", departmentRank());
        data.put("gateSummary", gateSummary());
        data.put("approvalRate", approvalRate());
        return ApiResponse.ok(data);
    }

    @Operation(summary = "访客统计概览")
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        return ApiResponse.ok(overviewData());
    }

    @Operation(summary = "近 N 天访客趋势")
    @GetMapping("/trend")
    public ApiResponse<List<Map<String, Object>>> trend(@RequestParam(defaultValue = "7") int days) {
        return ApiResponse.ok(trendData(Math.max(1, Math.min(days, 31))));
    }

    @Operation(summary = "部门访客排行")
    @GetMapping("/department-rank")
    public ApiResponse<List<Map<String, Object>>> departmentRankEndpoint() {
        return ApiResponse.ok(departmentRank());
    }

    @Operation(summary = "校门通行统计")
    @GetMapping("/gate-summary")
    public ApiResponse<List<Map<String, Object>>> gateSummaryEndpoint() {
        return ApiResponse.ok(gateSummary());
    }

    @Operation(summary = "审批通过率")
    @GetMapping("/approval-rate")
    public ApiResponse<Map<String, Object>> approvalRateEndpoint() {
        return ApiResponse.ok(approvalRate());
    }

    private Map<String, Object> overviewData() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        long todayVisitors = visitApplyService.count(new LambdaQueryWrapper<VisitApply>()
                .eq(VisitApply::getDeleted, 0)
                .ge(VisitApply::getPlanStartTime, start)
                .lt(VisitApply::getPlanStartTime, end));
        long currentVisitors = accessRecordService.count(new LambdaQueryWrapper<AccessRecord>()
                .eq(AccessRecord::getDeleted, 0)
                .eq(AccessRecord::getAccessStatus, WorkflowStatus.ACCESS_ENTERED));
        long overtimeVisitors = accessRecordService.count(new LambdaQueryWrapper<AccessRecord>()
                .eq(AccessRecord::getDeleted, 0)
                .eq(AccessRecord::getAccessStatus, WorkflowStatus.ACCESS_OVERTIME));
        long pendingApprovals = visitApplyService.count(new LambdaQueryWrapper<VisitApply>()
                .eq(VisitApply::getDeleted, 0)
                .in(VisitApply::getApplyStatus, List.of(
                        WorkflowStatus.APPLY_PENDING_HOST,
                        WorkflowStatus.APPLY_HOST_CONFIRMED,
                        WorkflowStatus.APPLY_PENDING_DEPT)));
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("todayVisitors", todayVisitors);
        data.put("currentVisitors", currentVisitors);
        data.put("overtimeVisitors", overtimeVisitors);
        data.put("pendingApprovals", pendingApprovals);
        data.put("approvalPassRate", approvalRate().get("passRate"));
        return data;
    }

    private List<Map<String, Object>> trendData(int days) {
        List<Map<String, Object>> data = new ArrayList<>();
        LocalDate first = LocalDate.now().minusDays(days - 1L);
        for (int i = 0; i < days; i++) {
            LocalDate day = first.plusDays(i);
            long count = visitApplyService.count(new LambdaQueryWrapper<VisitApply>()
                    .eq(VisitApply::getDeleted, 0)
                    .ge(VisitApply::getPlanStartTime, day.atStartOfDay())
                    .lt(VisitApply::getPlanStartTime, day.plusDays(1).atStartOfDay()));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", day.format(DAY_FORMATTER));
            row.put("count", count);
            data.add(row);
        }
        return data;
    }

    private List<Map<String, Object>> departmentRank() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        List<Department> departments = departmentService.list(new LambdaQueryWrapper<Department>()
                .eq(Department::getDeleted, 0)
                .ne(Department::getId, 1L)
                .orderByAsc(Department::getSortOrder));
        List<Map<String, Object>> data = new ArrayList<>();
        for (Department department : departments) {
            long count = visitApplyService.count(new LambdaQueryWrapper<VisitApply>()
                    .eq(VisitApply::getDeleted, 0)
                    .eq(VisitApply::getDepartmentId, department.getId())
                    .ge(VisitApply::getPlanStartTime, monthStart));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("departmentId", department.getId());
            row.put("departmentName", department.getDeptName());
            row.put("count", count);
            data.add(row);
        }
        data.sort((a, b) -> Long.compare(((Number) b.get("count")).longValue(), ((Number) a.get("count")).longValue()));
        return data.stream().limit(8).toList();
    }

    private List<Map<String, Object>> gateSummary() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        List<CampusGate> gates = campusGateService.list(new LambdaQueryWrapper<CampusGate>()
                .eq(CampusGate::getDeleted, 0)
                .orderByAsc(CampusGate::getId));
        List<Map<String, Object>> data = new ArrayList<>();
        for (CampusGate gate : gates) {
            long entryCount = accessRecordService.count(new LambdaQueryWrapper<AccessRecord>()
                    .eq(AccessRecord::getDeleted, 0)
                    .eq(AccessRecord::getEntryGateId, gate.getId())
                    .ge(AccessRecord::getEntryTime, monthStart));
            long exitCount = accessRecordService.count(new LambdaQueryWrapper<AccessRecord>()
                    .eq(AccessRecord::getDeleted, 0)
                    .eq(AccessRecord::getExitGateId, gate.getId())
                    .ge(AccessRecord::getExitTime, monthStart));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("gateId", gate.getId());
            row.put("gateName", gate.getGateName());
            row.put("entryCount", entryCount);
            row.put("exitCount", exitCount);
            data.add(row);
        }
        return data;
    }

    private Map<String, Object> approvalRate() {
        long approved = approvalRecordService.count(new LambdaQueryWrapper<ApprovalRecord>()
                .eq(ApprovalRecord::getDeleted, 0)
                .eq(ApprovalRecord::getApprovalStep, WorkflowStatus.APPROVAL_STEP_DEPARTMENT)
                .eq(ApprovalRecord::getApprovalResult, WorkflowStatus.APPROVAL_RESULT_APPROVED));
        long rejected = approvalRecordService.count(new LambdaQueryWrapper<ApprovalRecord>()
                .eq(ApprovalRecord::getDeleted, 0)
                .eq(ApprovalRecord::getApprovalStep, WorkflowStatus.APPROVAL_STEP_DEPARTMENT)
                .eq(ApprovalRecord::getApprovalResult, WorkflowStatus.APPROVAL_RESULT_REJECTED));
        long pending = approvalRecordService.count(new LambdaQueryWrapper<ApprovalRecord>()
                .eq(ApprovalRecord::getDeleted, 0)
                .eq(ApprovalRecord::getApprovalStep, WorkflowStatus.APPROVAL_STEP_DEPARTMENT)
                .eq(ApprovalRecord::getApprovalResult, "PENDING"));
        long total = approved + rejected + pending;
        double passRate = total == 0 ? 0 : Math.round(approved * 10000.0 / total) / 100.0;
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("approved", approved);
        data.put("rejected", rejected);
        data.put("pending", pending);
        data.put("passRate", passRate);
        return data;
    }
}