-- 重庆邮电大学智慧访客预约与出入校管理系统典型 SQL 查询
-- 说明：以下 SQL 均基于 MySQL 8，日期统计使用 CURRENT_DATE()/NOW() 动态计算，便于长期演示。
USE cqupt_visitor_system;
SET NAMES utf8mb4;

-- 1. 查询某访客的历史预约记录。
SELECT
  v.visitor_name,
  v.phone,
  va.apply_no,
  va.visit_reason,
  d.dept_name,
  u.real_name AS host_name,
  va.plan_start_time,
  va.plan_end_time,
  va.apply_status,
  va.access_status
FROM visit_apply va
JOIN visitor v ON va.visitor_id = v.id
LEFT JOIN department d ON va.department_id = d.id
LEFT JOIN sys_user u ON va.host_user_id = u.id
WHERE va.deleted = 0 AND v.phone = '13950000001'
ORDER BY va.plan_start_time DESC;

-- 2. 查询某被访人的待确认预约。
SELECT va.apply_no, v.visitor_name, v.phone, va.visit_reason, va.plan_start_time, va.apply_status
FROM visit_apply va
JOIN visitor v ON va.visitor_id = v.id
JOIN sys_user host ON va.host_user_id = host.id
WHERE va.deleted = 0
  AND host.username = 'teacher01'
  AND va.apply_status = 'PENDING_HOST'
ORDER BY va.plan_start_time ASC;

-- 3. 查询某部门的待审批预约。
SELECT va.apply_no, d.dept_name, v.visitor_name, host.real_name AS host_name, va.visit_reason, va.plan_start_time, va.apply_status
FROM visit_apply va
JOIN visitor v ON va.visitor_id = v.id
JOIN department d ON va.department_id = d.id
LEFT JOIN sys_user host ON va.host_user_id = host.id
WHERE va.deleted = 0
  AND d.dept_name = '信息与通信工程学院'
  AND va.apply_status IN ('HOST_CONFIRMED', 'PENDING_DEPT')
ORDER BY va.plan_start_time ASC;

-- 4. 查询当天已入校访客。
SELECT ar.id, va.apply_no, v.visitor_name, v.phone, g.gate_name AS entry_gate, guard.real_name AS guard_name, ar.entry_time
FROM access_record ar
JOIN visit_apply va ON ar.apply_id = va.id
JOIN visitor v ON ar.visitor_id = v.id
LEFT JOIN campus_gate g ON ar.entry_gate_id = g.id
LEFT JOIN sys_user guard ON ar.entry_guard_id = guard.id
WHERE ar.deleted = 0
  AND ar.entry_time >= CURRENT_DATE()
  AND ar.entry_time < DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY)
ORDER BY ar.entry_time DESC;

-- 5. 查询当天已入校但未离校访客。
SELECT ar.id, va.apply_no, v.visitor_name, v.phone, d.dept_name, ar.entry_time, ar.access_status
FROM access_record ar
JOIN visit_apply va ON ar.apply_id = va.id
JOIN visitor v ON ar.visitor_id = v.id
LEFT JOIN department d ON va.department_id = d.id
WHERE ar.deleted = 0
  AND ar.access_status = 'ENTERED'
  AND ar.entry_time >= CURRENT_DATE()
  AND ar.entry_time < DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY)
ORDER BY ar.entry_time DESC;

-- 6. 查询超时未离校访客。
SELECT va.apply_no, v.visitor_name, v.phone, va.plan_end_time, ar.entry_time, ar.overtime_flag, ar.abnormal_reason
FROM access_record ar
JOIN visit_apply va ON ar.apply_id = va.id
JOIN visitor v ON ar.visitor_id = v.id
WHERE ar.deleted = 0
  AND (ar.access_status = 'OVERTIME' OR (ar.exit_time IS NULL AND va.plan_end_time < NOW()))
ORDER BY va.plan_end_time ASC;

