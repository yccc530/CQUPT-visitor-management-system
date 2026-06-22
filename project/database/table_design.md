# 数据库表设计

## 设计约定

- 数据库：MySQL 8。
- 表名和字段名：统一使用小写下划线命名。
- 主键：所有表统一使用 `id BIGINT`。
- 公共时间字段：统一使用 `create_time`、`update_time`。
- 软删除字段：需要保留历史的表统一使用 `deleted`，`0` 表示未删除，`1` 表示已删除。
- 状态字段：统一使用 `VARCHAR(30)`，具体含义通过字典表或业务常量说明。
- 字符集：后续建表 SQL 建议使用 `utf8mb4`。

## 1. sys_user

表名：`sys_user`  
中文名称：系统用户表  
设计目的：存储被访人、部门审批人员、门岗安保人员、系统管理员和校级管理人员等系统账号。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 用户主键 |
| username | VARCHAR(50) | 否 | 否 | 否 | 无 | 登录账号，唯一 |
| password_hash | VARCHAR(255) | 否 | 否 | 否 | 无 | 加密后的密码 |
| real_name | VARCHAR(50) | 否 | 否 | 否 | 无 | 真实姓名 |
| phone | VARCHAR(20) | 否 | 否 | 否 | 无 | 手机号 |
| email | VARCHAR(100) | 否 | 否 | 是 | NULL | 邮箱 |
| department_id | BIGINT | 否 | department(id) | 是 | NULL | 所属部门 |
| user_type | VARCHAR(30) | 否 | 否 | 否 | 无 | 用户类型，如 HOST、APPROVER、GUARD、ADMIN、MANAGER |
| status | VARCHAR(30) | 否 | 否 | 否 | ENABLED | 账号状态 |
| last_login_time | DATETIME | 否 | 否 | 是 | NULL | 最近登录时间 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个部门包含多个系统用户；一个用户可通过 `sys_user_role` 拥有多个角色；一个用户可作为被访人、审批人、门岗经办人、日志操作人。  
索引建议：唯一索引 `uk_sys_user_username(username)`；索引 `idx_sys_user_phone(phone)`、`idx_sys_user_department(department_id)`、`idx_sys_user_status(status)`。

## 2. sys_role

表名：`sys_role`  
中文名称：角色表  
设计目的：存储系统角色，用于 RBAC 权限控制。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 角色主键 |
| role_code | VARCHAR(50) | 否 | 否 | 否 | 无 | 角色编码，唯一 |
| role_name | VARCHAR(50) | 否 | 否 | 否 | 无 | 角色名称 |
| role_desc | VARCHAR(255) | 否 | 否 | 是 | NULL | 角色说明 |
| status | VARCHAR(30) | 否 | 否 | 否 | ENABLED | 角色状态 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：角色通过 `sys_user_role` 分配给用户，通过 `sys_role_permission` 关联权限。  
索引建议：唯一索引 `uk_sys_role_code(role_code)`；索引 `idx_sys_role_status(status)`。

## 3. sys_permission

表名：`sys_permission`  
中文名称：权限表  
设计目的：存储菜单、按钮和接口权限。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 权限主键 |
| permission_code | VARCHAR(100) | 否 | 否 | 否 | 无 | 权限编码，唯一 |
| permission_name | VARCHAR(100) | 否 | 否 | 否 | 无 | 权限名称 |
| permission_type | VARCHAR(30) | 否 | 否 | 否 | MENU | 权限类型：MENU、BUTTON、API |
| parent_id | BIGINT | 否 | sys_permission(id) | 是 | NULL | 父级权限 |
| route_path | VARCHAR(200) | 否 | 否 | 是 | NULL | 前端路由 |
| component_path | VARCHAR(200) | 否 | 否 | 是 | NULL | 前端组件路径 |
| api_path | VARCHAR(200) | 否 | 否 | 是 | NULL | 后端接口路径 |
| sort_order | INT | 否 | 否 | 否 | 0 | 排序号 |
| status | VARCHAR(30) | 否 | 否 | 否 | ENABLED | 权限状态 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：权限可以形成树形菜单结构，并通过 `sys_role_permission` 授予角色。  
索引建议：唯一索引 `uk_sys_permission_code(permission_code)`；索引 `idx_sys_permission_parent(parent_id)`、`idx_sys_permission_type(permission_type)`。

