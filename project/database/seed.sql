-- 重庆邮电大学智慧访客预约与出入校管理系统
-- MySQL 8 seed data script

USE cqupt_visitor_system;
SET NAMES utf8mb4;

INSERT INTO department (id, parent_id, dept_code, dept_name, leader_user_id, phone, sort_order, status) VALUES
(1, NULL, 'CQUPT', '重庆邮电大学', NULL, '023-62460000', 1, 'ENABLED'),
(2, 1, 'CS', '计算机科学与技术学院', NULL, '023-62461001', 10, 'ENABLED'),
(3, 1, 'AUTO', '自动化学院', NULL, '023-62461002', 20, 'ENABLED'),
(4, 1, 'SECURITY', '保卫处', NULL, '023-62461003', 30, 'ENABLED'),
(5, 1, 'ADMIN', '党政办公室', NULL, '023-62461004', 40, 'ENABLED');

INSERT INTO sys_role (id, role_code, role_name, role_desc, status) VALUES
(1, 'VISITOR', '访客', '校外访客角色，用于后续访客端扩展', 'ENABLED'),
(2, 'HOST', '被访人', '负责确认本人被访问预约', 'ENABLED'),
(3, 'DEPT_APPROVER', '部门审批人员', '负责部门预约审批', 'ENABLED'),
(4, 'GATE_GUARD', '门岗安保人员', '负责门岗核验和出入校登记', 'ENABLED'),
(5, 'ADMIN', '系统管理员', '负责基础数据、用户和权限维护', 'ENABLED'),
(6, 'SCHOOL_MANAGER', '校级管理人员', '负责查看统计报表和全校访客态势', 'ENABLED');

INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, parent_id, route_path, component_path, api_path, sort_order, status) VALUES
(1, 'dashboard:view', '首页驾驶舱', 'MENU', NULL, '/dashboard', 'views/dashboard/index.vue', '/api/dashboard/**', 1, 'ENABLED'),
(2, 'apply:create', '访客预约申请', 'MENU', NULL, '/visit/apply', 'views/visit/apply.vue', '/api/visit-applies', 2, 'ENABLED'),
(3, 'apply:mine', '我的预约', 'MENU', NULL, '/visit/mine', 'views/visit/mine.vue', '/api/visit-applies/my', 3, 'ENABLED'),
(4, 'confirm:host', '待确认预约', 'MENU', NULL, '/approval/host', 'views/approval/host.vue', '/api/visit-applies/host-pending', 4, 'ENABLED'),
(5, 'approval:department', '部门审批', 'MENU', NULL, '/approval/department', 'views/approval/department.vue', '/api/visit-applies/department-pending', 5, 'ENABLED'),
(6, 'gate:verify', '门岗核验', 'MENU', NULL, '/gate/verify', 'views/gate/verify.vue', '/api/pass-codes/verify', 6, 'ENABLED'),
(7, 'access:entry', '入校登记', 'MENU', NULL, '/access/entry', 'views/access/entry.vue', '/api/access-records/entry', 7, 'ENABLED'),
(8, 'access:exit', '离校登记', 'MENU', NULL, '/access/exit', 'views/access/exit.vue', '/api/access-records/exit', 8, 'ENABLED'),
(9, 'access:current', '当前在校访客', 'MENU', NULL, '/access/current', 'views/access/current.vue', '/api/access-records/current', 9, 'ENABLED'),
(10, 'access:overtime', '超时未离校访客', 'MENU', NULL, '/access/overtime', 'views/access/overtime.vue', '/api/access-records/overtime', 10, 'ENABLED'),
(11, 'blacklist:manage', '黑名单管理', 'MENU', NULL, '/security/blacklist', 'views/security/blacklist.vue', '/api/blacklists', 11, 'ENABLED'),
(12, 'record:query', '访客记录查询', 'MENU', NULL, '/records', 'views/records/index.vue', '/api/visit-applies', 12, 'ENABLED'),
(13, 'report:statistics', '统计报表', 'MENU', NULL, '/reports', 'views/reports/index.vue', '/api/statistics/**', 13, 'ENABLED'),
(14, 'user:manage', '用户管理', 'MENU', NULL, '/system/users', 'views/system/users.vue', '/api/sys-users', 14, 'ENABLED'),
(15, 'role:manage', '角色权限管理', 'MENU', NULL, '/system/roles', 'views/system/roles.vue', '/api/sys-roles', 15, 'ENABLED'),
(16, 'department:manage', '部门管理', 'MENU', NULL, '/system/departments', 'views/system/departments.vue', '/api/departments', 16, 'ENABLED'),
(17, 'gate:manage', '校门管理', 'MENU', NULL, '/system/gates', 'views/system/gates.vue', '/api/campus-gates', 17, 'ENABLED'),
(18, 'log:view', '系统日志', 'MENU', NULL, '/system/logs', 'views/system/logs.vue', '/api/operation-logs', 18, 'ENABLED'),
(19, 'dict:manage', '字典管理', 'MENU', NULL, '/system/dicts', 'views/system/dicts.vue', '/api/dict-types', 19, 'ENABLED'),
(20, 'automation:manage', '截图报告记录', 'MENU', NULL, '/system/automation', 'views/system/automation.vue', '/api/screenshot-records', 20, 'ENABLED');

