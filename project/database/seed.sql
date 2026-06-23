-- 重庆邮电大学智慧访客预约与出入校管理系统
-- MySQL 8 seed data script
-- 说明：本脚本可重复执行，会清空并重建演示数据。默认密码均为 123456。

USE cqupt_visitor_system;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE report_record;
TRUNCATE TABLE screenshot_record;
TRUNCATE TABLE dict_item;
TRUNCATE TABLE operation_log;
TRUNCATE TABLE notice;
TRUNCATE TABLE blacklist;
TRUNCATE TABLE access_record;
TRUNCATE TABLE pass_code;
TRUNCATE TABLE approval_record;
TRUNCATE TABLE visitor_companion;
TRUNCATE TABLE visit_apply;
TRUNCATE TABLE visitor_vehicle;
TRUNCATE TABLE sys_role_permission;
TRUNCATE TABLE sys_user_role;
TRUNCATE TABLE sys_user;
TRUNCATE TABLE dict_type;
TRUNCATE TABLE visitor;
TRUNCATE TABLE campus_gate;
TRUNCATE TABLE sys_permission;
TRUNCATE TABLE sys_role;
TRUNCATE TABLE department;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO department (id, parent_id, dept_code, dept_name, leader_user_id, phone, sort_order, status) VALUES
(1, NULL, 'CQUPT', '重庆邮电大学', NULL, '023-62460000', 1, 'ENABLED'),
(2, 1, 'CS', '计算机科学与技术学院', NULL, '023-62461001', 10, 'ENABLED'),
(3, 1, 'SOFTWARE', '软件工程学院', NULL, '023-62461002', 20, 'ENABLED'),
(4, 1, 'COMMUNICATION', '通信与信息工程学院', NULL, '023-62461003', 30, 'ENABLED'),
(5, 1, 'AUTOMATION', '自动化学院', NULL, '023-62461004', 40, 'ENABLED'),
(6, 1, 'ACADEMIC', '教务处', NULL, '023-62461005', 50, 'ENABLED'),
(7, 1, 'SECURITY', '保卫处', NULL, '023-62461006', 60, 'ENABLED'),
(8, 1, 'ADMIN_OFFICE', '党政办公室', NULL, '023-62461007', 70, 'ENABLED');

INSERT INTO sys_role (id, role_code, role_name, role_desc, status) VALUES
(1, 'VISITOR', '访客', '校外访客角色，可提交预约并查看预约状态', 'ENABLED'),
(2, 'HOST', '被访人', '校内被访人，负责确认或拒绝与自己相关的预约申请', 'ENABLED'),
(3, 'DEPT_APPROVER', '部门审批人员', '负责本部门访客预约审批', 'ENABLED'),
(4, 'GATE_GUARD', '门岗安保人员', '负责门岗核验、入校登记、离校登记和超时处理', 'ENABLED'),
(5, 'ADMIN', '系统管理员', '负责用户、角色权限、部门、校门、字典、黑名单和日志维护', 'ENABLED'),
(6, 'SCHOOL_MANAGER', '校级管理人员', '负责查看全校访客记录、统计报表和风险态势', 'ENABLED');

INSERT INTO sys_permission (id, permission_code, permission_name, permission_type, parent_id, route_path, component_path, api_path, sort_order, status) VALUES
(1, 'dashboard:view', '首页统计驾驶舱', 'MENU', NULL, '/dashboard', 'views/Dashboard.vue', '/api/dashboard/**', 1, 'ENABLED'),
(2, 'apply:create', '访客预约申请', 'MENU', NULL, '/visit/apply', 'views/visit/Apply.vue', '/api/workflow/visit-applies', 2, 'ENABLED'),
(3, 'apply:mine', '我的预约', 'MENU', NULL, '/visit/mine', 'views/visit/MyAppointments.vue', '/api/workflow/visit-applies/my', 3, 'ENABLED'),
(4, 'confirm:host', '待确认预约', 'MENU', NULL, '/approval/host', 'views/approval/HostPending.vue', '/api/workflow/host/pending', 4, 'ENABLED'),
(5, 'approval:department', '部门审批', 'MENU', NULL, '/approval/department', 'views/approval/DepartmentApproval.vue', '/api/workflow/department/pending', 5, 'ENABLED'),
(6, 'gate:verify', '门岗核验', 'MENU', NULL, '/gate/verify', 'views/gate/GateVerify.vue', '/api/workflow/gate/verify', 6, 'ENABLED'),
(7, 'access:entry', '入校登记', 'MENU', NULL, '/access/entry', 'views/access/Entry.vue', '/api/workflow/access/entry', 7, 'ENABLED'),
(8, 'access:exit', '离校登记', 'MENU', NULL, '/access/exit', 'views/access/Exit.vue', '/api/workflow/access/exit', 8, 'ENABLED'),
(9, 'access:current', '当前在校访客', 'MENU', NULL, '/access/current', 'views/access/CurrentVisitors.vue', '/api/workflow/access/current', 9, 'ENABLED'),
(10, 'access:overtime', '超时未离校访客', 'MENU', NULL, '/access/overtime', 'views/access/OvertimeVisitors.vue', '/api/workflow/access/overtime', 10, 'ENABLED'),
(11, 'blacklist:manage', '黑名单管理', 'MENU', NULL, '/security/blacklist', 'views/security/Blacklist.vue', '/api/blacklists', 11, 'ENABLED'),
(12, 'record:query', '访客记录查询', 'MENU', NULL, '/records', 'views/records/VisitorRecords.vue', '/api/visit-applies', 12, 'ENABLED'),
(13, 'report:statistics', '统计报表', 'MENU', NULL, '/reports', 'views/reports/Statistics.vue', '/api/statistics/**', 13, 'ENABLED'),
(14, 'user:manage', '用户管理', 'MENU', NULL, '/system/users', 'views/system/Users.vue', '/api/sys-users', 14, 'ENABLED'),
(15, 'role:manage', '角色权限管理', 'MENU', NULL, '/system/roles', 'views/system/Roles.vue', '/api/sys-roles', 15, 'ENABLED'),
(16, 'department:manage', '部门管理', 'MENU', NULL, '/system/departments', 'views/system/Departments.vue', '/api/departments', 16, 'ENABLED'),
(17, 'gate:manage', '校门管理', 'MENU', NULL, '/system/gates', 'views/system/Gates.vue', '/api/campus-gates', 17, 'ENABLED'),
(18, 'log:view', '系统日志', 'MENU', NULL, '/system/logs', 'views/system/Logs.vue', '/api/operation-logs', 18, 'ENABLED'),
(19, 'dict:manage', '字典管理', 'MENU', NULL, '/system/dicts', 'views/system/Dicts.vue', '/api/dict-types', 19, 'ENABLED'),
(20, 'automation:manage', '截图报告记录', 'MENU', NULL, '/system/automation', 'views/system/Automation.vue', '/api/screenshot-records', 20, 'ENABLED');