## 4. sys_user_role

表名：`sys_user_role`  
中文名称：用户角色关联表  
设计目的：实现用户与角色之间的多对多关系。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 关联主键 |
| user_id | BIGINT | 否 | sys_user(id) | 否 | 无 | 用户编号 |
| role_id | BIGINT | 否 | sys_role(id) | 否 | 无 | 角色编号 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个用户可以拥有多个角色，一个角色可以分配给多个用户。  
索引建议：唯一索引 `uk_user_role(user_id, role_id)`；索引 `idx_user_role_role(role_id)`。

## 5. sys_role_permission

表名：`sys_role_permission`  
中文名称：角色权限关联表  
设计目的：实现角色与权限之间的多对多关系。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 关联主键 |
| role_id | BIGINT | 否 | sys_role(id) | 否 | 无 | 角色编号 |
| permission_id | BIGINT | 否 | sys_permission(id) | 否 | 无 | 权限编号 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个角色可以拥有多个权限，一个权限可以分配给多个角色。  
索引建议：唯一索引 `uk_role_permission(role_id, permission_id)`；索引 `idx_role_permission_permission(permission_id)`。

## 6. department

表名：`department`  
中文名称：部门表  
设计目的：存储学校部门组织结构，用于被访人归属、部门审批和统计分析。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 部门主键 |
| parent_id | BIGINT | 否 | department(id) | 是 | NULL | 上级部门 |
| dept_code | VARCHAR(50) | 否 | 否 | 否 | 无 | 部门编码，唯一 |
| dept_name | VARCHAR(100) | 否 | 否 | 否 | 无 | 部门名称 |
| leader_user_id | BIGINT | 否 | sys_user(id) | 是 | NULL | 部门负责人 |
| phone | VARCHAR(20) | 否 | 否 | 是 | NULL | 联系电话 |
| sort_order | INT | 否 | 否 | 否 | 0 | 排序号 |
| status | VARCHAR(30) | 否 | 否 | 否 | ENABLED | 部门状态 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个部门包含多个用户，一个部门可关联多个访客预约。  
索引建议：唯一索引 `uk_department_code(dept_code)`；索引 `idx_department_parent(parent_id)`、`idx_department_status(status)`。

## 7. campus_gate

表名：`campus_gate`  
中文名称：校门表  
设计目的：存储校门基础信息，用于门岗核验、入校登记、离校登记和校门通行统计。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 校门主键 |
| gate_code | VARCHAR(50) | 否 | 否 | 否 | 无 | 校门编码，唯一 |
| gate_name | VARCHAR(100) | 否 | 否 | 否 | 无 | 校门名称 |
| gate_location | VARCHAR(200) | 否 | 否 | 是 | NULL | 校门位置 |
| gate_type | VARCHAR(30) | 否 | 否 | 否 | NORMAL | 校门类型 |
| status | VARCHAR(30) | 否 | 否 | 否 | ENABLED | 校门状态 |
| remark | VARCHAR(255) | 否 | 否 | 是 | NULL | 备注 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个校门可以关联多条入校和离校记录。  
索引建议：唯一索引 `uk_campus_gate_code(gate_code)`；索引 `idx_campus_gate_status(status)`。

## 8. visitor

表名：`visitor`  
中文名称：访客表  
设计目的：存储校外访客基础身份信息。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 访客主键 |
| visitor_name | VARCHAR(50) | 否 | 否 | 否 | 无 | 访客姓名 |
| id_type | VARCHAR(30) | 否 | 否 | 否 | ID_CARD | 证件类型 |
| id_number | VARCHAR(50) | 否 | 否 | 否 | 无 | 证件号码 |
| phone | VARCHAR(20) | 否 | 否 | 否 | 无 | 手机号 |
| gender | VARCHAR(10) | 否 | 否 | 是 | NULL | 性别 |
| unit_name | VARCHAR(100) | 否 | 否 | 是 | NULL | 所属单位 |
| visitor_type | VARCHAR(30) | 否 | 否 | 否 | NORMAL | 访客类型 |
| remark | VARCHAR(255) | 否 | 否 | 是 | NULL | 备注 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个访客可以拥有车辆、提交预约、被加入黑名单。  
索引建议：索引 `idx_visitor_id_number(id_number)`、`idx_visitor_phone(phone)`；可按业务设置联合唯一索引 `uk_visitor_identity(id_type, id_number)`。

