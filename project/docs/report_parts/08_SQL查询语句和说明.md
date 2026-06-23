# 08 SQL 查询语句和说明

本节根据“重庆邮电大学智慧访客预约与出入校管理系统”的数据库逻辑结构，选取预约、审批、通行、黑名单、统计报表和系统审计等核心场景，给出典型 SQL 查询语句。SQL 脚本对应文件为 `database/query_examples.sql`，数据库名为 `cqupt_visitor_system`。

## 1. 查询某访客的历史预约记录

查询目的：根据访客手机号查询该访客的历史预约、被访人、访问部门、预约状态和通行状态。  
涉及数据表：`visit_apply`、`visitor`、`sys_user`、`department`。  
SQL 语句：

```sql
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
```

查询结果说明：返回该访客所有未删除预约记录，可用于“我的预约”和“访客记录查询”页面。

## 2. 查询某被访人的待确认预约

查询目的：查询指定被访人需要处理的预约申请。  
涉及数据表：`visit_apply`、`visitor`。  
SQL 语句：

```sql
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
```

查询结果说明：返回待确认预约列表，被访人可据此进行同意或拒绝操作。

## 3. 查询某部门的待审批预约

查询目的：查询某学院或部门需要审批的预约申请。  
涉及数据表：`visit_apply`、`visitor`、`sys_user`、`department`。  
SQL 语句：

```sql
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
  AND va.apply_status IN ('HOST_CONFIRMED', 'PENDING_DEPT')
  AND va.deleted = 0
ORDER BY va.submit_time ASC;
```

查询结果说明：返回部门审批人员待办数据，用于“部门审批页”。

## 4. 查询当天已入校访客

查询目的：查询当天已完成入校登记的访客及其入校校门、经办安保人员。  
涉及数据表：`access_record`、`visit_apply`、`visitor`、`department`、`campus_gate`、`sys_user`。  
SQL 语句：

```sql
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
```

查询结果说明：返回今日所有入校记录，可用于门岗登记记录和首页今日入校统计。

## 5. 查询当天已入校但未离校访客

查询目的：查询今日仍在校内的访客。  
涉及数据表：`access_record`、`visit_apply`、`visitor`、`department`、`campus_gate`。  
SQL 语句：

```sql
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
```

查询结果说明：返回当前在校访客及停留时长，用于“当前在校访客页”。

## 6. 查询超时未离校访客

查询目的：识别超过预约结束时间仍未登记离校的访客。  
涉及数据表：`access_record`、`visit_apply`、`visitor`、`department`。  
SQL 语句：

```sql
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
```

查询结果说明：返回超时分钟数和异常原因，用于“超时未离校访客页”和安全提醒。

## 7. 按部门统计本月访客数量

查询目的：统计本月各部门预约量、独立访客数和审批通过数量。  
涉及数据表：`department`、`visit_apply`。  
SQL 语句：

```sql
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
```

查询结果说明：返回部门维度排行数据，用于统计报表中的“部门访客排行”。

## 8. 按校门统计通行数量

查询目的：统计各校门入校、离校和总通行次数。  
涉及数据表：`campus_gate`、`access_record`。  
SQL 语句：

```sql
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
```

查询结果说明：返回校门通行排行，可用于门岗通行统计图表。

## 9. 查询黑名单访客

查询目的：查询当前生效的黑名单人员。  
涉及数据表：`blacklist`、`visitor`、`sys_user`。  
SQL 语句：

```sql
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
```

查询结果说明：返回黑名单等级、原因和操作人，用于门岗核验和黑名单管理。

## 10. 查询某预约的完整审批记录

查询目的：查看一个预约从被访人确认到部门审批的完整流程。  
涉及数据表：`approval_record`、`visit_apply`、`sys_user`、`department`。  
SQL 语句：

```sql
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
```

查询结果说明：返回审批节点、结果、意见和审批人，可用于预约详情页。

## 11. 查询某访客的车辆和随行人员

查询目的：查询预约关联的车辆信息和随行人员明细。  
涉及数据表：`visit_apply`、`visitor`、`visitor_vehicle`、`visitor_companion`。  
SQL 语句：

```sql
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
```

查询结果说明：返回车辆和随行人员列表，用于预约详情和门岗核验。

## 12. 查询系统操作日志

查询目的：查询最近七天系统操作日志，辅助系统审计。  
涉及数据表：`operation_log`。  
SQL 语句：

```sql
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
```

查询结果说明：返回最近操作记录，可用于“系统日志页”。

## 13. 查询今日访客概览

查询目的：汇总今日预约、审批通过、入校、当前在校和超时未离校指标。  
涉及数据表：`visit_apply`、`access_record`。  
SQL 语句：

```sql
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
```

查询结果说明：返回首页驾驶舱核心统计卡片所需指标。

## 14. 查询近七天访客趋势

查询目的：按日期统计近七天预约数量、审批通过数量和入校数量。  
涉及数据表：`visit_apply`、`access_record`。  
SQL 语句：

```sql
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
```

查询结果说明：返回连续日期趋势数据，用于 ECharts 折线图。

## 15. 查询审批通过率

查询目的：统计审批通过数、拒绝数和通过率。  
涉及数据表：`approval_record`。  
SQL 语句：

```sql
SELECT
  COUNT(*) AS total_finished_approval_count,
  SUM(CASE WHEN approval_result = 'APPROVED' THEN 1 ELSE 0 END) AS approved_count,
  SUM(CASE WHEN approval_result = 'REJECTED' THEN 1 ELSE 0 END) AS rejected_count,
  ROUND(SUM(CASE WHEN approval_result = 'APPROVED' THEN 1 ELSE 0 END) / COUNT(*) * 100, 2) AS approval_rate_percent
FROM approval_record
WHERE approval_result IN ('APPROVED', 'REJECTED')
  AND deleted = 0;
```

查询结果说明：返回审批完成总数和通过率，可用于统计报表中的审批效率分析。

## SQL 执行方式

建议按以下顺序执行：

```bash
mysql -u root -p < database/create_database.sql
mysql -u root -p cqupt_visitor_system < database/schema.sql
mysql -u root -p cqupt_visitor_system < database/seed.sql
mysql -u root -p cqupt_visitor_system < database/query_examples.sql
```

执行完成后，数据库中将包含完整表结构、演示数据和典型查询结果，能够支撑后续 Spring Boot 后端、Vue 前端页面和课程设计报告截图。
