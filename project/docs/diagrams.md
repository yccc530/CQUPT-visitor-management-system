# 系统设计图汇总

本文档汇总“重庆邮电大学智慧访客预约与出入校管理系统”课程设计报告所需的主要 Mermaid 图。图中的模块、实体、表名、角色和状态与当前项目代码、数据库脚本及需求文档保持一致。

## 1. 系统功能模块图

对应文件：`diagrams/system_module.mmd`

```mermaid
flowchart TB
  SYS[重庆邮电大学智慧访客预约与出入校管理系统]

  SYS --> M1[首页统计驾驶舱]
  SYS --> M2[访客预约管理]
  SYS --> M3[确认与审批管理]
  SYS --> M4[通行凭证与门岗核验]
  SYS --> M5[出入校登记管理]
  SYS --> M6[安全风控管理]
  SYS --> M7[查询统计报表]
  SYS --> M8[系统基础管理]
  SYS --> M9[自动截图与报告管理]

  M1 --> M101[今日访客概览]
  M1 --> M102[当前在校与超时预警]
  M1 --> M103[趋势图与排行图]
  M1 --> M104[快捷入口]

  M2 --> M201[访客预约申请]
  M2 --> M202[我的预约]
  M2 --> M203[预约详情]
  M2 --> M204[修改未审批预约]
  M2 --> M205[取消未审批预约]

  M3 --> M301[被访人待确认]
  M3 --> M302[被访人确认或拒绝]
  M3 --> M303[部门待审批]
  M3 --> M304[部门审批通过或拒绝]
  M3 --> M305[审批记录留痕]

  M4 --> M401[通行码生成]
  M4 --> M402[通行码查询]
  M4 --> M403[预约编号核验]
  M4 --> M404[手机号或证件号核验]
  M4 --> M405[核验结果提示]

  M5 --> M501[入校登记]
  M5 --> M502[离校登记]
  M5 --> M503[当前在校访客]
  M5 --> M504[超时未离校访客]
  M5 --> M505[访问记录归档]

  M6 --> M601[黑名单管理]
  M6 --> M602[黑名单自动检查]
  M6 --> M603[异常通行处理]
  M6 --> M604[系统操作日志]

  M7 --> M701[访客记录查询]
  M7 --> M702[部门访客排行]
  M7 --> M703[校门通行统计]
  M7 --> M704[近七天访问趋势]
  M7 --> M705[审批通过率]

  M8 --> M801[用户管理]
  M8 --> M802[角色权限管理]
  M8 --> M803[部门管理]
  M8 --> M804[校门管理]
  M8 --> M805[字典管理]

  M9 --> M901[Playwright 自动截图]
  M9 --> M902[截图清单 manifest]
  M9 --> M903[Markdown 报告生成]
  M9 --> M904[Word 报告导出]
```

该图从课程设计报告的系统功能角度描述重庆邮电大学智慧访客预约与出入校管理系统的功能边界。系统围绕访客预约、确认审批、通行核验、出入校登记、安全风控、统计报表和系统基础管理展开，并补充自动截图与报告管理模块，能够覆盖数据库课程设计要求中的主要业务模块。

## 2. 用户用例图

对应文件：`diagrams/use_case.mmd`

```mermaid
flowchart LR
  Visitor[访客]
  Host[被访人]
  Approver[部门审批人员]
  Guard[门岗安保人员]
  Admin[系统管理员]
  Manager[校级管理人员]

  subgraph System[重庆邮电大学智慧访客预约与出入校管理系统]
    UC01([登录系统])
    UC02([提交预约申请])
    UC03([查看我的预约])
    UC04([修改或取消未审批预约])
    UC05([查看通行凭证])
    UC06([确认预约])
    UC07([拒绝预约])
    UC08([部门审批通过])
    UC09([部门审批拒绝])
    UC10([门岗核验])
    UC11([入校登记])
    UC12([离校登记])
    UC13([查看当前在校访客])
    UC14([处理超时未离校])
    UC15([黑名单管理])
    UC16([访客记录查询])
    UC17([统计报表查看])
    UC18([用户角色权限管理])
    UC19([部门与校门管理])
    UC20([系统日志查看])
    UC21([截图与报告记录维护])
  end

  Visitor --> UC01
  Visitor --> UC02
  Visitor --> UC03
  Visitor --> UC04
  Visitor --> UC05

  Host --> UC01
  Host --> UC06
  Host --> UC07
  Host --> UC16

  Approver --> UC01
  Approver --> UC08
  Approver --> UC09
  Approver --> UC16
  Approver --> UC17

  Guard --> UC01
  Guard --> UC10
  Guard --> UC11
  Guard --> UC12
  Guard --> UC13
  Guard --> UC14

  Admin --> UC01
  Admin --> UC15
  Admin --> UC18
  Admin --> UC19
  Admin --> UC20
  Admin --> UC21
  Admin --> UC17

  Manager --> UC01
  Manager --> UC13
  Manager --> UC14
  Manager --> UC16
  Manager --> UC17
```