## 9. visitor_vehicle

表名：`visitor_vehicle`  
中文名称：访客车辆表  
设计目的：存储访客车辆信息，支持预约关联车辆和门岗核验。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 车辆主键 |
| visitor_id | BIGINT | 否 | visitor(id) | 否 | 无 | 访客编号 |
| plate_number | VARCHAR(20) | 否 | 否 | 否 | 无 | 车牌号 |
| vehicle_type | VARCHAR(30) | 否 | 否 | 否 | CAR | 车辆类型 |
| vehicle_color | VARCHAR(30) | 否 | 否 | 是 | NULL | 车辆颜色 |
| brand | VARCHAR(50) | 否 | 否 | 是 | NULL | 车辆品牌 |
| status | VARCHAR(30) | 否 | 否 | 否 | ENABLED | 车辆状态 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个访客可以登记多辆车，一辆车可被多次预约引用。  
索引建议：唯一索引 `uk_vehicle_plate(plate_number)`；索引 `idx_vehicle_visitor(visitor_id)`。

## 10. visitor_companion

表名：`visitor_companion`  
中文名称：随行人员表  
设计目的：存储某次预约中的随行人员信息。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 随行人员主键 |
| apply_id | BIGINT | 否 | visit_apply(id) | 否 | 无 | 预约编号 |
| companion_name | VARCHAR(50) | 否 | 否 | 否 | 无 | 随行人员姓名 |
| id_type | VARCHAR(30) | 否 | 否 | 否 | ID_CARD | 证件类型 |
| id_number | VARCHAR(50) | 否 | 否 | 否 | 无 | 证件号码 |
| phone | VARCHAR(20) | 否 | 否 | 是 | NULL | 手机号 |
| relation_remark | VARCHAR(100) | 否 | 否 | 是 | NULL | 与访客关系或备注 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个预约申请可以包含多个随行人员。  
索引建议：索引 `idx_companion_apply(apply_id)`、`idx_companion_id_number(id_number)`。

## 11. visit_apply

表名：`visit_apply`  
中文名称：访客预约表  
设计目的：存储访客预约申请、审批状态和访问状态，是系统核心业务表。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 预约主键 |
| apply_no | VARCHAR(50) | 否 | 否 | 否 | 无 | 预约编号，唯一 |
| visitor_id | BIGINT | 否 | visitor(id) | 否 | 无 | 访客编号 |
| host_user_id | BIGINT | 否 | sys_user(id) | 否 | 无 | 被访人编号 |
| department_id | BIGINT | 否 | department(id) | 否 | 无 | 被访部门 |
| vehicle_id | BIGINT | 否 | visitor_vehicle(id) | 是 | NULL | 预约关联车辆 |
| visit_reason | VARCHAR(255) | 否 | 否 | 否 | 无 | 来访事由 |
| plan_start_time | DATETIME | 否 | 否 | 否 | 无 | 计划开始时间 |
| plan_end_time | DATETIME | 否 | 否 | 否 | 无 | 计划结束时间 |
| apply_status | VARCHAR(30) | 否 | 否 | 否 | PENDING_CONFIRM | 预约状态 |
| access_status | VARCHAR(30) | 否 | 否 | 否 | NOT_ENTERED | 访问状态 |
| companion_count | INT | 否 | 否 | 否 | 0 | 随行人员数量 |
| submit_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 提交时间 |
| cancel_time | DATETIME | 否 | 否 | 是 | NULL | 取消时间 |
| cancel_reason | VARCHAR(255) | 否 | 否 | 是 | NULL | 取消原因 |
| remark | VARCHAR(255) | 否 | 否 | 是 | NULL | 备注 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：预约关联访客、被访人、部门和车辆，可产生随行人员、审批记录、通行凭证和出入校记录。  
索引建议：唯一索引 `uk_visit_apply_no(apply_no)`；索引 `idx_apply_visitor(visitor_id)`、`idx_apply_host(host_user_id)`、`idx_apply_department(department_id)`、`idx_apply_status(apply_status)`、`idx_apply_plan_time(plan_start_time, plan_end_time)`。

## 12. approval_record

