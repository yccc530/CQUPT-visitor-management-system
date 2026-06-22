-- 重庆邮电大学智慧访客预约与出入校管理系统
-- Typical SQL query examples for database course report

USE cqupt_visitor_system;
SET NAMES utf8mb4;

-- 1. 查询某访客的历史预约记录
SET @visitor_phone = '13900010001';
SELECT
  va.apply_no,
  v.visitor_name,
  v.phone,
  u.real_name AS host_name,
  d.dept_name,
  va.visit_reason,
  va.plan_start_time,
  va.plan_end_time,
  va.apply_status,
  va.access_status
FROM visit_apply va
JOIN visitor v ON va.visitor_id = v.id
JOIN sys_user u ON va.host_user_id = u.id
JOIN department d ON va.department_id = d.id
WHERE v.phone = @visitor_phone AND va.deleted = 0
ORDER BY va.submit_time DESC;

-- 2. 查询某被访人的待确认预约
SET @host_user_id = 2;
SELECT
  va.id,
  va.apply_no,
  v.visitor_name,
  v.company,
  v.phone,
  va.visit_reason,
  va.plan_start_time,
  va.plan_end_time,
  va.submit_time
FROM visit_apply va
JOIN visitor v ON va.visitor_id = v.id
WHERE va.host_user_id = @host_user_id
  AND va.apply_status = 'PENDING_HOST'
  AND va.deleted = 0
ORDER BY va.submit_time ASC;

-- 3. 查询某部门的待审批预约
SET @department_id = 2;
SELECT
  va.id,
  va.apply_no,
  d.dept_name,
  v.visitor_name,
  u.real_name AS host_name,
  va.visit_reason,
  va.plan_start_time,
  va.plan_end_time,
  va.submit_time
FROM visit_apply va
JOIN visitor v ON va.visitor_id = v.id
JOIN sys_user u ON va.host_user_id = u.id
JOIN department d ON va.department_id = d.id
WHERE va.department_id = @department_id
  AND va.apply_status = 'PENDING_DEPT'
  AND va.deleted = 0
ORDER BY va.submit_time ASC;

-- 4. 查询当天已入校访客
SELECT
  ar.id AS access_record_id,
  va.apply_no,
  v.visitor_name,
  v.phone,
  d.dept_name,
  cg.gate_name AS entry_gate_name,
  guard.real_name AS entry_guard_name,
  ar.entry_time,
  ar.access_status
FROM access_record ar
JOIN visit_apply va ON ar.apply_id = va.id
JOIN visitor v ON ar.visitor_id = v.id
JOIN department d ON va.department_id = d.id
LEFT JOIN campus_gate cg ON ar.entry_gate_id = cg.id
LEFT JOIN sys_user guard ON ar.entry_guard_id = guard.id
WHERE ar.entry_time >= CURRENT_DATE()
  AND ar.entry_time < DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY)
  AND ar.deleted = 0
ORDER BY ar.entry_time DESC;

-- 5. 查询当天已入校但未离校访客
SELECT
  ar.id AS access_record_id,
  va.apply_no,
  v.visitor_name,
  v.phone,
  d.dept_name,
  cg.gate_name AS entry_gate_name,
  ar.entry_time,
  va.plan_end_time,
  TIMESTAMPDIFF(MINUTE, ar.entry_time, NOW()) AS stay_minutes
FROM access_record ar
JOIN visit_apply va ON ar.apply_id = va.id
JOIN visitor v ON ar.visitor_id = v.id
JOIN department d ON va.department_id = d.id
LEFT JOIN campus_gate cg ON ar.entry_gate_id = cg.id
WHERE ar.entry_time >= CURRENT_DATE()
  AND ar.entry_time < DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY)
  AND ar.exit_time IS NULL
  AND ar.deleted = 0
ORDER BY ar.entry_time DESC;

-- 6. 查询超时未离校访客
SELECT
  ar.id AS access_record_id,
  va.apply_no,
  v.visitor_name,
  v.phone,
  d.dept_name,
  va.plan_end_time,
  ar.entry_time,
  TIMESTAMPDIFF(MINUTE, va.plan_end_time, NOW()) AS overtime_minutes,
  ar.abnormal_reason
FROM access_record ar
JOIN visit_apply va ON ar.apply_id = va.id
JOIN visitor v ON ar.visitor_id = v.id
JOIN department d ON va.department_id = d.id
WHERE ar.exit_time IS NULL
  AND va.plan_end_time < NOW()
  AND ar.deleted = 0
ORDER BY overtime_minutes DESC;

-- 7. 按部门统计本月访客数量
SELECT
  d.dept_name,
  COUNT(va.id) AS monthly_apply_count,
  COUNT(DISTINCT va.visitor_id) AS unique_visitor_count,
  SUM(CASE WHEN va.apply_status = 'APPROVED' THEN 1 ELSE 0 END) AS approved_count