INSERT INTO dict_type (id, type_code, type_name, status, remark) VALUES
(1, 'apply_status', '预约状态', 'ENABLED', '预约申请流程状态'),
(2, 'access_status', '通行状态', 'ENABLED', '访客出入校状态'),
(3, 'approval_result', '审批结果', 'ENABLED', '审批记录结果'),
(4, 'gate_type', '校门类型', 'ENABLED', '校门业务类型'),
(5, 'visitor_level', '访客等级', 'ENABLED', '访客风险或服务等级');

INSERT INTO dict_item (id, type_id, item_code, item_name, item_value, sort_order, status, remark) VALUES
(1, 1, 'PENDING_HOST', '待被访人确认', 'PENDING_HOST', 1, 'ENABLED', NULL),
(2, 1, 'PENDING_DEPT', '待部门审批', 'PENDING_DEPT', 2, 'ENABLED', NULL),
(3, 1, 'APPROVED', '审批通过', 'APPROVED', 3, 'ENABLED', NULL),
(4, 1, 'REJECTED', '审批拒绝', 'REJECTED', 4, 'ENABLED', NULL),
(5, 1, 'REJECTED_BLACKLIST', '黑名单拦截', 'REJECTED_BLACKLIST', 5, 'ENABLED', NULL),
(6, 2, 'NOT_ENTERED', '未入校', 'NOT_ENTERED', 1, 'ENABLED', NULL),
(7, 2, 'ENTERED', '已入校', 'ENTERED', 2, 'ENABLED', NULL),
(8, 2, 'EXITED', '已离校', 'EXITED', 3, 'ENABLED', NULL),
(9, 2, 'OVERTIME', '超时未离校', 'OVERTIME', 4, 'ENABLED', NULL),
(10, 3, 'APPROVED', '同意', 'APPROVED', 1, 'ENABLED', NULL),
(11, 3, 'REJECTED', '拒绝', 'REJECTED', 2, 'ENABLED', NULL),
(12, 3, 'PENDING', '待处理', 'PENDING', 3, 'ENABLED', NULL);

INSERT INTO campus_gate (id, gate_code, gate_name, gate_location, gate_type, status, remark) VALUES
(1, 'NORTH_GATE', '北校门', '南岸校区北侧主入口', 'NORMAL', 'ENABLED', '访客通行主入口'),
(2, 'SOUTH_GATE', '南校门', '南岸校区南侧入口', 'PEDESTRIAN', 'ENABLED', '行人通道'),
(3, 'EAST_VEHICLE_GATE', '东区车行门', '东区停车场入口', 'VEHICLE', 'ENABLED', '车辆访客通道'),
(4, 'WEST_GATE', '西校门', '西侧生活区入口', 'NORMAL', 'ENABLED', '备用通道');