表名：`approval_record`  
中文名称：审批记录表  
设计目的：记录被访人确认、部门审批等审批环节及意见。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 审批记录主键 |
| apply_id | BIGINT | 否 | visit_apply(id) | 否 | 无 | 预约编号 |
| approval_step | VARCHAR(30) | 否 | 否 | 否 | 无 | 审批环节，如 HOST_CONFIRM、DEPT_APPROVAL |
| approver_user_id | BIGINT | 否 | sys_user(id) | 否 | 无 | 审批人 |
| approval_result | VARCHAR(30) | 否 | 否 | 否 | 无 | 审批结果 |
| approval_comment | VARCHAR(255) | 否 | 否 | 是 | NULL | 审批意见 |
| approval_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 审批时间 |
| sort_order | INT | 否 | 否 | 否 | 0 | 审批顺序 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个预约可以产生多条审批记录，一个用户可以处理多条审批记录。  
索引建议：索引 `idx_approval_apply(apply_id)`、`idx_approval_approver(approver_user_id)`、`idx_approval_result(approval_result)`。

## 13. pass_code

表名：`pass_code`  
中文名称：通行凭证表  
设计目的：存储审批通过后生成的通行码及有效期。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 通行凭证主键 |
| apply_id | BIGINT | 否 | visit_apply(id) | 否 | 无 | 预约编号 |
| pass_code | VARCHAR(64) | 否 | 否 | 否 | 无 | 通行码，唯一 |
| qr_content | VARCHAR(255) | 否 | 否 | 是 | NULL | 二维码内容 |
| valid_from | DATETIME | 否 | 否 | 否 | 无 | 有效开始时间 |
| valid_to | DATETIME | 否 | 否 | 否 | 无 | 有效结束时间 |
| pass_status | VARCHAR(30) | 否 | 否 | 否 | VALID | 凭证状态 |
| used_time | DATETIME | 否 | 否 | 是 | NULL | 首次使用时间 |
| verify_count | INT | 否 | 否 | 否 | 0 | 核验次数 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个审批通过的预约通常生成一个通行凭证，通行凭证可用于门岗核验和出入校登记。  
索引建议：唯一索引 `uk_pass_code(pass_code)`；唯一索引 `uk_pass_apply(apply_id)`；索引 `idx_pass_status_time(pass_status, valid_from, valid_to)`。

## 14. access_record

表名：`access_record`  
中文名称：出入校记录表  
设计目的：记录访客入校、离校、当前在校和超时未离校状态。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 出入校记录主键 |
| apply_id | BIGINT | 否 | visit_apply(id) | 否 | 无 | 预约编号 |
| visitor_id | BIGINT | 否 | visitor(id) | 否 | 无 | 访客编号 |
| pass_code_id | BIGINT | 否 | pass_code(id) | 否 | 无 | 通行凭证编号 |
| entry_gate_id | BIGINT | 否 | campus_gate(id) | 是 | NULL | 入校校门 |
| exit_gate_id | BIGINT | 否 | campus_gate(id) | 是 | NULL | 离校校门 |
| entry_guard_id | BIGINT | 否 | sys_user(id) | 是 | NULL | 入校经办人 |
| exit_guard_id | BIGINT | 否 | sys_user(id) | 是 | NULL | 离校经办人 |
| entry_time | DATETIME | 否 | 否 | 是 | NULL | 入校时间 |
| exit_time | DATETIME | 否 | 否 | 是 | NULL | 离校时间 |
| access_status | VARCHAR(30) | 否 | 否 | 否 | NOT_ENTERED | 访问状态 |
| overtime_flag | TINYINT | 否 | 否 | 否 | 0 | 是否超时未离校 |
| abnormal_reason | VARCHAR(255) | 否 | 否 | 是 | NULL | 异常原因 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个预约可以产生出入校记录；校门可分别作为入校校门和离校校门；门岗用户可作为入校和离校经办人。  
索引建议：索引 `idx_access_apply(apply_id)`、`idx_access_visitor(visitor_id)`、`idx_access_status(access_status)`、`idx_access_entry_time(entry_time)`、`idx_access_exit_time(exit_time)`、`idx_access_gate(entry_gate_id, exit_gate_id)`。

## 15. blacklist