INSERT INTO dict_type (id, type_code, type_name, status, remark) VALUES
(1, 'apply_status', '预约状态', 'ENABLED', '访客预约申请状态'),
(2, 'access_status', '访问状态', 'ENABLED', '访客出入校状态'),
(3, 'approval_result', '审批结果', 'ENABLED', '审批记录结果'),
(4, 'gate_type', '校门类型', 'ENABLED', '校门业务类型'),
(5, 'visitor_level', '访客等级', 'ENABLED', '访客风险或服务等级'),
(6, 'pass_status', '通行凭证状态', 'ENABLED', '通行码有效性状态'),
(7, 'blacklist_status', '黑名单状态', 'ENABLED', '黑名单记录状态');

INSERT INTO dict_item (id, type_id, item_code, item_name, item_value, sort_order, status, remark) VALUES
(1, 1, 'PENDING_HOST', '待被访人确认', 'PENDING_HOST', 1, 'ENABLED', NULL),
(2, 1, 'HOST_CONFIRMED', '被访人已确认', 'HOST_CONFIRMED', 2, 'ENABLED', '进入部门审批队列'),
(3, 1, 'PENDING_DEPT', '待部门审批', 'PENDING_DEPT', 3, 'ENABLED', '兼容待审批列表'),
(4, 1, 'HOST_REJECTED', '被访人已拒绝', 'HOST_REJECTED', 4, 'ENABLED', NULL),
(5, 1, 'APPROVED', '审批通过', 'APPROVED', 5, 'ENABLED', NULL),
(6, 1, 'REJECTED', '审批拒绝', 'REJECTED', 6, 'ENABLED', NULL),
(7, 1, 'CANCELED', '已取消', 'CANCELED', 7, 'ENABLED', NULL),
(8, 1, 'REJECTED_BLACKLIST', '黑名单拦截', 'REJECTED_BLACKLIST', 8, 'ENABLED', NULL),
(9, 2, 'NOT_ENTERED', '未入校', 'NOT_ENTERED', 1, 'ENABLED', NULL),
(10, 2, 'ENTERED', '已入校', 'ENTERED', 2, 'ENABLED', NULL),
(11, 2, 'EXITED', '已离校', 'EXITED', 3, 'ENABLED', NULL),
(12, 2, 'OVERTIME', '超时未离校', 'OVERTIME', 4, 'ENABLED', NULL),
(13, 2, 'ABNORMAL', '异常处理', 'ABNORMAL', 5, 'ENABLED', NULL),
(14, 3, 'APPROVED', '同意', 'APPROVED', 1, 'ENABLED', NULL),
(15, 3, 'REJECTED', '拒绝', 'REJECTED', 2, 'ENABLED', NULL),
(16, 3, 'PENDING', '待处理', 'PENDING', 3, 'ENABLED', NULL),
(17, 4, 'NORMAL', '综合通行门', 'NORMAL', 1, 'ENABLED', NULL),
(18, 4, 'VEHICLE', '车辆通行门', 'VEHICLE', 2, 'ENABLED', NULL),
(19, 4, 'PEDESTRIAN', '行人通行门', 'PEDESTRIAN', 3, 'ENABLED', NULL),
(20, 5, 'NORMAL', '普通访客', 'NORMAL', 1, 'ENABLED', NULL),
(21, 5, 'VIP', '重要访客', 'VIP', 2, 'ENABLED', NULL),
(22, 5, 'RISK', '风险访客', 'RISK', 3, 'ENABLED', NULL),
(23, 6, 'VALID', '有效', 'VALID', 1, 'ENABLED', NULL),
(24, 6, 'USED', '已使用', 'USED', 2, 'ENABLED', NULL),
(25, 6, 'EXPIRED', '已过期', 'EXPIRED', 3, 'ENABLED', NULL),
(26, 6, 'DISABLED', '已停用', 'DISABLED', 4, 'ENABLED', NULL),
(27, 7, 'ACTIVE', '生效中', 'ACTIVE', 1, 'ENABLED', NULL),
(28, 7, 'EXPIRED', '已失效', 'EXPIRED', 2, 'ENABLED', NULL),
(29, 7, 'REMOVED', '已移除', 'REMOVED', 3, 'ENABLED', NULL);

INSERT INTO campus_gate (id, gate_code, gate_name, gate_location, gate_type, status, remark) VALUES
(1, 'NORTH_GATE', '北校门', '南岸校区北侧主入口', 'NORMAL', 'ENABLED', '访客和教职工综合通道'),
(2, 'SOUTH_GATE', '南校门', '南岸校区南侧入口', 'PEDESTRIAN', 'ENABLED', '行人访客通道'),
(3, 'EAST_VEHICLE_GATE', '东区车行门', '东区停车场入口', 'VEHICLE', 'ENABLED', '车辆访客通道'),
(4, 'WEST_GATE', '西校门', '西侧生活区入口', 'NORMAL', 'ENABLED', '备用通道'),
(5, 'INNOVATION_GATE', '创新园门岗', '大学科技园与创新创业基地入口', 'NORMAL', 'ENABLED', '校企合作访客常用入口');

INSERT INTO sys_user (id, username, password_hash, real_name, phone, email, department_id, user_type, status, last_login_time) VALUES
(1, 'admin', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '系统管理员', '13800000001', 'admin@cqupt.edu.cn', 8, 'ADMIN', 'ENABLED', NOW()),
(2, 'visitor01', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '访客测试账号', '13900010001', 'visitor01@example.com', NULL, 'VISITOR', 'ENABLED', NOW()),
(3, 'teacher01', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '张晓峰', '13800000002', 'teacher01@cqupt.edu.cn', 2, 'HOST', 'ENABLED', NOW()),
(4, 'teacher02', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '李明远', '13800000006', 'teacher02@cqupt.edu.cn', 3, 'HOST', 'ENABLED', NOW()),
(5, 'teacher03', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '陈嘉宁', '13800000008', 'teacher03@cqupt.edu.cn', 4, 'HOST', 'ENABLED', NOW()),
(6, 'teacher04', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '周若兰', '13800000009', 'teacher04@cqupt.edu.cn', 5, 'HOST', 'ENABLED', NOW()),
(7, 'approver01', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '李审批', '13800000003', 'approver01@cqupt.edu.cn', 2, 'APPROVER', 'ENABLED', NOW()),
(8, 'approver02', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '赵审批', '13800000007', 'approver02@cqupt.edu.cn', 3, 'APPROVER', 'ENABLED', NOW()),
(9, 'approver03', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '王审批', '13800000010', 'approver03@cqupt.edu.cn', 4, 'APPROVER', 'ENABLED', NOW()),
(10, 'approver04', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '刘审批', '13800000011', 'approver04@cqupt.edu.cn', 5, 'APPROVER', 'ENABLED', NOW()),
(11, 'guard01', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '王安保', '13800000004', 'guard01@cqupt.edu.cn', 7, 'GUARD', 'ENABLED', NOW()),
(12, 'guard02', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '孙安保', '13800000012', 'guard02@cqupt.edu.cn', 7, 'GUARD', 'ENABLED', NOW()),
(13, 'manager01', '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m', '周校管', '13800000005', 'manager01@cqupt.edu.cn', 1, 'MANAGER', 'ENABLED', NOW());