-- 7. 按部门统计本月访客数量。
SELECT d.dept_name, COUNT(*) AS visitor_count
FROM visit_apply va
JOIN department d ON va.department_id = d.id
WHERE va.deleted = 0
  AND va.plan_start_time >= DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')
  AND va.plan_start_time < DATE_ADD(DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01'), INTERVAL 1 MONTH)
GROUP BY d.id, d.dept_name
ORDER BY visitor_count DESC;

-- 8. 按校门统计本月通行数量。
SELECT g.gate_name,
       SUM(CASE WHEN ar.entry_gate_id = g.id THEN 1 ELSE 0 END) AS entry_count,
       SUM(CASE WHEN ar.exit_gate_id = g.id THEN 1 ELSE 0 END) AS exit_count,
       SUM(CASE WHEN ar.entry_gate_id = g.id THEN 1 ELSE 0 END) + SUM(CASE WHEN ar.exit_gate_id = g.id THEN 1 ELSE 0 END) AS total_count
FROM campus_gate g
LEFT JOIN access_record ar ON ar.deleted = 0
  AND (ar.entry_gate_id = g.id OR ar.exit_gate_id = g.id)
  AND COALESCE(ar.entry_time, ar.exit_time) >= DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')
  AND COALESCE(ar.entry_time, ar.exit_time) < DATE_ADD(DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01'), INTERVAL 1 MONTH)
WHERE g.deleted = 0
GROUP BY g.id, g.gate_name
ORDER BY total_count DESC;

-- 9. 查询黑名单访客。
SELECT b.id, v.visitor_name, b.phone, b.id_number, b.reason, b.level, b.status, b.start_time, b.end_time
FROM blacklist b
LEFT JOIN visitor v ON b.visitor_id = v.id
WHERE b.deleted = 0 AND b.status = 'ACTIVE'
ORDER BY b.level DESC, b.start_time DESC;

-- 10. 查询某预约的完整审批记录。
SELECT va.apply_no, ar.approval_step, approver.real_name AS approver_name, ar.approval_result, ar.approval_comment, ar.approval_time
FROM approval_record ar
JOIN visit_apply va ON ar.apply_id = va.id
LEFT JOIN sys_user approver ON ar.approver_user_id = approver.id
WHERE ar.deleted = 0 AND va.apply_no = (SELECT apply_no FROM visit_apply ORDER BY id LIMIT 1)
ORDER BY ar.sort_order ASC, ar.approval_time ASC;

-- 11. 查询某访客的车辆和随行人员。
SELECT va.apply_no, v.visitor_name, vv.plate_no, vv.vehicle_type, vv.color, vc.companion_name, vc.phone AS companion_phone, vc.relation_remark
FROM visit_apply va
JOIN visitor v ON va.visitor_id = v.id
LEFT JOIN visitor_vehicle vv ON va.vehicle_id = vv.id
LEFT JOIN visitor_companion vc ON vc.apply_id = va.id AND vc.deleted = 0
WHERE va.deleted = 0 AND va.id = 2;

-- 12. 查询系统操作日志。
SELECT operator_name, module_name, operation_type, request_method, request_url, operation_result, ip_address, operation_time
FROM operation_log
WHERE deleted = 0
ORDER BY operation_time DESC
LIMIT 20;

-- 13. 查询今日访客概览。
SELECT
  (SELECT COUNT(*) FROM visit_apply WHERE deleted = 0 AND plan_start_time >= CURRENT_DATE() AND plan_start_time < DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY)) AS today_visitors,
  (SELECT COUNT(*) FROM access_record WHERE deleted = 0 AND access_status = 'ENTERED') AS current_visitors,
  (SELECT COUNT(*) FROM access_record WHERE deleted = 0 AND access_status = 'OVERTIME') AS overtime_visitors,
  (SELECT COUNT(*) FROM visit_apply WHERE deleted = 0 AND apply_status IN ('PENDING_HOST','HOST_CONFIRMED','PENDING_DEPT')) AS pending_approvals,
  (SELECT COUNT(*) FROM blacklist WHERE deleted = 0 AND status = 'ACTIVE') AS blacklist_risks;

-- 14. 查询近七天访客趋势。
WITH RECURSIVE days AS (
  SELECT DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY) AS stat_date
  UNION ALL
  SELECT DATE_ADD(stat_date, INTERVAL 1 DAY) FROM days WHERE stat_date < CURRENT_DATE()
)
SELECT DATE_FORMAT(days.stat_date, '%m-%d') AS stat_date,
       COUNT(va.id) AS visitor_count