FROM department d
LEFT JOIN visit_apply va ON va.department_id = d.id
  AND va.submit_time >= DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01')
  AND va.submit_time < DATE_ADD(DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01'), INTERVAL 1 MONTH)
  AND va.deleted = 0
WHERE d.deleted = 0
GROUP BY d.id, d.dept_name
ORDER BY monthly_apply_count DESC, d.id ASC;

-- 8. 按校门统计通行数量
SELECT
  gate_name,
  SUM(entry_count) AS entry_count,
  SUM(exit_count) AS exit_count,
  SUM(entry_count + exit_count) AS total_passage_count
FROM (
  SELECT cg.gate_name, COUNT(ar.id) AS entry_count, 0 AS exit_count
  FROM campus_gate cg
  LEFT JOIN access_record ar ON ar.entry_gate_id = cg.id AND ar.deleted = 0
  GROUP BY cg.id, cg.gate_name
  UNION ALL
  SELECT cg.gate_name, 0 AS entry_count, COUNT(ar.id) AS exit_count
  FROM campus_gate cg
  LEFT JOIN access_record ar ON ar.exit_gate_id = cg.id AND ar.deleted = 0
  GROUP BY cg.id, cg.gate_name
) gate_stat
GROUP BY gate_name
ORDER BY total_passage_count DESC;

-- 9. 查询黑名单访客
SELECT
  b.id AS blacklist_id,
  COALESCE(v.visitor_name, '未建档访客') AS visitor_name,
  b.id_number,
  b.phone,
  b.reason,
  b.level,
  b.start_time,
  b.end_time,
  b.status,
  u.real_name AS operator_name
FROM blacklist b
LEFT JOIN visitor v ON b.visitor_id = v.id
LEFT JOIN sys_user u ON b.operator_user_id = u.id
WHERE b.status = 'ACTIVE'
  AND b.deleted = 0
ORDER BY b.level DESC, b.start_time DESC;

-- 10. 查询某预约的完整审批记录
SET @apply_no = CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0001');
SELECT
  va.apply_no,
  ar.approval_step,
  ar.approval_result,
  ar.approval_comment,
  ar.approval_time,
  u.real_name AS approver_name,
  d.dept_name AS approver_department
FROM approval_record ar
JOIN visit_apply va ON ar.apply_id = va.id
JOIN sys_user u ON ar.approver_user_id = u.id
LEFT JOIN department d ON u.department_id = d.id
WHERE va.apply_no = @apply_no
  AND ar.deleted = 0
ORDER BY ar.sort_order ASC, ar.approval_time ASC;

-- 11. 查询某访客的车辆和随行人员
SET @apply_id = 1;
SELECT
  va.apply_no,
  v.visitor_name,
  vv.plate_no,
  vv.vehicle_type,
  vv.brand,
  vc.companion_name,
  vc.phone AS companion_phone,
  vc.relation_remark
FROM visit_apply va
JOIN visitor v ON va.visitor_id = v.id
LEFT JOIN visitor_vehicle vv ON va.vehicle_id = vv.id
LEFT JOIN visitor_companion vc ON vc.apply_id = va.id AND vc.deleted = 0
WHERE va.id = @apply_id
  AND va.deleted = 0
ORDER BY vc.id ASC;

-- 12. 查询系统操作日志
SELECT
  ol.id,
  ol.operator_name,
  ol.module_name,
  ol.operation_type,
  ol.request_method,
  ol.request_url,
  ol.operation_result,
  ol.ip_address,
  ol.operation_time,
  ol.error_message
FROM operation_log ol
WHERE ol.operation_time >= DATE_ADD(NOW(), INTERVAL -7 DAY)
  AND ol.deleted = 0
ORDER BY ol.operation_time DESC
LIMIT 50;

-- 13. 查询今日访客概览
SELECT
  COUNT(DISTINCT va.id) AS today_apply_count,
  SUM(CASE WHEN va.apply_status = 'APPROVED' THEN 1 ELSE 0 END) AS today_approved_count,
  COUNT(DISTINCT ar.id) AS today_entry_count,
  SUM(CASE WHEN ar.exit_time IS NULL AND ar.entry_time IS NOT NULL THEN 1 ELSE 0 END) AS current_in_campus_count,
  SUM(CASE WHEN ar.exit_time IS NULL AND va.plan_end_time < NOW() THEN 1 ELSE 0 END) AS overtime_count
FROM visit_apply va
LEFT JOIN access_record ar ON ar.apply_id = va.id AND ar.deleted = 0
WHERE va.submit_time >= CURRENT_DATE()
  AND va.submit_time < DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY)
  AND va.deleted = 0;

-- 14. 查询近七天访客趋势
WITH RECURSIVE recent_days AS (
  SELECT DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY) AS stat_date
  UNION ALL
  SELECT DATE_ADD(stat_date, INTERVAL 1 DAY)
  FROM recent_days
  WHERE stat_date < CURRENT_DATE()
)
SELECT
  rd.stat_date,
  COUNT(va.id) AS apply_count,
  SUM(CASE WHEN va.apply_status = 'APPROVED' THEN 1 ELSE 0 END) AS approved_count,
  COUNT(ar.id) AS entry_count
FROM recent_days rd
LEFT JOIN visit_apply va ON DATE(va.submit_time) = rd.stat_date AND va.deleted = 0
LEFT JOIN access_record ar ON ar.apply_id = va.id AND ar.deleted = 0
GROUP BY rd.stat_date
ORDER BY rd.stat_date ASC;

-- 15. 查询审批通过率
SELECT
  COUNT(*) AS total_finished_approval_count,
  SUM(CASE WHEN approval_result = 'APPROVED' THEN 1 ELSE 0 END) AS approved_count,
  SUM(CASE WHEN approval_result = 'REJECTED' THEN 1 ELSE 0 END) AS rejected_count,
  ROUND(SUM(CASE WHEN approval_result = 'APPROVED' THEN 1 ELSE 0 END) / COUNT(*) * 100, 2) AS approval_rate_percent
FROM approval_record
WHERE approval_result IN ('APPROVED', 'REJECTED')
  AND deleted = 0;