UPDATE department SET leader_user_id = 13 WHERE id = 1;
UPDATE department SET leader_user_id = 3 WHERE id = 2;
UPDATE department SET leader_user_id = 4 WHERE id = 3;
UPDATE department SET leader_user_id = 5 WHERE id = 4;
UPDATE department SET leader_user_id = 6 WHERE id = 5;
UPDATE department SET leader_user_id = 13 WHERE id = 6;
UPDATE department SET leader_user_id = 11 WHERE id = 7;
UPDATE department SET leader_user_id = 1 WHERE id = 8;

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 5),
(2, 1),
(3, 2),
(4, 2),
(5, 2),
(6, 2),
(7, 3),
(8, 3),
(9, 3),
(10, 3),
(11, 4),
(12, 4),
(13, 6);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 3), (2, 4), (2, 12),
(3, 1), (3, 5), (3, 12), (3, 13),
(4, 1), (4, 6), (4, 7), (4, 8), (4, 9), (4, 10),
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 8), (5, 9), (5, 10),
(5, 11), (5, 12), (5, 13), (5, 14), (5, 15), (5, 16), (5, 17), (5, 18), (5, 19), (5, 20),
(6, 1), (6, 9), (6, 10), (6, 12), (6, 13);
INSERT INTO visitor (id, visitor_name, id_type, id_number, phone, company, gender, visitor_level, status) VALUES
(1, '王小明', 'ID_CARD', '500101199001010011', '13900010001', '重庆数智科技有限公司', '男', 'NORMAL', 'NORMAL'),
(2, '李华', 'ID_CARD', '500101199202020022', '13900010002', '重庆云联信息技术有限公司', '女', 'NORMAL', 'NORMAL'),
(3, '赵敏', 'ID_CARD', '500101198803030033', '13900010003', '西部智能装备研究院', '女', 'VIP', 'NORMAL'),
(4, '陈晨', 'ID_CARD', '500101199404040044', '13900010004', '重庆高校合作中心', '女', 'NORMAL', 'NORMAL'),
(5, '周杰', 'ID_CARD', '500101198705050055', '13900010005', '未知单位', '男', 'RISK', 'BLACKLISTED'),
(6, '刘洋', 'ID_CARD', '500101199606060066', '13900010006', '重庆交通数据公司', '男', 'NORMAL', 'NORMAL'),
(7, '张伟', 'ID_CARD', '500101199107070077', '13900010007', '成都智算科技有限公司', '男', 'NORMAL', 'NORMAL'),
(8, '黄婷', 'ID_CARD', '500101199308080088', '13900010008', '重庆软件园企业服务中心', '女', 'NORMAL', 'NORMAL'),
(9, '唐磊', 'ID_CARD', '500101199009090099', '13900010009', '重庆物联网创新中心', '男', 'NORMAL', 'NORMAL'),
(10, '何雨', 'ID_CARD', '500101199510100010', '13900010010', '重庆移动行业应用部', '女', 'VIP', 'NORMAL'),
(11, '蒋涛', 'ID_CARD', '500101198611110011', '13900010011', '重庆大数据产业集团', '男', 'NORMAL', 'NORMAL'),
(12, '孙悦', 'ID_CARD', '500101199712120012', '13900010012', '重庆高校联盟秘书处', '女', 'NORMAL', 'NORMAL'),
(13, '郭强', 'ID_CARD', '500101198913130013', '13900010013', '重庆安全测评中心', '男', 'NORMAL', 'NORMAL'),
(14, '罗娜', 'ID_CARD', '500101199414140014', '13900010014', '重庆实验设备有限公司', '女', 'NORMAL', 'NORMAL'),
(15, '马可', 'ID_CARD', '500101199515150015', '13900010015', '重庆教育咨询有限公司', '男', 'NORMAL', 'NORMAL'),
(16, '吴迪', 'ID_CARD', '500101198816160016', '13900010016', '异常访客样例单位', '男', 'RISK', 'BLACKLISTED');

INSERT INTO visitor_vehicle (id, visitor_id, plate_no, vehicle_type, color, brand, status) VALUES
(1, 1, '渝A12345', 'CAR', '白色', '比亚迪', 'NORMAL'),
(2, 3, '渝B67890', 'CAR', '黑色', '大众', 'NORMAL'),
(3, 6, '渝A66K88', 'CAR', '蓝色', '特斯拉', 'NORMAL'),
(4, 10, '渝D31010', 'CAR', '银色', '丰田', 'NORMAL'),
(5, 11, '渝A2T911', 'CAR', '灰色', '长安', 'NORMAL'),
(6, 13, '渝B9Q213', 'CAR', '黑色', '奥迪', 'NORMAL'),
(7, 16, '渝C0X016', 'CAR', '红色', '本田', 'LIMITED');