INSERT INTO sys_user (id, username, password_hash, real_name, phone, email, department_id, user_type, status, last_login_time) VALUES
(1, 'admin', '$2a$10$VYvP9rO2zG2XH8p8wYh3A.0t6Jw6gS4hLjL9c1XwC5p4fJ2lRk9dO', '系统管理员', '13800000001', 'admin@cqupt.edu.cn', 5, 'ADMIN', 'ENABLED', NOW()),
(2, 'teacher_zhang', '$2a$10$VYvP9rO2zG2XH8p8wYh3A.0t6Jw6gS4hLjL9c1XwC5p4fJ2lRk9dO', '张晓峰', '13800000002', 'zhangxf@cqupt.edu.cn', 2, 'HOST', 'ENABLED', NOW()),
(3, 'approver_cs', '$2a$10$VYvP9rO2zG2XH8p8wYh3A.0t6Jw6gS4hLjL9c1XwC5p4fJ2lRk9dO', '李审批', '13800000003', 'approve.cs@cqupt.edu.cn', 2, 'APPROVER', 'ENABLED', NOW()),
(4, 'guard_north', '$2a$10$VYvP9rO2zG2XH8p8wYh3A.0t6Jw6gS4hLjL9c1XwC5p4fJ2lRk9dO', '王安保', '13800000004', 'guard@cqupt.edu.cn', 4, 'GUARD', 'ENABLED', NOW()),
(5, 'school_manager', '$2a$10$VYvP9rO2zG2XH8p8wYh3A.0t6Jw6gS4hLjL9c1XwC5p4fJ2lRk9dO', '周校管', '13800000005', 'manager@cqupt.edu.cn', 5, 'MANAGER', 'ENABLED', NOW()),
(6, 'teacher_li', '$2a$10$VYvP9rO2zG2XH8p8wYh3A.0t6Jw6gS4hLjL9c1XwC5p4fJ2lRk9dO', '李明', '13800000006', 'liming@cqupt.edu.cn', 3, 'HOST', 'ENABLED', NOW()),
(7, 'approver_auto', '$2a$10$VYvP9rO2zG2XH8p8wYh3A.0t6Jw6gS4hLjL9c1XwC5p4fJ2lRk9dO', '赵审批', '13800000007', 'approve.auto@cqupt.edu.cn', 3, 'APPROVER', 'ENABLED', NOW());

UPDATE department SET leader_user_id = 2 WHERE id = 2;
UPDATE department SET leader_user_id = 6 WHERE id = 3;
UPDATE department SET leader_user_id = 4 WHERE id = 4;
UPDATE department SET leader_user_id = 1 WHERE id = 5;

INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(1, 1, 5),
(2, 2, 2),
(3, 3, 3),
(4, 4, 4),
(5, 5, 6),
(6, 6, 2),
(7, 7, 3);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(5, 1), (5, 11), (5, 14), (5, 15), (5, 16), (5, 17), (5, 18), (5, 19), (5, 20),
(2, 1), (2, 4), (2, 12),
(3, 1), (3, 5), (3, 12), (3, 13),
(4, 1), (4, 6), (4, 7), (4, 8), (4, 9), (4, 10), (4, 11),
(6, 1), (6, 9), (6, 10), (6, 12), (6, 13), (6, 18);

