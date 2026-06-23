-- 重庆邮电大学智慧访客预约与出入校管理系统
-- MySQL 8 schema script

USE cqupt_visitor_system;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS report_record;
DROP TABLE IF EXISTS screenshot_record;
DROP TABLE IF EXISTS dict_item;
DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS notice;
DROP TABLE IF EXISTS blacklist;
DROP TABLE IF EXISTS access_record;
DROP TABLE IF EXISTS pass_code;
DROP TABLE IF EXISTS approval_record;
DROP TABLE IF EXISTS visitor_companion;
DROP TABLE IF EXISTS visit_apply;
DROP TABLE IF EXISTS visitor_vehicle;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS dict_type;
DROP TABLE IF EXISTS visitor;
DROP TABLE IF EXISTS campus_gate;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS department;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE department (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门主键',
  parent_id BIGINT NULL COMMENT '上级部门',
  dept_code VARCHAR(50) NOT NULL COMMENT '部门编码',
  dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
  leader_user_id BIGINT NULL COMMENT '部门负责人用户编号',
  phone VARCHAR(20) NULL COMMENT '联系电话',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
  status VARCHAR(30) NOT NULL DEFAULT 'ENABLED' COMMENT '部门状态：ENABLED、DISABLED',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记：0未删除，1已删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_department_code (dept_code),
  KEY idx_department_parent (parent_id),
  KEY idx_department_leader (leader_user_id),
  KEY idx_department_status (status),
  CONSTRAINT fk_department_parent FOREIGN KEY (parent_id) REFERENCES department(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

CREATE TABLE sys_role (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色主键',
  role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
  role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
  role_desc VARCHAR(255) NULL COMMENT '角色说明',
  status VARCHAR(30) NOT NULL DEFAULT 'ENABLED' COMMENT '角色状态：ENABLED、DISABLED',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_role_code (role_code),
  KEY idx_sys_role_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

CREATE TABLE sys_permission (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限主键',
  permission_code VARCHAR(100) NOT NULL COMMENT '权限编码',
  permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
  permission_type VARCHAR(30) NOT NULL DEFAULT 'MENU' COMMENT '权限类型：MENU、BUTTON、API',
  parent_id BIGINT NULL COMMENT '父级权限',
  route_path VARCHAR(200) NULL COMMENT '前端路由',
  component_path VARCHAR(200) NULL COMMENT '前端组件路径',
  api_path VARCHAR(200) NULL COMMENT '后端接口路径',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
  status VARCHAR(30) NOT NULL DEFAULT 'ENABLED' COMMENT '权限状态',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_permission_code (permission_code),
  KEY idx_sys_permission_parent (parent_id),
  KEY idx_sys_permission_type (permission_type),
  CONSTRAINT fk_permission_parent FOREIGN KEY (parent_id) REFERENCES sys_permission(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

CREATE TABLE campus_gate (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '校门主键',
  gate_code VARCHAR(50) NOT NULL COMMENT '校门编码',
  gate_name VARCHAR(100) NOT NULL COMMENT '校门名称',
  gate_location VARCHAR(200) NULL COMMENT '校门位置',
  gate_type VARCHAR(30) NOT NULL DEFAULT 'NORMAL' COMMENT '校门类型：NORMAL、VEHICLE、PEDESTRIAN',
  status VARCHAR(30) NOT NULL DEFAULT 'ENABLED' COMMENT '校门状态',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_campus_gate_code (gate_code),
  KEY idx_campus_gate_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='校门表';

CREATE TABLE visitor (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '访客主键',
  visitor_name VARCHAR(50) NOT NULL COMMENT '访客姓名',
  id_type VARCHAR(30) NOT NULL DEFAULT 'ID_CARD' COMMENT '证件类型',
  id_number VARCHAR(50) NOT NULL COMMENT '证件号码',
  phone VARCHAR(20) NOT NULL COMMENT '手机号',
  company VARCHAR(100) NULL COMMENT '单位',
  gender VARCHAR(10) NULL COMMENT '性别',
  visitor_level VARCHAR(30) NOT NULL DEFAULT 'NORMAL' COMMENT '访客等级：NORMAL、VIP、RISK',
  status VARCHAR(30) NOT NULL DEFAULT 'NORMAL' COMMENT '访客状态',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_visitor_id_number (id_type, id_number),
  KEY idx_visitor_phone (phone),
  KEY idx_visitor_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客表';

CREATE TABLE dict_type (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典类型主键',
  type_code VARCHAR(50) NOT NULL COMMENT '字典类型编码',
  type_name VARCHAR(100) NOT NULL COMMENT '字典类型名称',
  status VARCHAR(30) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_dict_type_code (type_code),
  KEY idx_dict_type_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

CREATE TABLE sys_user (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户主键',
  username VARCHAR(50) NOT NULL COMMENT '登录账号',
  password_hash VARCHAR(255) NOT NULL COMMENT '加密后的密码',
  real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
  phone VARCHAR(20) NOT NULL COMMENT '手机号',
  email VARCHAR(100) NULL COMMENT '邮箱',
  department_id BIGINT NULL COMMENT '所属部门',
  user_type VARCHAR(30) NOT NULL COMMENT '用户类型：HOST、APPROVER、GUARD、ADMIN、MANAGER',
  status VARCHAR(30) NOT NULL DEFAULT 'ENABLED' COMMENT '账号状态',
  last_login_time DATETIME NULL COMMENT '最近登录时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_user_username (username),
  KEY idx_sys_user_phone (phone),
  KEY idx_sys_user_department (department_id),
  KEY idx_sys_user_status (status),
  CONSTRAINT fk_user_department FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

ALTER TABLE department
  ADD CONSTRAINT fk_department_leader FOREIGN KEY (leader_user_id) REFERENCES sys_user(id) ON DELETE SET NULL;

CREATE TABLE sys_user_role (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联主键',
  user_id BIGINT NOT NULL COMMENT '用户编号',
  role_id BIGINT NOT NULL COMMENT '角色编号',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_role (user_id, role_id),
  KEY idx_user_role_role (role_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

CREATE TABLE sys_role_permission (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联主键',
  role_id BIGINT NOT NULL COMMENT '角色编号',
  permission_id BIGINT NOT NULL COMMENT '权限编号',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_permission (role_id, permission_id),
  KEY idx_role_permission_permission (permission_id),
  CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
  CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

CREATE TABLE visitor_vehicle (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '车辆主键',
  visitor_id BIGINT NOT NULL COMMENT '访客编号',
  plate_no VARCHAR(20) NOT NULL COMMENT '车牌号',
  vehicle_type VARCHAR(30) NOT NULL DEFAULT 'CAR' COMMENT '车辆类型',
  color VARCHAR(30) NULL COMMENT '车辆颜色',
  brand VARCHAR(50) NULL COMMENT '品牌',
  status VARCHAR(30) NOT NULL DEFAULT 'NORMAL' COMMENT '车辆状态',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_vehicle_plate (plate_no),
  KEY idx_vehicle_visitor (visitor_id),
  CONSTRAINT fk_vehicle_visitor FOREIGN KEY (visitor_id) REFERENCES visitor(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客车辆表';

CREATE TABLE visit_apply (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约主键',
  apply_no VARCHAR(50) NOT NULL COMMENT '预约编号',
  visitor_id BIGINT NOT NULL COMMENT '访客编号',
  host_user_id BIGINT NOT NULL COMMENT '被访人编号',
  department_id BIGINT NOT NULL COMMENT '访问部门编号',
  vehicle_id BIGINT NULL COMMENT '车辆编号',
  visit_reason VARCHAR(255) NOT NULL COMMENT '来访事由',
  plan_start_time DATETIME NOT NULL COMMENT '计划开始时间',
  plan_end_time DATETIME NOT NULL COMMENT '计划结束时间',
  apply_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_HOST' COMMENT '预约状态',
  access_status VARCHAR(30) NOT NULL DEFAULT 'NOT_ENTERED' COMMENT '通行状态',
  companion_count INT NOT NULL DEFAULT 0 COMMENT '随行人数',
  submit_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  cancel_time DATETIME NULL COMMENT '取消时间',
  cancel_reason VARCHAR(255) NULL COMMENT '取消原因',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_visit_apply_no (apply_no),
  KEY idx_apply_visitor (visitor_id),
  KEY idx_apply_host (host_user_id),
  KEY idx_apply_department (department_id),
  KEY idx_apply_status (apply_status),
  KEY idx_apply_plan_time (plan_start_time, plan_end_time),
  CONSTRAINT fk_apply_visitor FOREIGN KEY (visitor_id) REFERENCES visitor(id) ON DELETE RESTRICT,
  CONSTRAINT fk_apply_host FOREIGN KEY (host_user_id) REFERENCES sys_user(id) ON DELETE RESTRICT,
  CONSTRAINT fk_apply_department FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE RESTRICT,
  CONSTRAINT fk_apply_vehicle FOREIGN KEY (vehicle_id) REFERENCES visitor_vehicle(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客预约表';

CREATE TABLE visitor_companion (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '随行人员主键',
  apply_id BIGINT NOT NULL COMMENT '预约编号',
  companion_name VARCHAR(50) NOT NULL COMMENT '随行人员姓名',
  id_type VARCHAR(30) NOT NULL DEFAULT 'ID_CARD' COMMENT '证件类型',
  id_number VARCHAR(50) NOT NULL COMMENT '证件号码',
  phone VARCHAR(20) NULL COMMENT '手机号',
  relation_remark VARCHAR(100) NULL COMMENT '关系说明',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  KEY idx_companion_apply (apply_id),
  KEY idx_companion_id_number (id_number),
  CONSTRAINT fk_companion_apply FOREIGN KEY (apply_id) REFERENCES visit_apply(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='随行人员表';

CREATE TABLE approval_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '审批记录主键',
  apply_id BIGINT NOT NULL COMMENT '预约编号',
  approval_step VARCHAR(30) NOT NULL COMMENT '审批环节：HOST_CONFIRM、DEPT_APPROVAL',
  approver_user_id BIGINT NOT NULL COMMENT '审批人编号',
  approval_result VARCHAR(30) NOT NULL COMMENT '审批结果：APPROVED、REJECTED、PENDING',
  approval_comment VARCHAR(255) NULL COMMENT '审批意见',
  approval_time DATETIME NULL COMMENT '审批时间',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '审批顺序',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  KEY idx_approval_apply (apply_id),
  KEY idx_approval_approver (approver_user_id),
  KEY idx_approval_step_result (approval_step, approval_result),
  CONSTRAINT fk_approval_apply FOREIGN KEY (apply_id) REFERENCES visit_apply(id) ON DELETE CASCADE,
  CONSTRAINT fk_approval_user FOREIGN KEY (approver_user_id) REFERENCES sys_user(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批记录表';

CREATE TABLE pass_code (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '通行凭证主键',
  apply_id BIGINT NOT NULL COMMENT '预约编号',
  pass_code VARCHAR(64) NOT NULL COMMENT '通行码',
  qr_content VARCHAR(500) NOT NULL COMMENT '二维码内容',
  valid_from DATETIME NOT NULL COMMENT '有效开始时间',
  valid_to DATETIME NOT NULL COMMENT '有效结束时间',
  pass_status VARCHAR(30) NOT NULL DEFAULT 'VALID' COMMENT '凭证状态：VALID、USED、EXPIRED、DISABLED',
  used_time DATETIME NULL COMMENT '首次使用时间',
  verify_count INT NOT NULL DEFAULT 0 COMMENT '核验次数',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pass_code (pass_code),
  UNIQUE KEY uk_pass_apply (apply_id),
  KEY idx_pass_status_time (pass_status, valid_from, valid_to),
  CONSTRAINT fk_pass_apply FOREIGN KEY (apply_id) REFERENCES visit_apply(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通行凭证表';

CREATE TABLE access_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '出入校记录主键',
  apply_id BIGINT NOT NULL COMMENT '预约编号',
  visitor_id BIGINT NOT NULL COMMENT '访客编号',
  pass_code_id BIGINT NOT NULL COMMENT '通行凭证编号',
  entry_gate_id BIGINT NULL COMMENT '入校校门',
  exit_gate_id BIGINT NULL COMMENT '离校校门',
  entry_guard_id BIGINT NULL COMMENT '入校经办人',
  exit_guard_id BIGINT NULL COMMENT '离校经办人',
  entry_time DATETIME NULL COMMENT '入校时间',
  exit_time DATETIME NULL COMMENT '离校时间',
  access_status VARCHAR(30) NOT NULL DEFAULT 'ENTERED' COMMENT '访问状态：ENTERED、EXITED、OVERTIME、ABNORMAL',
  overtime_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否超时未离校',
  abnormal_reason VARCHAR(255) NULL COMMENT '异常原因',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  KEY idx_access_apply (apply_id),
  KEY idx_access_visitor (visitor_id),
  KEY idx_access_entry_gate (entry_gate_id),
  KEY idx_access_exit_gate (exit_gate_id),
  KEY idx_access_status_time (access_status, entry_time, exit_time),
  CONSTRAINT fk_access_apply FOREIGN KEY (apply_id) REFERENCES visit_apply(id) ON DELETE CASCADE,
  CONSTRAINT fk_access_visitor FOREIGN KEY (visitor_id) REFERENCES visitor(id) ON DELETE RESTRICT,
  CONSTRAINT fk_access_pass FOREIGN KEY (pass_code_id) REFERENCES pass_code(id) ON DELETE RESTRICT,
  CONSTRAINT fk_access_entry_gate FOREIGN KEY (entry_gate_id) REFERENCES campus_gate(id) ON DELETE SET NULL,
  CONSTRAINT fk_access_exit_gate FOREIGN KEY (exit_gate_id) REFERENCES campus_gate(id) ON DELETE SET NULL,
  CONSTRAINT fk_access_entry_guard FOREIGN KEY (entry_guard_id) REFERENCES sys_user(id) ON DELETE SET NULL,
  CONSTRAINT fk_access_exit_guard FOREIGN KEY (exit_guard_id) REFERENCES sys_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出入校记录表';

CREATE TABLE blacklist (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '黑名单主键',
  visitor_id BIGINT NULL COMMENT '访客编号',
  id_number VARCHAR(50) NOT NULL COMMENT '证件号码',
  phone VARCHAR(20) NULL COMMENT '手机号',
  reason VARCHAR(255) NOT NULL COMMENT '加入原因',
  level VARCHAR(30) NOT NULL DEFAULT 'WARNING' COMMENT '黑名单等级：WARNING、LIMITED、FORBIDDEN',
  start_time DATETIME NOT NULL COMMENT '生效时间',
  end_time DATETIME NULL COMMENT '失效时间',
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE、EXPIRED、REMOVED',
  operator_user_id BIGINT NOT NULL COMMENT '操作人',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  KEY idx_blacklist_visitor (visitor_id),
  KEY idx_blacklist_id_number (id_number),
  KEY idx_blacklist_status_time (status, start_time, end_time),
  CONSTRAINT fk_blacklist_visitor FOREIGN KEY (visitor_id) REFERENCES visitor(id) ON DELETE SET NULL,
  CONSTRAINT fk_blacklist_operator FOREIGN KEY (operator_user_id) REFERENCES sys_user(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='黑名单表';

CREATE TABLE notice (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知主键',
  receiver_user_id BIGINT NULL COMMENT '接收用户',
  receiver_type VARCHAR(30) NOT NULL DEFAULT 'USER' COMMENT '接收对象类型',
  title VARCHAR(100) NOT NULL COMMENT '标题',
  content VARCHAR(1000) NOT NULL COMMENT '内容',
  business_type VARCHAR(50) NULL COMMENT '业务类型',
  business_id BIGINT NULL COMMENT '业务编号',
  read_status VARCHAR(30) NOT NULL DEFAULT 'UNREAD' COMMENT '阅读状态',
  read_time DATETIME NULL COMMENT '阅读时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  KEY idx_notice_receiver (receiver_user_id, read_status),
  KEY idx_notice_business (business_type, business_id),
  CONSTRAINT fk_notice_receiver FOREIGN KEY (receiver_user_id) REFERENCES sys_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知消息表';

CREATE TABLE operation_log (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  operator_user_id BIGINT NULL COMMENT '操作用户',
  operator_name VARCHAR(50) NULL COMMENT '操作人姓名',
  module_name VARCHAR(100) NOT NULL COMMENT '模块名称',
  operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
  request_method VARCHAR(10) NOT NULL COMMENT '请求方法',
  request_url VARCHAR(255) NOT NULL COMMENT '请求地址',
  operation_result VARCHAR(30) NOT NULL DEFAULT 'SUCCESS' COMMENT '操作结果',
  ip_address VARCHAR(50) NULL COMMENT 'IP地址',
  operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  error_message VARCHAR(1000) NULL COMMENT '错误信息',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  KEY idx_log_operator (operator_user_id),
  KEY idx_log_module_time (module_name, operation_time),
  KEY idx_log_result_time (operation_result, operation_time),
  CONSTRAINT fk_log_operator FOREIGN KEY (operator_user_id) REFERENCES sys_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

CREATE TABLE dict_item (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典项主键',
  type_id BIGINT NOT NULL COMMENT '字典类型编号',
  item_code VARCHAR(50) NOT NULL COMMENT '字典项编码',
  item_name VARCHAR(100) NOT NULL COMMENT '字典项名称',
  item_value VARCHAR(100) NOT NULL COMMENT '字典项值',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
  status VARCHAR(30) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_dict_item (type_id, item_code),
  KEY idx_dict_item_type (type_id, status),
  CONSTRAINT fk_dict_item_type FOREIGN KEY (type_id) REFERENCES dict_type(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项表';

CREATE TABLE screenshot_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '截图记录主键',
  screenshot_code VARCHAR(50) NOT NULL COMMENT '截图编号',
  page_name VARCHAR(100) NOT NULL COMMENT '页面名称',
  route_path VARCHAR(200) NOT NULL COMMENT '前端路由',
  role_code VARCHAR(50) NOT NULL COMMENT '截图角色',
  file_path VARCHAR(255) NOT NULL COMMENT '截图文件路径',
  description VARCHAR(255) NULL COMMENT '说明',
  capture_time DATETIME NULL COMMENT '截图时间',
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING、SUCCESS、FAILED',
  created_by BIGINT NULL COMMENT '创建用户',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_screenshot_code (screenshot_code),
  KEY idx_screenshot_status (status),
  KEY idx_screenshot_created_by (created_by),
  CONSTRAINT fk_screenshot_created_by FOREIGN KEY (created_by) REFERENCES sys_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='截图记录表';

CREATE TABLE report_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '报告记录主键',
  report_code VARCHAR(50) NOT NULL COMMENT '报告编号',
  report_name VARCHAR(100) NOT NULL COMMENT '报告名称',
  markdown_path VARCHAR(255) NOT NULL COMMENT 'Markdown路径',
  word_path VARCHAR(255) NULL COMMENT 'Word路径',
  generate_status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '生成状态：PENDING、SUCCESS、FAILED',
  generate_time DATETIME NULL COMMENT '生成时间',
  generated_by BIGINT NULL COMMENT '生成人',
  error_message VARCHAR(1000) NULL COMMENT '错误信息',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_report_code (report_code),
  KEY idx_report_status (generate_status),
  KEY idx_report_generated_by (generated_by),
  CONSTRAINT fk_report_generated_by FOREIGN KEY (generated_by) REFERENCES sys_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报告记录表';