该图描述访客、被访人、部门审批人员、门岗安保人员、系统管理员和校级管理人员六类角色与系统功能之间的使用关系。用例覆盖预约提交、审批处理、门岗核验、出入校登记、统计查询和系统维护，体现了系统的角色权限边界。

## 3. 数据流程图

对应文件：`diagrams/data_flow.mmd`

```mermaid
flowchart LR
  Visitor[外部实体：访客]
  Host[外部实体：被访人]
  Approver[外部实体：部门审批人员]
  Guard[外部实体：门岗安保人员]
  Manager[外部实体：校级管理人员]
  Admin[外部实体：系统管理员]

  subgraph P[处理过程]
    P1[1. 预约申请受理]
    P2[2. 黑名单检查]
    P3[3. 被访人确认]
    P4[4. 部门审批]
    P5[5. 通行凭证生成]
    P6[6. 门岗核验]
    P7[7. 入校登记]
    P8[8. 离校登记]
    P9[9. 查询统计与报表]
    P10[10. 系统基础维护]
  end

  subgraph D[数据存储]
    D1[(visitor 访客信息)]
    D2[(visit_apply 预约申请)]
    D3[(blacklist 黑名单)]
    D4[(approval_record 审批记录)]
    D5[(pass_code 通行凭证)]
    D6[(access_record 出入校记录)]
    D7[(sys_user/sys_role/sys_permission 用户权限)]
    D8[(department/campus_gate 部门校门)]
    D9[(operation_log 操作日志)]
    D10[(screenshot_record/report_record 截图报告)]
  end

  Visitor -->|访客身份、来访事由、访问时间| P1
  P1 -->|写入或更新访客资料| D1
  P1 -->|生成预约申请| D2
  P1 --> P2
  P2 -->|读取黑名单规则| D3
  P2 -->|风险状态或拦截结果| D2
  P2 -->|待确认预约| P3
  Host -->|确认或拒绝意见| P3
  P3 -->|确认记录| D4
  P3 -->|更新预约状态| D2
  P3 -->|待部门审批申请| P4
  Approver -->|审批意见| P4
  P4 -->|审批记录| D4
  P4 -->|通过或拒绝状态| D2
  P4 -->|审批通过申请| P5
  P5 -->|生成二维码和有效期| D5
  Guard -->|预约编号、手机号、证件号或通行码| P6
  P6 -->|读取预约状态| D2
  P6 -->|读取通行凭证| D5
  P6 -->|读取黑名单| D3
  P6 -->|核验通过| P7
  P7 -->|入校时间、校门、安保人员| D6
  P7 -->|访问状态已入校| D2
  Guard -->|离校确认| P8
  P8 -->|离校时间、校门、安保人员| D6
  P8 -->|访问状态已离校或超时| D2
  Manager -->|统计条件| P9
  Admin -->|统计条件| P9
  P9 -->|读取业务数据| D1
  P9 -->|读取预约数据| D2
  P9 -->|读取通行数据| D6
  P9 -->|输出报表、趋势、排行| Manager
  Admin -->|用户、角色、部门、校门、字典维护| P10
  P10 --> D7
  P10 --> D8
  P10 --> D9
  P9 --> D10
```

该图综合表示顶层数据流程和二层数据处理过程。访客预约数据先进入预约申请受理和黑名单检查，再经过被访人确认、部门审批、通行凭证生成、门岗核验、入校登记、离校登记和查询统计等处理过程，所有关键数据均落入对应数据库表。

## 4. E-R 图

对应文件：`diagrams/er_diagram.mmd`