INSERT INTO visit_apply (id, apply_no, visitor_id, host_user_id, department_id, vehicle_id, visit_reason, plan_start_time, plan_end_time, apply_status, access_status, companion_count, submit_time, cancel_time, cancel_reason, remark) VALUES
(1, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0001'), 1, 3, 2, 1, '参加计算机学院校企合作交流', DATE_ADD(CURRENT_DATE(), INTERVAL 9 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 11 HOUR), 'APPROVED', 'EXITED', 1, DATE_ADD(NOW(), INTERVAL -2 DAY), NULL, NULL, '正常预约并完成离校'),
(2, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0002'), 2, 3, 2, NULL, '讨论学生实习基地建设', DATE_ADD(CURRENT_DATE(), INTERVAL 14 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 18 HOUR), 'APPROVED', 'ENTERED', 0, DATE_ADD(NOW(), INTERVAL -1 DAY), NULL, NULL, '当前在校访客'),
(3, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0003'), 3, 6, 5, 2, '自动化实验室设备调试', DATE_ADD(CURRENT_DATE(), INTERVAL 8 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 10 HOUR), 'APPROVED', 'OVERTIME', 2, DATE_ADD(NOW(), INTERVAL -3 DAY), NULL, NULL, '超时未离校演示数据'),
(4, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), '%Y%m%d'), '0004'), 4, 3, 2, NULL, '预约参观智慧校园平台', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 11 HOUR), 'PENDING_HOST', 'NOT_ENTERED', 0, NOW(), NULL, NULL, '待被访人确认'),
(5, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), '%Y%m%d'), '0005'), 8, 3, 2, NULL, '补充提交校企合作协议材料', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 10 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 12 HOUR), 'HOST_CONFIRMED', 'NOT_ENTERED', 1, DATE_ADD(NOW(), INTERVAL -5 HOUR), NULL, NULL, '被访人已确认，待部门审批'),
(6, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), '%Y%m%d'), '0006'), 9, 4, 3, NULL, '软件工程实践基地项目洽谈', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 14 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 17 HOUR), 'PENDING_DEPT', 'NOT_ENTERED', 0, DATE_ADD(NOW(), INTERVAL -6 HOUR), NULL, NULL, '部门待审批'),
(7, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL 2 DAY), '%Y%m%d'), '0007'), 7, 5, 4, NULL, '通信实验室参观申请', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 2 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 2 DAY), INTERVAL 11 HOUR), 'HOST_REJECTED', 'NOT_ENTERED', 0, DATE_ADD(NOW(), INTERVAL -1 DAY), NULL, NULL, '被访人拒绝'),
(8, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), '%Y%m%d'), '0008'), 6, 3, 2, 3, '项目验收资料递交', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 15 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 17 HOUR), 'REJECTED', 'NOT_ENTERED', 0, DATE_ADD(NOW(), INTERVAL -4 DAY), NULL, NULL, '部门审批拒绝'),
(9, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL 2 DAY), '%Y%m%d'), '0009'), 15, 4, 3, NULL, '招生宣传材料沟通', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 2 DAY), INTERVAL 15 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 2 DAY), INTERVAL 17 HOUR), 'CANCELED', 'NOT_ENTERED', 0, DATE_ADD(NOW(), INTERVAL -2 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '访客行程变化主动取消', '已取消预约'),
(10, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), '%Y%m%d'), '0010'), 5, 6, 5, NULL, '申请进入自动化学院洽谈', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 12 HOUR), 'REJECTED_BLACKLIST', 'NOT_ENTERED', 0, NOW(), NULL, NULL, '黑名单访客被拦截'),
(11, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), '%Y%m%d'), '0011'), 10, 5, 4, 4, '5G 行业应用交流', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 13 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 16 HOUR), 'APPROVED', 'NOT_ENTERED', 1, DATE_ADD(NOW(), INTERVAL -1 DAY), NULL, NULL, '已生成通行凭证，尚未入校'),
(12, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0012'), 11, 4, 3, 5, '大数据平台联调会议', DATE_ADD(CURRENT_DATE(), INTERVAL 10 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 18 HOUR), 'APPROVED', 'ENTERED', 0, DATE_ADD(NOW(), INTERVAL -1 DAY), NULL, NULL, '当前在校车辆访客'),
(13, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), '%Y%m%d'), '0013'), 12, 3, 2, NULL, '高校联盟会议', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 12 HOUR), 'APPROVED', 'EXITED', 0, DATE_ADD(NOW(), INTERVAL -3 DAY), NULL, NULL, '昨日访问完成'),
(14, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), '%Y%m%d'), '0014'), 13, 4, 3, 6, '软件安全测评交流', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 10 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 12 HOUR), 'APPROVED', 'EXITED', 1, DATE_ADD(NOW(), INTERVAL -4 DAY), NULL, NULL, '近七天趋势数据'),
(15, CONCAT('VA', DATE_FORMAT(CURRENT_DATE(), '%Y%m%d'), '0015'), 14, 5, 4, NULL, '通信实验设备维护', DATE_ADD(CURRENT_DATE(), INTERVAL 7 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 9 HOUR), 'APPROVED', 'OVERTIME', 0, DATE_ADD(NOW(), INTERVAL -2 DAY), NULL, NULL, '超时未离校'),
(16, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL -3 DAY), '%Y%m%d'), '0016'), 1, 6, 5, 1, '自动化创新实验室交流', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -3 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -3 DAY), INTERVAL 11 HOUR), 'APPROVED', 'EXITED', 0, DATE_ADD(NOW(), INTERVAL -5 DAY), NULL, NULL, '近七天趋势数据'),
(17, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL -4 DAY), '%Y%m%d'), '0017'), 2, 3, 2, NULL, '学生实习回访交流', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -4 DAY), INTERVAL 14 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -4 DAY), INTERVAL 16 HOUR), 'APPROVED', 'EXITED', 0, DATE_ADD(NOW(), INTERVAL -6 DAY), NULL, NULL, '近七天趋势数据'),
(18, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL -5 DAY), '%Y%m%d'), '0018'), 3, 4, 3, 2, '软件工程课程资源合作', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -5 DAY), INTERVAL 10 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -5 DAY), INTERVAL 12 HOUR), 'APPROVED', 'EXITED', 2, DATE_ADD(NOW(), INTERVAL -7 DAY), NULL, NULL, '近七天趋势数据'),
(19, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), '%Y%m%d'), '0019'), 16, 5, 4, 7, '临时拜访通信学院', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 15 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 17 HOUR), 'REJECTED_BLACKLIST', 'NOT_ENTERED', 0, NOW(), NULL, NULL, '黑名单车辆访客拦截'),
(20, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), '%Y%m%d'), '0020'), 4, 5, 4, NULL, '通信学院合作参观', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), INTERVAL 11 HOUR), 'APPROVED', 'EXITED', 0, DATE_ADD(NOW(), INTERVAL -8 DAY), NULL, NULL, '近七天趋势数据'),
(21, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY), '%Y%m%d'), '0021'), 10, 6, 5, 4, '自动化学院项目复盘', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY), INTERVAL 13 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY), INTERVAL 15 HOUR), 'APPROVED', 'EXITED', 1, DATE_ADD(NOW(), INTERVAL -9 DAY), NULL, NULL, '近七天趋势数据'),
(22, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL -8 DAY), '%Y%m%d'), '0022'), 6, 3, 2, 3, '历史预约记录查询样例', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -8 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -8 DAY), INTERVAL 11 HOUR), 'APPROVED', 'EXITED', 0, DATE_ADD(NOW(), INTERVAL -10 DAY), NULL, NULL, '历史查询样例'),
(23, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL 3 DAY), '%Y%m%d'), '0023'), 8, 4, 3, NULL, '软件学院产教融合交流', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 3 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 3 DAY), INTERVAL 11 HOUR), 'PENDING_HOST', 'NOT_ENTERED', 0, NOW(), NULL, NULL, '第二条待确认数据'),
(24, CONCAT('VA', DATE_FORMAT(DATE_ADD(CURRENT_DATE(), INTERVAL 3 DAY), '%Y%m%d'), '0024'), 9, 6, 5, NULL, '自动化学院校企联合课题沟通', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 3 DAY), INTERVAL 14 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 3 DAY), INTERVAL 17 HOUR), 'HOST_CONFIRMED', 'NOT_ENTERED', 0, DATE_ADD(NOW(), INTERVAL -3 HOUR), NULL, NULL, '第二条被访人已确认数据');