FROM days
LEFT JOIN visit_apply va ON va.deleted = 0
  AND va.plan_start_time >= days.stat_date
  AND va.plan_start_time < DATE_ADD(days.stat_date, INTERVAL 1 DAY)
GROUP BY days.stat_date
ORDER BY days.stat_date;

-- 15. 查询审批通过率。
SELECT
  SUM(CASE WHEN approval_result = 'APPROVED' THEN 1 ELSE 0 END) AS approved_count,
  SUM(CASE WHEN approval_result IN ('REJECTED','RETURNED') THEN 1 ELSE 0 END) AS rejected_count,
  SUM(CASE WHEN approval_result = 'PENDING' THEN 1 ELSE 0 END) AS pending_count,
  ROUND(SUM(CASE WHEN approval_result = 'APPROVED' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS pass_rate
FROM approval_record
WHERE deleted = 0 AND approval_step = 'DEPT_APPROVAL';

-- 16. 查询本周访客数。
SELECT COUNT(*) AS this_week_visitors
FROM visit_apply
WHERE deleted = 0
  AND plan_start_time >= DATE_SUB(CURRENT_DATE(), INTERVAL WEEKDAY(CURRENT_DATE()) DAY)
  AND plan_start_time < DATE_ADD(DATE_SUB(CURRENT_DATE(), INTERVAL WEEKDAY(CURRENT_DATE()) DAY), INTERVAL 7 DAY);

-- 17. 查询本月访客数。
SELECT COUNT(*) AS this_month_visitors
FROM visit_apply
WHERE deleted = 0
  AND plan_start_time >= DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')
  AND plan_start_time < DATE_ADD(DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01'), INTERVAL 1 MONTH);

-- 18. 访问事由分布。
SELECT reason_type, COUNT(*) AS reason_count
FROM (
  SELECT CASE
    WHEN visit_reason LIKE '%家长%' OR visit_reason LIKE '%学籍%' THEN '学生家长'
    WHEN visit_reason LIKE '%企业%' OR visit_reason LIKE '%合作%' THEN '企业合作'
    WHEN visit_reason LIKE '%维修%' OR visit_reason LIKE '%后勤%' THEN '后勤维修'
    WHEN visit_reason LIKE '%学术%' OR visit_reason LIKE '%研讨%' THEN '学术交流'
    WHEN visit_reason LIKE '%面试%' OR visit_reason LIKE '%就业%' THEN '面试招聘'
    WHEN visit_reason LIKE '%快递%' OR visit_reason LIKE '%配送%' THEN '快递配送'
    WHEN visit_reason LIKE '%校友%' THEN '校友返校'
    WHEN visit_reason LIKE '%专家%' OR visit_reason LIKE '%评审%' OR visit_reason LIKE '%讲座%' THEN '外聘专家'
    ELSE '其他'
  END AS reason_type
  FROM visit_apply
  WHERE deleted = 0
    AND plan_start_time >= DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')
    AND plan_start_time < DATE_ADD(DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01'), INTERVAL 1 MONTH)
) t
GROUP BY reason_type
ORDER BY reason_count DESC;

-- 19. 审批状态分布。
SELECT apply_status, COUNT(*) AS status_count
FROM visit_apply
WHERE deleted = 0
GROUP BY apply_status
ORDER BY status_count DESC;

-- 20. 入校离校状态分布。
SELECT access_status, COUNT(*) AS status_count
FROM visit_apply
WHERE deleted = 0
GROUP BY access_status
ORDER BY status_count DESC;

-- 21. 黑名单风险数量。
SELECT level, status, COUNT(*) AS risk_count
FROM blacklist
WHERE deleted = 0
GROUP BY level, status
ORDER BY risk_count DESC;

-- 22. 最近操作日志。
SELECT operator_name, module_name, operation_type, operation_result, request_url, operation_time
FROM operation_log
WHERE deleted = 0
ORDER BY operation_time DESC
LIMIT 10;
