# API 接口说明

项目名称：重庆邮电大学智慧访客预约与出入校管理系统  
后端包名：`edu.cqupt.visitor`  
接口统一前缀：`/api`  
接口文档地址：`http://localhost:8080/swagger-ui.html`  
OpenAPI 地址：`http://localhost:8080/v3/api-docs`

## 1. 后端技术栈

- Spring Boot 3.3.2
- MyBatis Plus 3.5.7
- MySQL 8
- Spring Validation
- Springdoc OpenAPI
- Lombok

## 2. 启动方式

启动前请先执行数据库脚本：

```bash
mysql -u root -p < database/create_database.sql
mysql -u root -p cqupt_visitor_system < database/schema.sql
mysql -u root -p cqupt_visitor_system < database/seed.sql
```

然后启动后端。当前 Windows 中文路径环境下建议使用已验证的 JAR 方式：

```bash
cd backend
mvn -DskipTests package
java -jar target/cqupt-visitor-backend-0.1.0.jar
```

默认数据库连接配置位于 `backend/src/main/resources/application.yml`，可通过环境变量覆盖：

| 环境变量 | 默认值 | 说明 |
|---|---:|---|
| `DB_HOST` | `localhost` | MySQL 主机 |
| `DB_PORT` | `3306` | MySQL 端口 |
| `DB_NAME` | `cqupt_visitor_system` | 数据库名 |
| `DB_USERNAME` | `root` | 数据库账号 |
| `DB_PASSWORD` | `root` | 数据库密码 |

## 3. 统一返回格式

所有接口统一返回 `ApiResponse<T>`：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "success": true,
  "timestamp": "2026-06-22T19:00:00"
}
```

错误响应示例：

```json
{
  "code": 404,
  "message": "数据不存在",
  "data": null,
  "success": false,
  "timestamp": "2026-06-22T19:00:00"
}
```

## 4. 通用 CRUD 约定

每个基础模块均提供以下接口：

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/{module}?current=1&size=10` | 分页查询 |
| GET | `/api/{module}/{id}` | 查询详情 |
| POST | `/api/{module}` | 新增记录 |
| PUT | `/api/{module}/{id}` | 修改记录 |
| DELETE | `/api/{module}/{id}` | 删除记录，配合 MyBatis Plus 逻辑删除 |

## 5. 模块接口清单

| 模块 | 数据表 | Controller | 接口路径 | 说明 |
|---|---|---|---|---|
| 用户管理 | `sys_user` | `SysUserController` | `/api/sys-users` | 管理被访人、审批人员、门岗安保、管理员、校级管理人员账号 |
| 角色管理 | `sys_role` | `SysRoleController` | `/api/sys-roles` | 管理 RBAC 角色 |
| 权限管理 | `sys_permission` | `SysPermissionController` | `/api/sys-permissions` | 管理菜单、按钮、接口权限 |
| 用户角色关联 | `sys_user_role` | `SysUserRoleController` | `/api/sys-user-roles` | 维护用户与角色关系 |
| 角色权限关联 | `sys_role_permission` | `SysRolePermissionController` | `/api/sys-role-permissions` | 维护角色与权限关系 |
| 部门管理 | `department` | `DepartmentController` | `/api/departments` | 管理学校组织结构和审批部门 |
| 校门管理 | `campus_gate` | `CampusGateController` | `/api/campus-gates` | 管理校门基础信息 |
| 访客管理 | `visitor` | `VisitorController` | `/api/visitors` | 维护访客身份信息 |
| 访客车辆管理 | `visitor_vehicle` | `VisitorVehicleController` | `/api/visitor-vehicles` | 维护访客车辆信息 |
| 随行人员管理 | `visitor_companion` | `VisitorCompanionController` | `/api/visitor-companions` | 维护预约随行人员 |
| 预约申请管理 | `visit_apply` | `VisitApplyController` | `/api/visit-applies` | 管理访客预约申请 |
| 审批记录管理 | `approval_record` | `ApprovalRecordController` | `/api/approval-records` | 管理被访人确认和部门审批记录 |
| 通行凭证管理 | `pass_code` | `PassCodeController` | `/api/pass-codes` | 管理通行码和二维码凭证 |
| 出入校记录管理 | `access_record` | `AccessRecordController` | `/api/access-records` | 管理入校、离校、当前在校、超时未离校记录 |
| 黑名单管理 | `blacklist` | `BlacklistController` | `/api/blacklists` | 管理风险访客和拦截记录 |
| 通知消息管理 | `notice` | `NoticeController` | `/api/notices` | 管理业务通知 |
| 操作日志管理 | `operation_log` | `OperationLogController` | `/api/operation-logs` | 查询系统审计日志 |
| 字典类型管理 | `dict_type` | `DictTypeController` | `/api/dict-types` | 管理状态、类型等字典分类 |
| 字典项管理 | `dict_item` | `DictItemController` | `/api/dict-items` | 管理字典明细 |
| 截图记录管理 | `screenshot_record` | `ScreenshotRecordController` | `/api/screenshot-records` | 管理自动截图结果 |
| 报告记录管理 | `report_record` | `ReportRecordController` | `/api/report-records` | 管理报告生成记录 |