INSERT INTO visitor_companion (id, apply_id, companion_name, id_type, id_number, phone, relation_remark) VALUES
(1, 1, '王小兵', 'ID_CARD', '500101199101010012', '13900011001', '同事'),
(2, 3, '赵一', 'ID_CARD', '500101199303030034', '13900011002', '技术工程师'),
(3, 3, '赵二', 'ID_CARD', '500101199303030035', '13900011003', '技术工程师'),
(4, 5, '黄静', 'ID_CARD', '500101199505050056', '13900011004', '项目助理'),
(5, 11, '何小雨', 'ID_CARD', '500101199610100011', '13900011005', '产品经理'),
(6, 14, '郭小强', 'ID_CARD', '500101198913130014', '13900011006', '测评工程师'),
(7, 18, '赵三', 'ID_CARD', '500101198803030036', '13900011007', '课程顾问'),
(8, 18, '赵四', 'ID_CARD', '500101198803030037', '13900011008', '课程顾问'),
(9, 21, '何晓', 'ID_CARD', '500101199510100012', '13900011009', '项目助理');
INSERT INTO approval_record (id, apply_id, approval_step, approver_user_id, approval_result, approval_comment, approval_time, sort_order) VALUES
(1, 1, 'HOST_CONFIRM', 3, 'APPROVED', '确认接待，来访事由属实', DATE_ADD(CURRENT_DATE(), INTERVAL 8 HOUR), 1),
(2, 1, 'DEPT_APPROVAL', 7, 'APPROVED', '同意入校交流', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 8 HOUR), INTERVAL 10 MINUTE), 2),
(3, 2, 'HOST_CONFIRM', 3, 'APPROVED', '可安排会议室接待', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 15 HOUR), 1),
(4, 2, 'DEPT_APPROVAL', 7, 'APPROVED', '同意入校', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 16 HOUR), 2),
(5, 3, 'HOST_CONFIRM', 6, 'APPROVED', '实验室设备调试需要入校', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 9 HOUR), 1),
(6, 3, 'DEPT_APPROVAL', 10, 'APPROVED', '同意入校，注意离校登记', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 10 HOUR), 2),
(7, 4, 'HOST_CONFIRM', 3, 'PENDING', '等待被访人处理', NULL, 1),
(8, 5, 'HOST_CONFIRM', 3, 'APPROVED', '合作协议材料补充，确认接待', DATE_ADD(NOW(), INTERVAL -4 HOUR), 1),
(9, 5, 'DEPT_APPROVAL', 7, 'PENDING', '等待部门审批', NULL, 2),
(10, 6, 'HOST_CONFIRM', 4, 'APPROVED', '软件工程基地事项属实', DATE_ADD(NOW(), INTERVAL -5 HOUR), 1),
(11, 6, 'DEPT_APPROVAL', 8, 'PENDING', '等待部门审批', NULL, 2),
(12, 7, 'HOST_CONFIRM', 5, 'REJECTED', '当天被访人外出，无法接待', DATE_ADD(NOW(), INTERVAL -20 HOUR), 1),
(13, 8, 'HOST_CONFIRM', 3, 'APPROVED', '资料递交属实', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 9 HOUR), 1),
(14, 8, 'DEPT_APPROVAL', 7, 'REJECTED', '资料不完整，暂缓入校', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 10 HOUR), 2),
(15, 9, 'HOST_CONFIRM', 4, 'PENDING', '访客主动取消，审批终止', NULL, 1),
(16, 11, 'HOST_CONFIRM', 5, 'APPROVED', '确认行业应用交流接待', DATE_ADD(NOW(), INTERVAL -20 HOUR), 1),
(17, 11, 'DEPT_APPROVAL', 9, 'APPROVED', '同意入校交流', DATE_ADD(NOW(), INTERVAL -19 HOUR), 2),
(18, 12, 'HOST_CONFIRM', 4, 'APPROVED', '大数据平台会议确认', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 9 HOUR), 1),
(19, 12, 'DEPT_APPROVAL', 8, 'APPROVED', '同意进入软件工程学院', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 10 HOUR), 2),
(20, 13, 'HOST_CONFIRM', 3, 'APPROVED', '高校联盟会议确认', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 14 HOUR), 1),
(21, 13, 'DEPT_APPROVAL', 7, 'APPROVED', '同意入校参会', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 15 HOUR), 2),
(22, 14, 'HOST_CONFIRM', 4, 'APPROVED', '安全测评交流确认', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -3 DAY), INTERVAL 9 HOUR), 1),
(23, 14, 'DEPT_APPROVAL', 8, 'APPROVED', '同意入校测评交流', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -3 DAY), INTERVAL 10 HOUR), 2),
(24, 15, 'HOST_CONFIRM', 5, 'APPROVED', '通信实验设备维护确认', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 9 HOUR), 1),
(25, 15, 'DEPT_APPROVAL', 9, 'APPROVED', '同意入校维护', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 10 HOUR), 2),
(26, 16, 'HOST_CONFIRM', 6, 'APPROVED', '创新实验室交流确认', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -4 DAY), INTERVAL 9 HOUR), 1),
(27, 16, 'DEPT_APPROVAL', 10, 'APPROVED', '同意入校交流', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -4 DAY), INTERVAL 10 HOUR), 2),
(28, 17, 'HOST_CONFIRM', 3, 'APPROVED', '实习基地回访确认', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -5 DAY), INTERVAL 9 HOUR), 1),
(29, 17, 'DEPT_APPROVAL', 7, 'APPROVED', '同意入校回访', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -5 DAY), INTERVAL 10 HOUR), 2),
(30, 18, 'HOST_CONFIRM', 4, 'APPROVED', '课程资源合作确认', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), INTERVAL 9 HOUR), 1),
(31, 18, 'DEPT_APPROVAL', 8, 'APPROVED', '同意入校交流', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), INTERVAL 10 HOUR), 2),
(32, 20, 'HOST_CONFIRM', 5, 'APPROVED', '通信学院参观确认', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY), INTERVAL 9 HOUR), 1),
(33, 20, 'DEPT_APPROVAL', 9, 'APPROVED', '同意入校参观', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY), INTERVAL 10 HOUR), 2),
(34, 21, 'HOST_CONFIRM', 6, 'APPROVED', '项目复盘确认', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -8 DAY), INTERVAL 9 HOUR), 1),
(35, 21, 'DEPT_APPROVAL', 10, 'APPROVED', '同意入校复盘', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -8 DAY), INTERVAL 10 HOUR), 2),
(36, 22, 'HOST_CONFIRM', 3, 'APPROVED', '历史预约记录样例确认', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -9 DAY), INTERVAL 9 HOUR), 1),
(37, 22, 'DEPT_APPROVAL', 7, 'APPROVED', '同意入校', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -9 DAY), INTERVAL 10 HOUR), 2),
(38, 23, 'HOST_CONFIRM', 4, 'PENDING', '等待被访人处理', NULL, 1),
(39, 24, 'HOST_CONFIRM', 6, 'APPROVED', '联合课题沟通确认', DATE_ADD(NOW(), INTERVAL -2 HOUR), 1),
(40, 24, 'DEPT_APPROVAL', 10, 'PENDING', '等待部门审批', NULL, 2);