表名：`blacklist`  
中文名称：黑名单表  
设计目的：记录限制入校或高风险访客信息。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 黑名单主键 |
| visitor_id | BIGINT | 否 | visitor(id) | 是 | NULL | 访客编号 |
| id_number | VARCHAR(50) | 否 | 否 | 否 | 无 | 证件号码快照 |
| phone | VARCHAR(20) | 否 | 否 | 是 | NULL | 手机号快照 |
| reason | VARCHAR(255) | 否 | 否 | 否 | 无 | 加入原因 |
| level | VARCHAR(30) | 否 | 否 | 否 | NORMAL | 风险等级 |
| start_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 限制开始时间 |
| end_time | DATETIME | 否 | 否 | 是 | NULL | 限制结束时间 |
| status | VARCHAR(30) | 否 | 否 | 否 | ACTIVE | 黑名单状态 |
| operator_user_id | BIGINT | 否 | sys_user(id) | 否 | 无 | 操作人 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个访客可以对应多条黑名单历史记录，管理员负责维护黑名单。  
索引建议：索引 `idx_blacklist_visitor(visitor_id)`、`idx_blacklist_id_number(id_number)`、`idx_blacklist_phone(phone)`、`idx_blacklist_status(status)`。

## 16. notice

表名：`notice`  
中文名称：通知消息表  
设计目的：存储预约、审批和系统管理过程中的消息通知。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 通知主键 |
| receiver_user_id | BIGINT | 否 | sys_user(id) | 是 | NULL | 接收用户 |
| receiver_type | VARCHAR(30) | 否 | 否 | 否 | USER | 接收对象类型 |
| title | VARCHAR(100) | 否 | 否 | 否 | 无 | 通知标题 |
| content | TEXT | 否 | 否 | 否 | 无 | 通知内容 |
| business_type | VARCHAR(50) | 否 | 否 | 是 | NULL | 关联业务类型 |
| business_id | BIGINT | 否 | 否 | 是 | NULL | 关联业务编号 |
| read_status | VARCHAR(30) | 否 | 否 | 否 | UNREAD | 阅读状态 |
| read_time | DATETIME | 否 | 否 | 是 | NULL | 阅读时间 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个用户可接收多条通知，通知可关联预约、审批或系统事件。  
索引建议：索引 `idx_notice_receiver(receiver_user_id, read_status)`、`idx_notice_business(business_type, business_id)`。

## 17. operation_log

表名：`operation_log`  
中文名称：操作日志表  
设计目的：记录登录、审批、核验、出入校登记、黑名单维护等关键操作。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 日志主键 |
| operator_user_id | BIGINT | 否 | sys_user(id) | 是 | NULL | 操作用户 |
| operator_name | VARCHAR(50) | 否 | 否 | 是 | NULL | 操作人姓名快照 |
| module_name | VARCHAR(50) | 否 | 否 | 否 | 无 | 操作模块 |
| operation_type | VARCHAR(50) | 否 | 否 | 否 | 无 | 操作类型 |
| request_method | VARCHAR(20) | 否 | 否 | 是 | NULL | 请求方法 |
| request_url | VARCHAR(255) | 否 | 否 | 是 | NULL | 请求地址 |
| operation_result | VARCHAR(30) | 否 | 否 | 否 | SUCCESS | 操作结果 |
| ip_address | VARCHAR(50) | 否 | 否 | 是 | NULL | IP 地址 |
| operation_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 操作时间 |
| error_message | TEXT | 否 | 否 | 是 | NULL | 错误信息 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个用户可以产生多条操作日志；系统自动操作可允许操作用户为空。  
索引建议：索引 `idx_log_operator(operator_user_id)`、`idx_log_module(module_name)`、`idx_log_time(operation_time)`、`idx_log_result(operation_result)`。

## 18. dict_type

表名：`dict_type`  
中文名称：字典类型表  
设计目的：存储字典分类，如预约状态、访问状态、审批结果、证件类型等。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 字典类型主键 |
| dict_code | VARCHAR(50) | 否 | 否 | 否 | 无 | 字典编码，唯一 |
| dict_name | VARCHAR(100) | 否 | 否 | 否 | 无 | 字典名称 |
| description | VARCHAR(255) | 否 | 否 | 是 | NULL | 字典说明 |
| status | VARCHAR(30) | 否 | 否 | 否 | ENABLED | 状态 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个字典类型包含多个字典项。  
索引建议：唯一索引 `uk_dict_type_code(dict_code)`；索引 `idx_dict_type_status(status)`。