INSERT INTO visitor (id, visitor_name, id_type, id_number, phone, company, gender, visitor_level, status) VALUES
(1, '王小明', 'ID_CARD', '500101199001010011', '13900010001', '重庆数智科技有限公司', '男', 'NORMAL', 'NORMAL'),
(2, '李华', 'ID_CARD', '500101199202020022', '13900010002', '重庆云联信息技术有限公司', '女', 'NORMAL', 'NORMAL'),
(3, '赵强', 'ID_CARD', '500101198803030033', '13900010003', '西部智能装备研究院', '男', 'VIP', 'NORMAL'),
(4, '陈敏', 'ID_CARD', '500101199404040044', '13900010004', '重庆高校合作中心', '女', 'NORMAL', 'NORMAL'),
(5, '周杰', 'ID_CARD', '500101198705050055', '13900010005', '未知单位', '男', 'RISK', 'BLACKLISTED'),
(6, '刘洋', 'ID_CARD', '500101199606060066', '13900010006', '重庆交通数据公司', '男', 'NORMAL', 'NORMAL');

INSERT INTO visitor_vehicle (id, visitor_id, plate_no, vehicle_type, color, brand, status) VALUES
(1, 1, '渝A12345', 'CAR', '白色', '比亚迪', 'NORMAL'),
(2, 3, '渝B67890', 'CAR', '黑色', '大众', 'NORMAL'),
(3, 6, '渝A66K88', 'CAR', '蓝色', '特斯拉', 'NORMAL');