INSERT INTO pass_code (id, apply_id, pass_code, qr_content, valid_from, valid_to, pass_status, used_time, verify_count) VALUES
(1, 1, 'PC-TODAY-0001', 'CQUPT:PASS:PC-TODAY-0001', DATE_ADD(CURRENT_DATE(), INTERVAL 8 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 12 HOUR), 'USED', DATE_ADD(CURRENT_DATE(), INTERVAL 9 HOUR), 2),
(2, 2, 'PC-TODAY-0002', 'CQUPT:PASS:PC-TODAY-0002', DATE_ADD(CURRENT_DATE(), INTERVAL 13 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 19 HOUR), 'USED', DATE_ADD(CURRENT_DATE(), INTERVAL 14 HOUR), 1),
(3, 3, 'PC-OVERTIME-0003', 'CQUPT:PASS:PC-OVERTIME-0003', DATE_ADD(CURRENT_DATE(), INTERVAL 7 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 10 HOUR), 'EXPIRED', DATE_ADD(CURRENT_DATE(), INTERVAL 8 HOUR), 1),
(4, 11, 'PC-FUTURE-0011', 'CQUPT:PASS:PC-FUTURE-0011', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 12 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 17 HOUR), 'VALID', NULL, 0),
(5, 12, 'PC-TODAY-0012', 'CQUPT:PASS:PC-TODAY-0012', DATE_ADD(CURRENT_DATE(), INTERVAL 9 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 19 HOUR), 'USED', DATE_ADD(CURRENT_DATE(), INTERVAL 10 HOUR), 1),
(6, 13, 'PC-HIS-0013', 'CQUPT:PASS:PC-HIS-0013', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 8 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 13 HOUR), 'USED', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 9 HOUR), 2),
(7, 14, 'PC-HIS-0014', 'CQUPT:PASS:PC-HIS-0014', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 13 HOUR), 'USED', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 10 HOUR), 1),
(8, 15, 'PC-OVERTIME-0015', 'CQUPT:PASS:PC-OVERTIME-0015', DATE_ADD(CURRENT_DATE(), INTERVAL 6 HOUR), DATE_ADD(CURRENT_DATE(), INTERVAL 9 HOUR), 'EXPIRED', DATE_ADD(CURRENT_DATE(), INTERVAL 7 HOUR), 1),
(9, 16, 'PC-HIS-0016', 'CQUPT:PASS:PC-HIS-0016', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -3 DAY), INTERVAL 8 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -3 DAY), INTERVAL 12 HOUR), 'USED', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -3 DAY), INTERVAL 9 HOUR), 1),
(10, 17, 'PC-HIS-0017', 'CQUPT:PASS:PC-HIS-0017', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -4 DAY), INTERVAL 13 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -4 DAY), INTERVAL 17 HOUR), 'USED', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -4 DAY), INTERVAL 14 HOUR), 1),
(11, 18, 'PC-HIS-0018', 'CQUPT:PASS:PC-HIS-0018', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -5 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -5 DAY), INTERVAL 13 HOUR), 'USED', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -5 DAY), INTERVAL 10 HOUR), 1),
(12, 20, 'PC-HIS-0020', 'CQUPT:PASS:PC-HIS-0020', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), INTERVAL 8 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), INTERVAL 12 HOUR), 'USED', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), INTERVAL 9 HOUR), 1),
(13, 21, 'PC-HIS-0021', 'CQUPT:PASS:PC-HIS-0021', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY), INTERVAL 12 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY), INTERVAL 16 HOUR), 'USED', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY), INTERVAL 13 HOUR), 1),
(14, 22, 'PC-HIS-0022', 'CQUPT:PASS:PC-HIS-0022', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -8 DAY), INTERVAL 8 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -8 DAY), INTERVAL 12 HOUR), 'USED', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -8 DAY), INTERVAL 9 HOUR), 1);