## 6. 重点接口示例

### 6.1 用户分页查询

- 方法：`GET`
- 路径：`/api/sys-users?current=1&size=10`
- 返回：用户分页列表
- 业务说明：用于系统管理员查看和维护校内系统账号。

### 6.2 新增访客

- 方法：`POST`
- 路径：`/api/visitors`
- 请求体示例：

```json
{
  "visitorName": "王小明",
  "idType": "ID_CARD",
  "idNumber": "500101199001010011",
  "phone": "13900010001",
  "company": "重庆数智科技有限公司",
  "gender": "男",
  "visitorLevel": "NORMAL",
  "status": "NORMAL"
}
```

### 6.3 新增预约申请

- 方法：`POST`
- 路径：`/api/visit-applies`
- 请求体示例：

```json
{
  "applyNo": "VA202606220001",
  "visitorId": 1,
  "hostUserId": 2,
  "departmentId": 2,
  "vehicleId": 1,
  "visitReason": "参加计算机学院校企合作交流",
  "planStartTime": "2026-06-22T09:00:00",
  "planEndTime": "2026-06-22T11:00:00",
  "applyStatus": "PENDING_HOST",
  "accessStatus": "NOT_ENTERED",
  "companionCount": 1
}
```

### 6.4 新增审批记录

- 方法：`POST`
- 路径：`/api/approval-records`
- 业务说明：用于保存被访人确认或部门审批结果。当前阶段为基础 CRUD，后续 `visitor-workflow-builder` 阶段将补充状态流转校验。

### 6.5 新增出入校记录

- 方法：`POST`
- 路径：`/api/access-records`
- 业务说明：用于门岗登记入校、离校记录。当前阶段为基础 CRUD，后续将拆分为核验、入校登记、离校登记等业务接口。

### 6.6 查询系统日志

- 方法：`GET`
- 路径：`/api/operation-logs?current=1&size=20`
- 业务说明：用于系统管理员或校级管理人员查看操作审计记录。

## 7. 后续扩展说明

当前阶段完成基础 CRUD 和接口文档，能够与数据库表一一对应。后续阶段需要在此基础上继续实现：

- JWT 登录认证与角色权限控制。
- 访客预约流程状态流转。
- 黑名单自动检查。
- 通行码生成与门岗核验。
- 入校、离校、当前在校、超时未离校专用接口。
- 统计报表聚合接口。

## 8. 登录认证与权限接口

| 模块 | 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|---|
| 登录认证 | POST | `/api/auth/login` | 用户名密码登录，返回 JWT Token | 匿名 |
| 登录认证 | POST | `/api/auth/logout` | 退出登录，撤销当前 Token | 已登录 |
| 当前用户 | GET | `/api/auth/me` | 获取当前用户、角色和权限编码 | 已登录 |
| 菜单权限 | GET | `/api/auth/menus` | 获取当前用户可访问菜单树 | 已登录 |

登录请求示例：

```json
{
  "username": "admin",
  "password": "123456"
}
```

登录成功响应中 `data.token` 即 JWT Token，前端后续请求应加入请求头：

```http
Authorization: Bearer <token>
```

默认测试账号：`admin / 123456`、`teacher01 / 123456`、`approver01 / 123456`、`guard01 / 123456`、`manager01 / 123456`。

无 Token 或 Token 无效返回 `401`，角色无权限返回 `403`。
## 9. 访客核心流程接口

本节接口均使用统一前缀 `/api/workflow`，返回格式仍为 `ApiResponse<T>`。除登录接口外，请求头均需要携带：

```http
Authorization: Bearer <token>
```