INSERT INTO visit_apply (id, apply_no, visitor_id, host_user_id, department_id, vehicle_id, visit_reason, plan_start_time, plan_end_time, apply_status, access_status, companion_count, submit_time, remark) VALUES
(1, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0001'), 1, 2, 2, 1, '参加计算机学院校企合作交流', DATE_ADD(CURRENT_DATE(), INTERVAL 9 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 11 HOUR), 'APPROVED', 'EXITED', 1, DATE_ADD(NOW(), INTERVAL -1 DAY), '已完成访问'),
(2, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0002'), 2, 2, 2, NULL, '讨论学生实习基地建设', DATE_ADD(CURRENT_DATE(), INTERVAL 14 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 18 HOUR), 'APPROVED', 'ENTERED', 0, DATE_ADD(NOW(), INTERVAL -2 DAY), '当前在校'),
(3, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0003'), 3, 6, 3, 2, '自动化实验室设备调试', DATE_ADD(NOW(), INTERVAL -5 HOUR), DATE_ADD(NOW(), INTERVAL -2 HOUR), 'APPROVED', 'OVERTIME', 2, DATE_ADD(NOW(), INTERVAL -3 DAY), '超时未离校演示数据'),
(4, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0004'), 4, 2, 2, NULL, '预约参观智慧校园平台', DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 2 HOUR), 'PENDING_HOST', 'NOT_ENTERED', 0, NOW(), '待被访人确认'),
(5, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0005'), 5, 6, 3, NULL, '申请进入自动化学院洽谈', DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 3 HOUR), 'REJECTED_BLACKLIST', 'NOT_ENTERED', 0, NOW(), '黑名单拦截'),
(6, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0006'), 6, 2, 2, 3, '办理项目验收资料', DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), INTERVAL 2 HOUR), 'REJECTED', 'NOT_ENTERED', 0, DATE_ADD(NOW(), INTERVAL -7 DAY), '部门审批拒绝'),
(7, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0007'), 2, 2, 2, NULL, '补充提交合作协议材料', DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 4 HOUR), 'PENDING_DEPT', 'NOT_ENTERED', 1, NOW(), '待部门审批');

INSERT INTO visitor_companion (id, apply_id, companion_name, id_type, id_number, phone, relation_remark) VALUES
(1, 1, '王小亮', 'ID_CARD', '500101199101010012', '13900011001', '同事'),
(2, 3, '赵一', 'ID_CARD', '500101199303030034', '13900011002', '技术工程师'),
(3, 3, '赵二', 'ID_CARD', '500101199303030035', '13900011003', '技术工程师'),
(4, 7, '李静', 'ID_CARD', '500101199505050056', '13900011004', '项目助理');

INSERT INTO approval_record (id, apply_id, approval_step, approver_user_id, approval_result, approval_comment, approval_time, sort_order) VALUES
(1, 1, 'HOST_CONFIRM', 2, 'APPROVED', '确认接待，来访事由属实', DATE_ADD(CURRENT_DATE(), INTERVAL 8 HOUR), 1),
(2, 1, 'DEPT_APPROVAL', 3, 'APPROVED', '同意入校交流', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 8 HOUR), INTERVAL 10 MINUTE), 2),
(3, 2, 'HOST_CONFIRM', 2, 'APPROVED', '可安排会议室接待', DATE_ADD(NOW(), INTERVAL -1 DAY), 1),
(4, 2, 'DEPT_APPROVAL', 3, 'APPROVED', '同意入校', DATE_ADD(NOW(), INTERVAL -1 DAY), 2),
(5, 3, 'HOST_CONFIRM', 6, 'APPROVED', '实验室设备调试需要入校', DATE_ADD(NOW(), INTERVAL -2 DAY), 1),
(6, 3, 'DEPT_APPROVAL', 7, 'APPROVED', '同意入校，注意离校登记', DATE_ADD(NOW(), INTERVAL -2 DAY), 2),
(7, 6, 'HOST_CONFIRM', 2, 'APPROVED', '资料提交属实', DATE_ADD(NOW(), INTERVAL -6 DAY), 1),
(8, 6, 'DEPT_APPROVAL', 3, 'REJECTED', '材料不完整，暂缓入校', DATE_ADD(NOW(), INTERVAL -6 DAY), 2),
(9, 7, 'HOST_CONFIRM', 2, 'APPROVED', '同意接待，转部门审批', NOW(), 1);

INSERT INTO pass_code (id, apply_id, pass_code, qr_content, valid_from, valid_to, pass_status, used_time, verify_count) VALUES
(1, 1, 'PC-TODAY-0001', 'CQUPT:PASS:PC-TODAY-0001', DATE_ADD(CURRENT_DATE(), INTERVAL 8 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 12 HOUR), 'USED', DATE_ADD(CURRENT_DATE(), INTERVAL 9 HOUR), 2),
(2, 2, 'PC-TODAY-0002', 'CQUPT:PASS:PC-TODAY-0002', DATE_ADD(CURRENT_DATE(), INTERVAL 13 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 19 HOUR), 'USED', DATE_ADD(CURRENT_DATE(), INTERVAL 14 HOUR), 1),
(3, 3, 'PC-OVERTIME-0003', 'CQUPT:PASS:PC-OVERTIME-0003', DATE_ADD(NOW(), INTERVAL -6 HOUR), DATE_ADD(NOW(), INTERVAL -1 HOUR), 'EXPIRED', DATE_ADD(NOW(), INTERVAL -4 HOUR), 1);

INSERT INTO access_record (id, apply_id, visitor_id, pass_code_id, entry_gate_id, exit_gate_id, entry_guard_id, exit_guard_id, entry_time, exit_time, access_status, overtime_flag, abnormal_reason) VALUES
(1, 1, 1, 1, 1, 2, 4, 4, DATE_ADD(CURRENT_DATE(), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 10 HOUR), INTERVAL 40 MINUTE), 'EXITED', 0, NULL),
(2, 2, 2, 2, 1, NULL, 4, NULL, DATE_ADD(CURRENT_DATE(), INTERVAL 14 HOUR), NULL, 'ENTERED', 0, NULL),
(3, 3, 3, 3, 3, NULL, 4, NULL, DATE_ADD(NOW(), INTERVAL -4 HOUR), NULL, 'OVERTIME', 1, '超过预约结束时间仍未登记离校');

INSERT INTO blacklist (id, visitor_id, id_number, phone, reason, level, start_time, end_time, status, operator_user_id) VALUES
(1, 5, '500101198705050055', '13900010005', '历史预约存在冒用身份信息记录', 'FORBIDDEN', DATE_ADD(NOW(), INTERVAL -30 DAY), NULL, 'ACTIVE', 1),
(2, NULL, '500101198001010099', '13900019999', '校门核验多次失败，待人工复核', 'WARNING', DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL 25 DAY), 'ACTIVE', 4);

INSERT INTO notice (id, receiver_user_id, receiver_type, title, content, business_type, business_id, read_status, read_time) VALUES
(1, 2, 'USER', '新的访客预约待确认', '访客陈敏提交了新的预约申请，请及时确认。', 'VISIT_APPLY', 4, 'UNREAD', NULL),
(2, 3, 'USER', '部门审批待处理', '访客李华的预约已通过被访人确认，请进行部门审批。', 'VISIT_APPLY', 7, 'UNREAD', NULL),
(3, 4, 'USER', '超时未离校提醒', '访客赵强已超过计划离校时间，请门岗核查。', 'ACCESS_RECORD', 3, 'READ', NOW()),
(4, 5, 'USER', '今日访客统计已更新', '今日访客概览数据已生成，可在统计报表页面查看。', 'STATISTICS', NULL, 'READ', NOW());

INSERT INTO operation_log (id, operator_user_id, operator_name, module_name, operation_type, request_method, request_url, operation_result, ip_address, operation_time, error_message) VALUES
(1, 1, '系统管理员', '用户管理', 'CREATE', 'POST', '/api/sys-users', 'SUCCESS', '127.0.0.1', DATE_ADD(NOW(), INTERVAL -3 DAY), NULL),
(2, 2, '张晓峰', '被访人确认', 'APPROVE', 'POST', '/api/approval-records', 'SUCCESS', '10.10.1.20', DATE_ADD(NOW(), INTERVAL -1 DAY), NULL),
(3, 3, '李审批', '部门审批', 'APPROVE', 'POST', '/api/approval-records', 'SUCCESS', '10.10.1.30', DATE_ADD(NOW(), INTERVAL -1 DAY), NULL),
(4, 4, '王安保', '门岗核验', 'VERIFY', 'GET', '/api/pass-codes/verify/PC-TODAY-0002', 'SUCCESS', '10.10.2.10', DATE_ADD(CURRENT_DATE(), INTERVAL 14 HOUR), NULL),
(5, 4, '王安保', '出入校登记', 'ENTRY', 'POST', '/api/access-records', 'SUCCESS', '10.10.2.10', DATE_ADD(CURRENT_DATE(), INTERVAL 14 HOUR), NULL),
(6, 4, '王安保', '门岗核验', 'VERIFY', 'GET', '/api/pass-codes/verify/INVALID', 'FAILED', '10.10.2.10', NOW(), '通行码不存在或已过期');

INSERT INTO screenshot_record (id, screenshot_code, page_name, route_path, role_code, file_path, description, capture_time, status, created_by) VALUES
(1, 'SCR-LOGIN', '登录页', '/login', 'ANONYMOUS', 'screenshots/login.png', '系统登录界面截图', NULL, 'PENDING', 1),
(2, 'SCR-DASHBOARD', '首页统计驾驶舱', '/dashboard', 'ADMIN', 'screenshots/dashboard.png', '首页统计卡片与趋势图', NULL, 'PENDING', 1),
(3, 'SCR-REPORT', '统计报表页', '/reports', 'SCHOOL_MANAGER', 'screenshots/reports.png', '统计报表页面截图', NULL, 'PENDING', 1);

INSERT INTO report_record (id, report_code, report_name, markdown_path, word_path, generate_status, generate_time, generated_by, error_message) VALUES
(1, 'RPT-COURSE-DESIGN', '重庆邮电大学智慧访客预约与出入校管理系统课程设计报告', 'docs/重庆邮电大学访客管理系统设计报告.md', NULL, 'PENDING', NULL, 1, NULL);