INSERT INTO access_record (id, apply_id, visitor_id, pass_code_id, entry_gate_id, exit_gate_id, entry_guard_id, exit_guard_id, entry_time, exit_time, access_status, overtime_flag, abnormal_reason) VALUES
(1, 1, 1, 1, 1, 2, 11, 12, DATE_ADD(CURRENT_DATE(), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 10 HOUR), INTERVAL 40 MINUTE), 'EXITED', 0, NULL),
(2, 2, 2, 2, 1, NULL, 11, NULL, DATE_ADD(CURRENT_DATE(), INTERVAL 14 HOUR), NULL, 'ENTERED', 0, NULL),
(3, 3, 3, 3, 3, NULL, 11, NULL, DATE_ADD(CURRENT_DATE(), INTERVAL 8 HOUR), NULL, 'OVERTIME', 1, '超过预约结束时间仍未登记离校'),
(4, 12, 11, 5, 5, NULL, 12, NULL, DATE_ADD(CURRENT_DATE(), INTERVAL 10 HOUR), NULL, 'ENTERED', 0, NULL),
(5, 13, 12, 6, 2, 2, 11, 11, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY), INTERVAL 11 HOUR), 'EXITED', 0, NULL),
(6, 14, 13, 7, 3, 3, 12, 12, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 10 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 12 HOUR), 'EXITED', 0, NULL),
(7, 15, 14, 8, 1, NULL, 11, NULL, DATE_ADD(CURRENT_DATE(), INTERVAL 7 HOUR), NULL, 'OVERTIME', 1, '设备维护人员未按时离校'),
(8, 16, 1, 9, 5, 5, 12, 12, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -3 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -3 DAY), INTERVAL 10 HOUR), 'EXITED', 0, NULL),
(9, 17, 2, 10, 1, 4, 11, 12, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -4 DAY), INTERVAL 14 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -4 DAY), INTERVAL 16 HOUR), 'EXITED', 0, NULL),
(10, 18, 3, 11, 3, 3, 12, 12, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -5 DAY), INTERVAL 10 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -5 DAY), INTERVAL 12 HOUR), 'EXITED', 0, NULL),
(11, 20, 4, 12, 4, 4, 11, 11, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -6 DAY), INTERVAL 10 HOUR), 'EXITED', 0, NULL),
(12, 21, 10, 13, 5, 5, 12, 12, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY), INTERVAL 13 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY), INTERVAL 15 HOUR), 'EXITED', 0, NULL),
(13, 22, 6, 14, 1, 2, 11, 12, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -8 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -8 DAY), INTERVAL 10 HOUR), 'EXITED', 0, NULL);
INSERT INTO blacklist (id, visitor_id, id_number, phone, reason, level, start_time, end_time, status, operator_user_id) VALUES
(1, 5, '500101198705050055', '13900010005', '历史预约存在冒用身份信息记录', 'FORBIDDEN', DATE_ADD(NOW(), INTERVAL -30 DAY), NULL, 'ACTIVE', 1),
(2, 16, '500101198816160016', '13900010016', '门岗核验多次失败且车辆信息异常', 'LIMITED', DATE_ADD(NOW(), INTERVAL -10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), 'ACTIVE', 11),
(3, NULL, '500101198001010099', '13900019999', '临时风险号码，待人工复核', 'WARNING', DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL 25 DAY), 'ACTIVE', 12),
(4, 7, '500101199107070077', '13900010007', '历史异常记录已过期，仅保留审计样例', 'WARNING', DATE_ADD(NOW(), INTERVAL -90 DAY), DATE_ADD(NOW(), INTERVAL -10 DAY), 'EXPIRED', 1);

INSERT INTO notice (id, receiver_user_id, receiver_type, title, content, business_type, business_id, read_status, read_time) VALUES
(1, 3, 'USER', '新的访客预约待确认', '访客陈晨提交了新的预约申请，请及时确认。', 'VISIT_APPLY', 4, 'UNREAD', NULL),
(2, 3, 'USER', '访客预约已确认', '访客黄婷的预约已由您确认，正在等待部门审批。', 'VISIT_APPLY', 5, 'READ', DATE_ADD(NOW(), INTERVAL -2 HOUR)),
(3, 7, 'USER', '部门审批待处理', '计算机科学与技术学院存在待审批访客预约，请及时处理。', 'VISIT_APPLY', 5, 'UNREAD', NULL),
(4, 8, 'USER', '部门审批待处理', '软件工程学院存在待审批访客预约，请及时处理。', 'VISIT_APPLY', 6, 'UNREAD', NULL),
(5, 10, 'USER', '自动化学院审批提醒', '自动化学院联合课题沟通预约已通过被访人确认。', 'VISIT_APPLY', 24, 'UNREAD', NULL),
(6, 11, 'USER', '超时未离校提醒', '访客赵敏超过预约结束时间仍未登记离校，请门岗跟进。', 'ACCESS_RECORD', 3, 'READ', NOW()),
(7, 12, 'USER', '超时未离校提醒', '访客罗娜超过预约结束时间仍未登记离校，请复核。', 'ACCESS_RECORD', 7, 'UNREAD', NULL),
(8, 13, 'USER', '今日访客统计已更新', '今日访客、当前在校、超时未离校和审批待处理数据已更新。', 'STATISTICS', NULL, 'READ', NOW()),
(9, 1, 'USER', '黑名单拦截记录', '系统已拦截两条黑名单访客预约申请，请在黑名单管理中查看。', 'BLACKLIST', 1, 'READ', NOW()),
(10, 1, 'USER', '自动截图任务待执行', '截图记录已初始化，后续可执行 Playwright 自动截图脚本。', 'SCREENSHOT', NULL, 'UNREAD', NULL);