| 模块 | 方法 | 路径 | 说明 | 允许角色 |
|---|---|---|---|---|
| 预约申请 | POST | `/api/workflow/visit-applies` | 提交预约，自动检查黑名单，创建访客、车辆、随行人员和预约申请 | 系统管理员、访客 |
| 预约申请 | GET | `/api/workflow/visit-applies/my` | 查询当前角色可见的预约；访客可按手机号或证件号查询本人预约 | 系统管理员、校级管理人员、被访人、部门审批人员、访客 |
| 预约申请 | GET | `/api/workflow/visit-applies/{id}` | 查询预约详情 | 系统管理员、校级管理人员、被访人、部门审批人员、访客 |
| 预约申请 | PUT | `/api/workflow/visit-applies/{id}` | 修改待确认、被访人已确认或待部门审批的预约 | 系统管理员、访客 |
| 预约申请 | POST | `/api/workflow/visit-applies/{id}/cancel` | 取消未审批结束的预约 | 系统管理员、访客 |
| 被访人确认 | GET | `/api/workflow/host/pending` | 查询待被访人确认的预约 | 系统管理员、被访人 |
| 被访人确认 | POST | `/api/workflow/host/{id}/confirm` | 被访人确认预约，写入审批记录 | 系统管理员、被访人 |
| 被访人确认 | POST | `/api/workflow/host/{id}/reject` | 被访人拒绝预约，写入审批记录 | 系统管理员、被访人 |
| 部门审批 | GET | `/api/workflow/department/pending` | 查询本部门待审批预约 | 系统管理员、部门审批人员 |
| 部门审批 | POST | `/api/workflow/department/{id}/approve` | 部门审批通过，写入审批记录并生成通行凭证 | 系统管理员、部门审批人员 |
| 部门审批 | POST | `/api/workflow/department/{id}/reject` | 部门审批拒绝，写入审批记录 | 系统管理员、部门审批人员 |
| 通行凭证 | GET | `/api/workflow/pass-codes?applyId=1` | 查询审批通过预约的通行凭证 | 系统管理员、访客、门岗安保、校级管理人员 |
| 门岗核验 | POST | `/api/workflow/gate/verify` | 按预约编号、手机号、证件号或通行码核验是否允许入校 | 系统管理员、门岗安保 |
| 出入校登记 | POST | `/api/workflow/access/entry` | 核验通过后登记入校 | 系统管理员、门岗安保 |
| 出入校登记 | POST | `/api/workflow/access/exit` | 已入校访客登记离校，禁止重复离校 | 系统管理员、门岗安保 |
| 超时未离校 | GET | `/api/workflow/access/overtime?mark=true` | 查询并可标记超时未离校访客 | 系统管理员、门岗安保、校级管理人员 |

### 9.1 提交预约示例

```json
{
  "visitorName": "赵明",
  "idType": "ID_CARD",
  "idNumber": "500101199912120088",
  "phone": "13900018888",
  "company": "重庆数智科技有限公司",
  "gender": "男",
  "hostUserId": 2,
  "departmentId": 2,
  "visitReason": "参加智慧校园项目沟通会",
  "planStartTime": "2026-06-22T09:00:00",
  "planEndTime": "2026-06-22T11:00:00",
  "vehiclePlateNo": "渝A88K88",
  "vehicleType": "小型汽车",
  "companions": [
    {
      "companionName": "李娜",
      "idType": "ID_CARD",
      "idNumber": "500101199812120066",
      "phone": "13900016666",
      "relationRemark": "同事"
    }
  ]
}
```

成功后 `applyStatus = PENDING_HOST`，`accessStatus = NOT_ENTERED`。

### 9.2 审批请求示例

```json
{
  "comment": "来访事由属实，同意进入下一环节"
}
```

被访人确认后状态变为 `HOST_CONFIRMED`；部门审批通过后状态变为 `APPROVED`，系统自动生成 `pass_code`。

### 9.3 门岗核验示例

```json
{
  "passCode": "PC202606220001",
  "gateId": 1
}
```

也可以使用以下任一条件核验：`applyId`、`applyNo`、`phone`、`idNumber`、`passCode`。核验返回字段包括 `allowed`、`message`、`applyStatus`、`accessStatus`、`validFrom`、`validTo`。未审批、已拒绝、已取消、黑名单、过期凭证、重复入校都会返回 `allowed = false` 和明确原因。

### 9.4 入校和离校示例

入校登记：

```json
{
  "applyId": 8,
  "gateId": 1
}
```

离校登记：

```json
{
  "applyId": 8,
  "gateId": 2
}
```

入校成功后写入 `access_record.entry_time`，预约访问状态更新为 `ENTERED`。离校成功后写入 `access_record.exit_time`，预约访问状态更新为 `EXITED`。若重复离校，接口返回错误：`该访客已登记离校，不能重复离校`。
## 阶段 14 联调补充接口

### 统计报表接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/statistics/dashboard` | 返回驾驶舱、趋势、部门排行、校门统计和审批通过率综合数据。 |
| GET | `/api/statistics/overview` | 返回今日访客、当前在校、超时未离校、待处理审批等概览指标。 |
| GET | `/api/statistics/trend?days=7` | 返回近 N 天访客趋势。 |
| GET | `/api/statistics/department-rank` | 返回本月部门访客排行。 |
| GET | `/api/statistics/gate-summary` | 返回本月各校门入校和离校统计。 |
| GET | `/api/statistics/approval-rate` | 返回部门审批通过、拒绝和待处理数据。 |

### 列表展示字段补充

预约、出入校记录、黑名单和用户列表接口在保持原实体字段的基础上，补充返回前端展示字段，例如 `visitorName`、`phone`、`departmentName`、`entryGateName`、`exitGateName`、`operatorName`、`departmentName` 等。这些字段由后端根据外键查询补全，不在数据库业务表中冗余存储。