```mermaid
erDiagram
  DEPARTMENT ||--o{ SYS_USER : contains
  DEPARTMENT ||--o{ VISIT_APPLY : receives
  DEPARTMENT ||--o{ DEPARTMENT : parent_of

  SYS_USER ||--o{ SYS_USER_ROLE : owns
  SYS_ROLE ||--o{ SYS_USER_ROLE : assigned
  SYS_ROLE ||--o{ SYS_ROLE_PERMISSION : owns
  SYS_PERMISSION ||--o{ SYS_ROLE_PERMISSION : granted
  SYS_PERMISSION ||--o{ SYS_PERMISSION : parent_of

  VISITOR ||--o{ VISITOR_VEHICLE : owns
  VISITOR ||--o{ VISIT_APPLY : submits
  VISITOR ||--o{ BLACKLIST : may_be_listed
  VISITOR ||--o{ ACCESS_RECORD : enters_or_exits

  SYS_USER ||--o{ VISIT_APPLY : hosts
  VISIT_APPLY ||--o{ VISITOR_COMPANION : includes
  VISITOR_VEHICLE ||--o{ VISIT_APPLY : used_by
  VISIT_APPLY ||--o{ APPROVAL_RECORD : has
  SYS_USER ||--o{ APPROVAL_RECORD : approves
  VISIT_APPLY ||--|| PASS_CODE : generates
  PASS_CODE ||--o{ ACCESS_RECORD : verifies
  VISIT_APPLY ||--o{ ACCESS_RECORD : produces
  CAMPUS_GATE ||--o{ ACCESS_RECORD : records
  SYS_USER ||--o{ ACCESS_RECORD : handles

  SYS_USER ||--o{ NOTICE : receives
  SYS_USER ||--o{ OPERATION_LOG : creates
  DICT_TYPE ||--o{ DICT_ITEM : contains
  SYS_USER ||--o{ SCREENSHOT_RECORD : creates
  SYS_USER ||--o{ REPORT_RECORD : generates

  DEPARTMENT {
    bigint id PK
    bigint parent_id FK
    varchar dept_code UK
    varchar dept_name
    bigint leader_user_id FK
  }

  SYS_USER {
    bigint id PK
    varchar username UK
    varchar password_hash
    varchar real_name
    bigint department_id FK
    varchar user_type
  }

  SYS_ROLE {
    bigint id PK
    varchar role_code UK
    varchar role_name
  }

  SYS_PERMISSION {
    bigint id PK
    varchar permission_code UK
    varchar permission_name
    varchar route_path
    varchar api_path
  }

  VISITOR {
    bigint id PK
    varchar visitor_name
    varchar id_number UK
    varchar phone
    varchar visitor_level
  }

  VISIT_APPLY {
    bigint id PK
    varchar apply_no UK
    bigint visitor_id FK
    bigint host_user_id FK
    bigint department_id FK
    bigint vehicle_id FK
    varchar apply_status
    varchar access_status
  }

  APPROVAL_RECORD {
    bigint id PK
    bigint apply_id FK
    varchar approval_step
    bigint approver_user_id FK
    varchar approval_result
    datetime approval_time
  }

  PASS_CODE {
    bigint id PK
    bigint apply_id FK
    varchar pass_code UK
    datetime valid_from
    datetime valid_to
    varchar pass_status
  }

  ACCESS_RECORD {
    bigint id PK
    bigint apply_id FK
    bigint visitor_id FK
    bigint pass_code_id FK
    bigint entry_gate_id FK
    bigint exit_gate_id FK
    varchar access_status
  }
```

该图从概念结构角度描述系统核心实体及联系，包括部门、系统用户、角色权限、访客、预约申请、审批记录、通行凭证、出入校记录、黑名单、通知、日志、截图记录和报告记录。实体关系能够自然转换为后续 MySQL 关系模式。

## 5. 数据库表关系图

对应文件：`diagrams/table_relation.mmd`

```mermaid
erDiagram
  department ||--o{ sys_user : department_id
  department ||--o{ department : parent_id
  department ||--o{ visit_apply : department_id

  sys_user ||--o{ sys_user_role : user_id
  sys_role ||--o{ sys_user_role : role_id
  sys_role ||--o{ sys_role_permission : role_id
  sys_permission ||--o{ sys_role_permission : permission_id
  sys_permission ||--o{ sys_permission : parent_id

  visitor ||--o{ visitor_vehicle : visitor_id
  visitor ||--o{ visit_apply : visitor_id
  visitor_vehicle ||--o{ visit_apply : vehicle_id
  sys_user ||--o{ visit_apply : host_user_id
  visit_apply ||--o{ visitor_companion : apply_id
  visit_apply ||--o{ approval_record : apply_id
  sys_user ||--o{ approval_record : approver_user_id

  visit_apply ||--|| pass_code : apply_id
  pass_code ||--o{ access_record : pass_code_id
  visit_apply ||--o{ access_record : apply_id
  visitor ||--o{ access_record : visitor_id
  campus_gate ||--o{ access_record : entry_gate_id
  campus_gate ||--o{ access_record : exit_gate_id
  sys_user ||--o{ access_record : entry_guard_id
  sys_user ||--o{ access_record : exit_guard_id

  visitor ||--o{ blacklist : visitor_id
  sys_user ||--o{ blacklist : operator_user_id
  sys_user ||--o{ notice : receiver_user_id
  sys_user ||--o{ operation_log : operator_user_id
  dict_type ||--o{ dict_item : type_id
  sys_user ||--o{ screenshot_record : created_by
  sys_user ||--o{ report_record : generated_by
```