INSERT INTO operation_log (id, operator_user_id, operator_name, module_name, operation_type, request_method, request_url, operation_result, ip_address, operation_time, error_message) VALUES
(1, 1, '系统管理员', '系统初始化', 'INIT_DATA', 'SQL', 'database/seed.sql', 'SUCCESS', '127.0.0.1', DATE_ADD(NOW(), INTERVAL -10 DAY), NULL),
(2, 1, '系统管理员', '用户管理', 'CREATE', 'POST', '/api/sys-users', 'SUCCESS', '127.0.0.1', DATE_ADD(NOW(), INTERVAL -9 DAY), NULL),
(3, 3, '张晓峰', '被访人确认', 'APPROVE', 'POST', '/api/workflow/host/1/confirm', 'SUCCESS', '10.10.1.20', DATE_ADD(CURRENT_DATE(), INTERVAL 8 HOUR), NULL),
(4, 7, '李审批', '部门审批', 'APPROVE', 'POST', '/api/workflow/department/1/approve', 'SUCCESS', '10.10.1.30', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 8 HOUR), INTERVAL 10 MINUTE), NULL),
(5, 11, '王安保', '门岗核验', 'VERIFY', 'POST', '/api/workflow/gate/verify', 'SUCCESS', '10.10.2.10', DATE_ADD(CURRENT_DATE(), INTERVAL 9 HOUR), NULL),
(6, 11, '王安保', '入校登记', 'ENTRY', 'POST', '/api/workflow/access/entry', 'SUCCESS', '10.10.2.10', DATE_ADD(CURRENT_DATE(), INTERVAL 9 HOUR), NULL),
(7, 12, '孙安保', '离校登记', 'EXIT', 'POST', '/api/workflow/access/exit', 'SUCCESS', '10.10.2.11', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 10 HOUR), INTERVAL 40 MINUTE), NULL),
(8, 3, '张晓峰', '被访人确认', 'APPROVE', 'POST', '/api/workflow/host/5/confirm', 'SUCCESS', '10.10.1.20', DATE_ADD(NOW(), INTERVAL -4 HOUR), NULL),
(9, 5, '陈嘉宁', '被访人确认', 'REJECT', 'POST', '/api/workflow/host/7/reject', 'SUCCESS', '10.10.1.22', DATE_ADD(NOW(), INTERVAL -20 HOUR), NULL),
(10, 7, '李审批', '部门审批', 'REJECT', 'POST', '/api/workflow/department/8/reject', 'SUCCESS', '10.10.1.30', DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL -2 DAY), INTERVAL 10 HOUR), NULL),
(11, 1, '系统管理员', '黑名单管理', 'CREATE', 'POST', '/api/blacklists', 'SUCCESS', '127.0.0.1', DATE_ADD(NOW(), INTERVAL -30 DAY), NULL),
(12, 11, '王安保', '门岗核验', 'VERIFY', 'POST', '/api/workflow/gate/verify', 'FAILED', '10.10.2.10', NOW(), '通行码不存在或已过期'),
(13, 11, '王安保', '超时未离校', 'MARK_OVERTIME', 'GET', '/api/workflow/access/overtime?mark=true', 'SUCCESS', '10.10.2.10', DATE_ADD(CURRENT_DATE(), INTERVAL 11 HOUR), NULL),
(14, 12, '孙安保', '超时未离校', 'MARK_OVERTIME', 'GET', '/api/workflow/access/overtime?mark=true', 'SUCCESS', '10.10.2.11', DATE_ADD(CURRENT_DATE(), INTERVAL 10 HOUR), NULL),
(15, 13, '周校管', '统计报表', 'QUERY', 'GET', '/api/statistics/overview', 'SUCCESS', '10.10.3.10', NOW(), NULL),
(16, 1, '系统管理员', '角色权限管理', 'UPDATE', 'PUT', '/api/sys-roles/5', 'SUCCESS', '127.0.0.1', DATE_ADD(NOW(), INTERVAL -8 DAY), NULL),
(17, 1, '系统管理员', '部门管理', 'UPDATE', 'PUT', '/api/departments/2', 'SUCCESS', '127.0.0.1', DATE_ADD(NOW(), INTERVAL -7 DAY), NULL),
(18, 1, '系统管理员', '校门管理', 'UPDATE', 'PUT', '/api/campus-gates/5', 'SUCCESS', '127.0.0.1', DATE_ADD(NOW(), INTERVAL -6 DAY), NULL),
(19, 1, '系统管理员', '自动截图', 'CREATE_TASK', 'POST', '/api/screenshot-records', 'SUCCESS', '127.0.0.1', DATE_ADD(NOW(), INTERVAL -1 DAY), NULL),
(20, 1, '系统管理员', '报告生成', 'CREATE_TASK', 'POST', '/api/report-records', 'SUCCESS', '127.0.0.1', DATE_ADD(NOW(), INTERVAL -1 DAY), NULL);

INSERT INTO screenshot_record (id, screenshot_code, page_name, route_path, role_code, file_path, description, capture_time, status, created_by) VALUES
(1, 'SCR-LOGIN', '登录页', '/login', 'ANONYMOUS', 'screenshots/login.png', '系统登录界面截图', NULL, 'PENDING', 1),
(2, 'SCR-DASHBOARD', '首页统计驾驶舱', '/dashboard', 'ADMIN', 'screenshots/dashboard.png', '首页统计卡片、趋势图和快捷入口截图', NULL, 'PENDING', 1),
(3, 'SCR-VISIT-APPLY', '访客预约申请页', '/visit/apply', 'VISITOR', 'screenshots/visit_apply.png', '访客提交预约表单截图', NULL, 'PENDING', 1),
(4, 'SCR-MY-APPLY', '我的预约页', '/visit/mine', 'VISITOR', 'screenshots/my_appointments.png', '访客查看个人预约状态截图', NULL, 'PENDING', 1),
(5, 'SCR-HOST-PENDING', '待确认预约页', '/approval/host', 'HOST', 'screenshots/host_pending.png', '被访人确认预约截图', NULL, 'PENDING', 1),
(6, 'SCR-DEPT-APPROVAL', '部门审批页', '/approval/department', 'DEPT_APPROVER', 'screenshots/department_approval.png', '部门审批人员处理预约截图', NULL, 'PENDING', 1),
(7, 'SCR-GATE-VERIFY', '门岗核验页', '/gate/verify', 'GATE_GUARD', 'screenshots/gate_verify.png', '门岗核验通行码截图', NULL, 'PENDING', 1),
(8, 'SCR-ENTRY', '入校登记页', '/access/entry', 'GATE_GUARD', 'screenshots/entry.png', '入校登记业务截图', NULL, 'PENDING', 1),
(9, 'SCR-EXIT', '离校登记页', '/access/exit', 'GATE_GUARD', 'screenshots/exit.png', '离校登记业务截图', NULL, 'PENDING', 1),
(10, 'SCR-CURRENT', '当前在校访客页', '/access/current', 'GATE_GUARD', 'screenshots/current_visitors.png', '当前在校访客列表截图', NULL, 'PENDING', 1),
(11, 'SCR-OVERTIME', '超时未离校访客页', '/access/overtime', 'GATE_GUARD', 'screenshots/overtime_visitors.png', '超时未离校访客列表截图', NULL, 'PENDING', 1),
(12, 'SCR-BLACKLIST', '黑名单管理页', '/security/blacklist', 'ADMIN', 'screenshots/blacklist.png', '黑名单管理截图', NULL, 'PENDING', 1),
(13, 'SCR-RECORDS', '访客记录查询页', '/records', 'SCHOOL_MANAGER', 'screenshots/visitor_records.png', '访客历史记录查询截图', NULL, 'PENDING', 1),
(14, 'SCR-REPORTS', '统计报表页', '/reports', 'SCHOOL_MANAGER', 'screenshots/reports.png', '统计报表页面截图', NULL, 'PENDING', 1),
(15, 'SCR-USERS', '用户管理页', '/system/users', 'ADMIN', 'screenshots/users.png', '用户管理页面截图', NULL, 'PENDING', 1),
(16, 'SCR-LOGS', '系统日志页', '/system/logs', 'ADMIN', 'screenshots/logs.png', '系统日志页面截图', NULL, 'PENDING', 1);

INSERT INTO report_record (id, report_code, report_name, markdown_path, word_path, generate_status, generate_time, generated_by, error_message) VALUES
(1, 'RPT-COURSE-DESIGN', '重庆邮电大学智慧访客预约与出入校管理系统设计报告', 'docs/重庆邮电大学智慧访客预约与出入校管理系统设计报告.md', 'docs/重庆邮电大学智慧访客预约与出入校管理系统设计报告.docx', 'PENDING', NULL, 1, NULL),
(2, 'RPT-DIAGRAMS', '系统设计图汇总文档', 'docs/diagrams.md', NULL, 'SUCCESS', NOW(), 1, NULL);