## 19. dict_item

表名：`dict_item`  
中文名称：字典项表  
设计目的：存储字典类型下的具体取值。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 字典项主键 |
| dict_type_id | BIGINT | 否 | dict_type(id) | 否 | 无 | 字典类型编号 |
| item_code | VARCHAR(50) | 否 | 否 | 否 | 无 | 字典项编码 |
| item_label | VARCHAR(100) | 否 | 否 | 否 | 无 | 显示名称 |
| item_value | VARCHAR(100) | 否 | 否 | 否 | 无 | 字典值 |
| sort_order | INT | 否 | 否 | 否 | 0 | 排序号 |
| status | VARCHAR(30) | 否 | 否 | 否 | ENABLED | 状态 |
| remark | VARCHAR(255) | 否 | 否 | 是 | NULL | 备注 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个字典类型下包含多个字典项。  
索引建议：唯一索引 `uk_dict_item_code(dict_type_id, item_code)`；索引 `idx_dict_item_type(dict_type_id)`、`idx_dict_item_status(status)`。

## 20. screenshot_record

表名：`screenshot_record`  
中文名称：截图记录表  
设计目的：记录 Playwright 自动截图的页面、文件路径和报告说明。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 截图记录主键 |
| screenshot_code | VARCHAR(50) | 否 | 否 | 否 | 无 | 截图编码，唯一 |
| page_name | VARCHAR(100) | 否 | 否 | 否 | 无 | 页面名称 |
| route_path | VARCHAR(200) | 否 | 否 | 否 | 无 | 前端路由 |
| role_code | VARCHAR(50) | 否 | 否 | 是 | NULL | 截图使用角色 |
| file_path | VARCHAR(255) | 否 | 否 | 否 | 无 | 截图文件路径 |
| description | VARCHAR(255) | 否 | 否 | 是 | NULL | 截图说明 |
| capture_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 截图时间 |
| status | VARCHAR(30) | 否 | 否 | 否 | SUCCESS | 截图状态 |
| created_by | BIGINT | 否 | sys_user(id) | 是 | NULL | 创建用户 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个用户或自动任务可以生成多条截图记录。  
索引建议：唯一索引 `uk_screenshot_code(screenshot_code)`；索引 `idx_screenshot_page(page_name)`、`idx_screenshot_status(status)`。

## 21. report_record

表名：`report_record`  
中文名称：报告记录表  
设计目的：记录自动报告生成的 Markdown、Word 输出路径和生成状态。

字段设计：

| 字段名 | 数据类型 | 主键 | 外键 | 是否为空 | 默认值 | 说明 |
|---|---|---|---|---|---|---|
| id | BIGINT | 是 | 否 | 否 | 自增 | 报告记录主键 |
| report_code | VARCHAR(50) | 否 | 否 | 否 | 无 | 报告编码，唯一 |
| report_name | VARCHAR(100) | 否 | 否 | 否 | 无 | 报告名称 |
| markdown_path | VARCHAR(255) | 否 | 否 | 是 | NULL | Markdown 文件路径 |
| word_path | VARCHAR(255) | 否 | 否 | 是 | NULL | Word 文件路径 |
| generate_status | VARCHAR(30) | 否 | 否 | 否 | PENDING | 生成状态 |
| generate_time | DATETIME | 否 | 否 | 是 | NULL | 生成时间 |
| generated_by | BIGINT | 否 | sys_user(id) | 是 | NULL | 生成人 |
| error_message | TEXT | 否 | 否 | 是 | NULL | 错误信息 |
| create_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 否 | 否 | 否 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 否 | 否 | 0 | 软删除标记 |

关系说明：一个用户或自动任务可以生成多条报告记录。  
索引建议：唯一索引 `uk_report_code(report_code)`；索引 `idx_report_status(generate_status)`、`idx_report_time(generate_time)`。

## 范式与冗余检查

1. 各表字段均为原子值，满足第一范式。
2. 多对多关系通过关联表拆分，非主属性依赖主键，满足第二范式。
3. 部门、校门、角色、权限、字典等基础数据独立成表，业务表通过外键引用，避免传递依赖，满足第三范式。
4. `blacklist.id_number`、`blacklist.phone` 和 `operation_log.operator_name` 属于风险检查和审计场景下的快照字段，是可解释的适度冗余。
