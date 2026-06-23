package edu.cqupt.visitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.constant.WorkflowStatus;
import edu.cqupt.visitor.entity.AccessRecord;
import edu.cqupt.visitor.entity.ApprovalRecord;
import edu.cqupt.visitor.entity.Blacklist;
import edu.cqupt.visitor.entity.CampusGate;
import edu.cqupt.visitor.entity.Department;
import edu.cqupt.visitor.entity.OperationLog;
import edu.cqupt.visitor.entity.VisitApply;
import edu.cqupt.visitor.service.AccessRecordService;
import edu.cqupt.visitor.service.ApprovalRecordService;
import edu.cqupt.visitor.service.BlacklistService;
import edu.cqupt.visitor.service.CampusGateService;
import edu.cqupt.visitor.service.DepartmentService;
import edu.cqupt.visitor.service.OperationLogService;
import edu.cqupt.visitor.service.VisitApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.DayOfWeek;
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
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final VisitApplyService visitApplyService;
    private final AccessRecordService accessRecordService;
    private final ApprovalRecordService approvalRecordService;
    private final DepartmentService departmentService;
    private final CampusGateService campusGateService;
    private final BlacklistService blacklistService;
    private final OperationLogService operationLogService;

    @Operation(summary = "统计驾驶舱数据")
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("overview", overviewData());
        data.put("trend", trendData(7));
        data.put("departmentRank", departmentRank());
        data.put("gateSummary", gateSummary());
        data.put("approvalRate", approvalRate());
        data.put("reasonDistribution", reasonDistributionData());
        data.put("approvalStatusDistribution", approvalStatusDistributionData());
        data.put("accessStatusDistribution", accessStatusDistributionData());
        data.put("blacklistRisk", blacklistRiskData());
        data.put("recentLogs", recentLogsData());
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

    @Operation(summary = "访问事由分布")
    @GetMapping("/reason-distribution")
    public ApiResponse<List<Map<String, Object>>> reasonDistribution() {
        return ApiResponse.ok(reasonDistributionData());
    }

    @Operation(summary = "审批状态分布")
    @GetMapping("/approval-status-distribution")
    public ApiResponse<List<Map<String, Object>>> approvalStatusDistribution() {
        return ApiResponse.ok(approvalStatusDistributionData());
    }

    @Operation(summary = "入校离校状态分布")
    @GetMapping("/access-status-distribution")
    public ApiResponse<List<Map<String, Object>>> accessStatusDistribution() {
        return ApiResponse.ok(accessStatusDistributionData());
    }

    @Operation(summary = "黑名单风险统计")
    @GetMapping("/blacklist-risk")
    public ApiResponse<Map<String, Object>> blacklistRisk() {
        return ApiResponse.ok(blacklistRiskData());
    }

    @Operation(summary = "最近操作日志")
    @GetMapping("/recent-logs")
    public ApiResponse<List<Map<String, Object>>> recentLogs() {
        return ApiResponse.ok(recentLogsData());
    }

    private Map<String, Object> overviewData() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        LocalDateTime weekStart = today.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime weekEnd = today.with(DayOfWeek.MONDAY).plusDays(7).atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = today.withDayOfMonth(1).plusMonths(1).atStartOfDay();

        long todayVisitors = countApplyBetween(todayStart, todayEnd);
        long thisWeekVisitors = countApplyBetween(weekStart, weekEnd);
        long thisMonthVisitors = countApplyBetween(monthStart, monthEnd);
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
        long blacklistRiskCount = blacklistService.count(new LambdaQueryWrapper<Blacklist>()
                .eq(Blacklist::getDeleted, 0)
                .eq(Blacklist::getStatus, WorkflowStatus.BLACKLIST_ACTIVE));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("todayVisitors", todayVisitors);
        data.put("thisWeekVisitors", thisWeekVisitors);
        data.put("thisMonthVisitors", thisMonthVisitors);
        data.put("currentVisitors", currentVisitors);
        data.put("overtimeVisitors", overtimeVisitors);
        data.put("pendingApprovals", pendingApprovals);
        data.put("blacklistRiskCount", blacklistRiskCount);
        data.put("approvalPassRate", approvalRate().get("passRate"));
        return data;
    }

    private long countApplyBetween(LocalDateTime start, LocalDateTime end) {
        return visitApplyService.count(new LambdaQueryWrapper<VisitApply>()
                .eq(VisitApply::getDeleted, 0)
                .ge(VisitApply::getPlanStartTime, start)
                .lt(VisitApply::getPlanStartTime, end));
    }

    private List<Map<String, Object>> trendData(int days) {
        List<Map<String, Object>> data = new ArrayList<>();
        LocalDate first = LocalDate.now().minusDays(days - 1L);
        for (int i = 0; i < days; i++) {
            LocalDate day = first.plusDays(i);
            long count = countApplyBetween(day.atStartOfDay(), day.plusDays(1).atStartOfDay());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", day.format(DAY_FORMATTER));
            row.put("count", count);
            data.add(row);
        }
        return data;
    }

    private List<Map<String, Object>> departmentRank() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDate.now().withDayOfMonth(1).plusMonths(1).atStartOfDay();
        List<Department> departments = departmentService.list(new LambdaQueryWrapper<Department>()
                .eq(Department::getDeleted, 0)
                .ne(Department::getId, 1L)
                .orderByAsc(Department::getSortOrder));
        List<Map<String, Object>> data = new ArrayList<>();
        for (Department department : departments) {
            long count = visitApplyService.count(new LambdaQueryWrapper<VisitApply>()
                    .eq(VisitApply::getDeleted, 0)
                    .eq(VisitApply::getDepartmentId, department.getId())
                    .ge(VisitApply::getPlanStartTime, monthStart)
                    .lt(VisitApply::getPlanStartTime, monthEnd));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("departmentId", department.getId());
            row.put("departmentName", department.getDeptName());
            row.put("count", count);
            data.add(row);
        }
        data.sort((a, b) -> Long.compare(((Number) b.get("count")).longValue(), ((Number) a.get("count")).longValue()));
        return data.stream().limit(10).toList();
    }

    private List<Map<String, Object>> gateSummary() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDate.now().withDayOfMonth(1).plusMonths(1).atStartOfDay();
        List<CampusGate> gates = campusGateService.list(new LambdaQueryWrapper<CampusGate>()
                .eq(CampusGate::getDeleted, 0)
                .orderByAsc(CampusGate::getId));
        List<Map<String, Object>> data = new ArrayList<>();
        for (CampusGate gate : gates) {
            long entryCount = accessRecordService.count(new LambdaQueryWrapper<AccessRecord>()
                    .eq(AccessRecord::getDeleted, 0)
                    .eq(AccessRecord::getEntryGateId, gate.getId())
                    .ge(AccessRecord::getEntryTime, monthStart)
                    .lt(AccessRecord::getEntryTime, monthEnd));
            long exitCount = accessRecordService.count(new LambdaQueryWrapper<AccessRecord>()
                    .eq(AccessRecord::getDeleted, 0)
                    .eq(AccessRecord::getExitGateId, gate.getId())
                    .ge(AccessRecord::getExitTime, monthStart)
                    .lt(AccessRecord::getExitTime, monthEnd));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("gateId", gate.getId());
            row.put("gateName", gate.getGateName());
            row.put("entryCount", entryCount);
            row.put("exitCount", exitCount);
            row.put("totalCount", entryCount + exitCount);
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
                .in(ApprovalRecord::getApprovalResult, List.of(WorkflowStatus.APPROVAL_RESULT_REJECTED, "RETURNED")));
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

    private List<Map<String, Object>> reasonDistributionData() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDate.now().withDayOfMonth(1).plusMonths(1).atStartOfDay();
        Map<String, Long> buckets = new LinkedHashMap<>();
        for (String name : List.of("学生家长", "企业合作", "后勤维修", "学术交流", "面试招聘", "快递配送", "校友返校", "外聘专家", "其他")) {
            buckets.put(name, 0L);
        }
        List<VisitApply> applies = visitApplyService.list(new LambdaQueryWrapper<VisitApply>()
                .eq(VisitApply::getDeleted, 0)
                .ge(VisitApply::getPlanStartTime, monthStart)
                .lt(VisitApply::getPlanStartTime, monthEnd));
        for (VisitApply apply : applies) {
            String bucket = reasonBucket(apply.getVisitReason());
            buckets.put(bucket, buckets.get(bucket) + 1);
        }
        List<Map<String, Object>> data = new ArrayList<>();
        buckets.forEach((name, count) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("reasonType", name);
            row.put("count", count);
            data.add(row);
        });
        return data;
    }

    private String reasonBucket(String reason) {
        String text = reason == null ? "" : reason;
        if (text.contains("家长") || text.contains("学籍")) return "学生家长";
        if (text.contains("企业") || text.contains("合作")) return "企业合作";
        if (text.contains("维修") || text.contains("后勤")) return "后勤维修";
        if (text.contains("学术") || text.contains("研讨")) return "学术交流";
        if (text.contains("面试") || text.contains("就业")) return "面试招聘";
        if (text.contains("快递") || text.contains("配送")) return "快递配送";
        if (text.contains("校友")) return "校友返校";
        if (text.contains("专家") || text.contains("评审") || text.contains("讲座")) return "外聘专家";
        return "其他";
    }

    private List<Map<String, Object>> approvalStatusDistributionData() {
        List<String> statuses = List.of(
                WorkflowStatus.APPLY_PENDING_HOST,
                WorkflowStatus.APPLY_HOST_CONFIRMED,
                WorkflowStatus.APPLY_PENDING_DEPT,
                WorkflowStatus.APPLY_APPROVED,
                WorkflowStatus.APPLY_REJECTED,
                WorkflowStatus.APPLY_HOST_REJECTED,
                WorkflowStatus.APPLY_CANCELED,
                WorkflowStatus.APPLY_REJECTED_BLACKLIST);
        Map<String, String> labels = Map.of(
                WorkflowStatus.APPLY_PENDING_HOST, "待被访人确认",
                WorkflowStatus.APPLY_HOST_CONFIRMED, "被访人已确认",
                WorkflowStatus.APPLY_PENDING_DEPT, "待部门审批",
                WorkflowStatus.APPLY_APPROVED, "审批通过",
                WorkflowStatus.APPLY_REJECTED, "审批拒绝",
                WorkflowStatus.APPLY_HOST_REJECTED, "被访人已拒绝",
                WorkflowStatus.APPLY_CANCELED, "已取消",
                WorkflowStatus.APPLY_REJECTED_BLACKLIST, "黑名单拦截");
        List<Map<String, Object>> data = new ArrayList<>();
        for (String status : statuses) {
            long count = visitApplyService.count(new LambdaQueryWrapper<VisitApply>()
                    .eq(VisitApply::getDeleted, 0)
                    .eq(VisitApply::getApplyStatus, status));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("status", status);
            row.put("statusName", labels.get(status));
            row.put("count", count);
            data.add(row);
        }
        return data;
    }

    private List<Map<String, Object>> accessStatusDistributionData() {
        List<String> statuses = List.of(
                WorkflowStatus.ACCESS_NOT_ENTERED,
                WorkflowStatus.ACCESS_ENTERED,
                WorkflowStatus.ACCESS_EXITED,
                WorkflowStatus.ACCESS_OVERTIME,
                WorkflowStatus.ACCESS_ABNORMAL);
        Map<String, String> labels = Map.of(
                WorkflowStatus.ACCESS_NOT_ENTERED, "未入校",
                WorkflowStatus.ACCESS_ENTERED, "已入校",
                WorkflowStatus.ACCESS_EXITED, "已离校",
                WorkflowStatus.ACCESS_OVERTIME, "超时未离校",
                WorkflowStatus.ACCESS_ABNORMAL, "异常处理");
        List<Map<String, Object>> data = new ArrayList<>();
        for (String status : statuses) {
            long count = visitApplyService.count(new LambdaQueryWrapper<VisitApply>()
                    .eq(VisitApply::getDeleted, 0)
                    .eq(VisitApply::getAccessStatus, status));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("status", status);
            row.put("statusName", labels.get(status));
            row.put("count", count);
            data.add(row);
        }
        return data;
    }

    private Map<String, Object> blacklistRiskData() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalCount", blacklistService.count(new LambdaQueryWrapper<Blacklist>().eq(Blacklist::getDeleted, 0)));
        data.put("activeCount", blacklistService.count(new LambdaQueryWrapper<Blacklist>()
                .eq(Blacklist::getDeleted, 0)
                .eq(Blacklist::getStatus, WorkflowStatus.BLACKLIST_ACTIVE)));

        List<Map<String, Object>> byLevel = new ArrayList<>();
        Map<String, String> levelLabels = Map.of("WARNING", "提醒关注", "LIMITED", "限制通行", "FORBIDDEN", "禁止通行");
        for (String level : List.of("WARNING", "LIMITED", "FORBIDDEN")) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("level", level);
            row.put("levelName", levelLabels.get(level));
            row.put("count", blacklistService.count(new LambdaQueryWrapper<Blacklist>()
                    .eq(Blacklist::getDeleted, 0)
                    .eq(Blacklist::getLevel, level)));
            byLevel.add(row);
        }
        data.put("byLevel", byLevel);

        List<Map<String, Object>> byStatus = new ArrayList<>();
        Map<String, String> statusLabels = Map.of("ACTIVE", "生效中", "EXPIRED", "已失效", "REMOVED", "已移除");
        for (String status : List.of("ACTIVE", "EXPIRED", "REMOVED")) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("status", status);
            row.put("statusName", statusLabels.get(status));
            row.put("count", blacklistService.count(new LambdaQueryWrapper<Blacklist>()
                    .eq(Blacklist::getDeleted, 0)
                    .eq(Blacklist::getStatus, status)));
            byStatus.add(row);
        }
        data.put("byStatus", byStatus);
        return data;
    }

    private List<Map<String, Object>> recentLogsData() {
        List<OperationLog> logs = operationLogService.list(new LambdaQueryWrapper<OperationLog>()
                .eq(OperationLog::getDeleted, 0)
                .orderByDesc(OperationLog::getOperationTime)
                .last("LIMIT 10"));
        List<Map<String, Object>> data = new ArrayList<>();
        for (OperationLog log : logs) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("operatorName", log.getOperatorName());
            row.put("moduleName", log.getModuleName());
            row.put("operationType", log.getOperationType());
            row.put("operationResult", log.getOperationResult());
            row.put("requestUrl", log.getRequestUrl());
            row.put("operationTime", log.getOperationTime() == null ? null : log.getOperationTime().format(TIME_FORMATTER));
            data.add(row);
        }
        return data;
    }
}