该图从逻辑结构角度展示 MySQL 表之间的主外键依赖关系。图中表名与 schema.sql 保持一致，说明 visit_apply、approval_record、pass_code、access_record 等核心业务表如何连接访客、用户、部门和校门基础数据。

## 6. 系统架构图

对应文件：`diagrams/system_architecture.mmd`

```mermaid
flowchart TB
  subgraph Client[前端访问层]
    Browser[浏览器]
    Vue[Vue 3 + Vite]
    Element[Element Plus + ECharts]
  end

  subgraph Frontend[前端工程层]
    Router[Vue Router 路由控制]
    Axios[Axios 请求拦截器]
    Token[Token 本地存储]
    Pages[业务页面组件]
  end

  subgraph Backend[后端服务层]
    Controller[Spring Boot Controller]
    Security[Spring Security + JWT]
    Service[业务 Service]
    Workflow[访客流程服务]
    Mapper[MyBatis Plus Mapper]
    Swagger[Swagger 接口文档]
  end

  subgraph Data[数据持久层]
    MySQL[(MySQL 8 数据库)]
    Tables[21 张业务数据表]
  end

  subgraph Automation[自动化支撑层]
    Playwright[Playwright 自动截图]
    Manifest[screenshots/manifest.json]
    ReportScript[generate_report.py]
    Markdown[Markdown 课程设计报告]
    Word[Word 文档导出]
  end

  Browser --> Vue
  Vue --> Element
  Vue --> Router
  Router --> Pages
  Pages --> Axios
  Axios --> Token
  Axios -->|/api| Controller
  Controller --> Security
  Security --> Service
  Service --> Workflow
  Service --> Mapper
  Mapper --> MySQL
  MySQL --> Tables
  Controller --> Swagger

  Playwright --> Browser
  Playwright --> Manifest
  Manifest --> ReportScript
  MySQL --> ReportScript
  ReportScript --> Markdown
  ReportScript --> Word
```

该图描述系统技术架构。前端采用 Vue 3、Vite、Element Plus、Axios 和 ECharts，后端采用 Spring Boot 3、Spring Security、JWT、MyBatis Plus，数据库采用 MySQL 8，同时由 Playwright 和报告生成脚本支撑自动截图与课程设计报告生成。

## 7. 访客预约审批流程图

对应文件：`diagrams/visitor_workflow.mmd`

```mermaid
flowchart TD
  Start([访客提交预约申请])
  SaveVisitor[保存或更新访客信息]
  CheckBlacklist{手机号或证件号是否命中黑名单}
  RejectBlacklist[标记 REJECTED_BLACKLIST 并写入操作日志]
  PendingHost[预约状态：PENDING_HOST 待被访人确认]
  HostDecision{被访人处理}
  HostReject[状态：HOST_REJECTED]
  HostConfirm[状态：HOST_CONFIRMED]
  DeptQueue[进入部门审批队列]
  DeptDecision{部门审批}
  DeptReject[状态：REJECTED]
  Approved[状态：APPROVED]
  GeneratePass[生成 pass_code 通行凭证]
  NotifyVisitor[通知访客查看通行码]
  Archive[审批记录和操作日志归档]
  End([进入门岗核验环节])

  Start --> SaveVisitor
  SaveVisitor --> CheckBlacklist
  CheckBlacklist -->|是| RejectBlacklist
  RejectBlacklist --> Archive
  CheckBlacklist -->|否| PendingHost
  PendingHost --> HostDecision
  HostDecision -->|拒绝| HostReject
  HostDecision -->|确认| HostConfirm
  HostConfirm --> DeptQueue
  DeptQueue --> DeptDecision
  DeptDecision -->|拒绝| DeptReject
  DeptDecision -->|通过| Approved
  Approved --> GeneratePass
  GeneratePass --> NotifyVisitor
  HostReject --> Archive
  DeptReject --> Archive
  NotifyVisitor --> Archive
  Archive --> End
```

该图描述从访客提交预约到审批通过生成通行凭证的完整流程。流程包含黑名单自动检查、被访人确认、部门审批、审批记录留痕和通行码生成，体现了预约状态从 PENDING_HOST 到 APPROVED、REJECTED 等状态的流转。

## 8. 门岗核验入校流程图

对应文件：`diagrams/gate_check_workflow.mmd`

```mermaid
flowchart TD
  Start([门岗安保发起核验])
  Input[输入通行码、预约编号、手机号或证件号]
  QueryApply[查询 visit_apply、visitor、pass_code]
  ApplyExists{是否存在预约}
  StatusCheck{预约是否审批通过}
  TimeCheck{当前时间是否在有效期内}
  BlacklistCheck{访客是否在有效黑名单中}
  AccessCheck{是否已离校或重复入校}
  Deny[返回不允许入校及原因]
  Allow[返回允许入校]
  Entry[登记入校：写 access_record]
  UpdateEntered[更新 access_status = ENTERED]
  ExitRequest[访客离校登记]
  ExitCheck{是否已入校且未离校}
  Exit[登记离校时间和离校校门]
  UpdateExited[更新 access_status = EXITED]
  Overtime[定时或人工标记超时未离校]
  End([访问记录归档])

  Start --> Input
  Input --> QueryApply
  QueryApply --> ApplyExists
  ApplyExists -->|否| Deny
  ApplyExists -->|是| StatusCheck
  StatusCheck -->|否| Deny
  StatusCheck -->|是| TimeCheck
  TimeCheck -->|否| Deny
  TimeCheck -->|是| BlacklistCheck
  BlacklistCheck -->|是| Deny
  BlacklistCheck -->|否| AccessCheck
  AccessCheck -->|异常| Deny
  AccessCheck -->|正常| Allow
  Allow --> Entry
  Entry --> UpdateEntered
  UpdateEntered --> ExitRequest
  ExitRequest --> ExitCheck
  ExitCheck -->|否| Deny
  ExitCheck -->|是| Exit
  Exit --> UpdateExited
  UpdateExited --> End
  UpdateEntered --> Overtime
  Overtime --> End
```

该图描述门岗安保人员使用预约编号、手机号、证件号或通行码核验访客的过程。系统会检查预约是否存在、审批是否通过、访问时间是否有效、是否命中黑名单以及是否重复入离校，并据此完成入校、离校或超时处理。

## 9. 自动截图和报告生成流程图

对应文件：`diagrams/automation_report_workflow.mmd`

```mermaid
flowchart TD
  Start([执行 scripts/run_all.sh])
  InitDB[初始化 MySQL 数据库]
  StartBackend[启动 Spring Boot 后端]
  StartFrontend[启动 Vue 前端]
  Login[Playwright 使用测试账号登录]
  VisitPages[访问登录页、驾驶舱、预约、审批、核验、报表等核心页面]
  Capture[保存截图到 screenshots/]
  Manifest[生成 screenshots/manifest.json]
  ReadDocs[读取 docs/report_parts 与 docs/diagrams.md]
  ReadSQL[读取 database/schema.sql 与 query_examples.sql]
  ReadScreenshots[读取截图清单和图片路径]
  GenerateMD[生成 Markdown 课程设计报告]
  ExportWord[使用 python-docx 或 Pandoc 导出 Word]
  Record[写入 screenshot_record 与 report_record]
  End([形成可提交材料])

  Start --> InitDB
  InitDB --> StartBackend
  StartBackend --> StartFrontend
  StartFrontend --> Login
  Login --> VisitPages
  VisitPages --> Capture
  Capture --> Manifest
  Manifest --> ReadScreenshots
  ReadDocs --> GenerateMD
  ReadSQL --> GenerateMD
  ReadScreenshots --> GenerateMD
  GenerateMD --> ExportWord
  ExportWord --> Record
  Record --> End
```

该图描述后续自动化交付流程。系统通过 run_all.sh 串联数据库初始化、后端启动、前端启动、Playwright 自动登录截图、manifest 生成、Markdown 报告生成和 Word 导出，为课程设计报告提供可重复生成的截图与文档材料。

