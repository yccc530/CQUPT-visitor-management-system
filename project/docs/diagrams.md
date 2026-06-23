# 系统设计图汇总

本文档汇总“重庆邮电大学智慧访客预约与出入校管理系统”课程设计报告所需图件。为避免图形过大和线条交叉，E-R 图按业务域拆分，流程图按功能层次拆分，完整主外键表关系图建议作为附录使用。

## 报告使用建议

| 推荐位置 | 图文件 | 用途 |
| --- | --- | --- |
| 正文 | `diagrams/er_overview.mmd` | 概念结构总体说明 |
| 正文 | `diagrams/er_core_business.mmd` | 说明访客预约、审批、通行、出入校核心流程 |
| 正文 | `diagrams/system_module.mmd` | 说明系统功能模块划分 |
| 正文 | `diagrams/visitor_workflow.mmd` | 说明预约审批业务流程 |
| 正文 | `diagrams/data_flow_level0.mmd`、`diagrams/data_flow_level1.mmd` | 说明数据流程图 |
| 附录 | `diagrams/er_user_permission.mmd`、`diagrams/er_system_support.mmd`、`diagrams/table_relation.mmd` | 展示权限、支撑实体和完整表关系 |

## 1. 总体简化 E-R 图

对应文件：`diagrams/er_overview.mmd`

总体简化 E-R 图只保留主要实体和主干关系，用于报告正文快速说明概念模型全貌。图中不展开所有弱关联和审计字段，重点突出访客、预约、审批、通行、权限和系统支撑之间的主联系。

```mermaid
erDiagram
  department ||--o{ sys_user : contains
  sys_user ||--o{ visit_apply : hosts
  visitor ||--o{ visit_apply : submits
  department ||--o{ visit_apply : approves_scope
  visit_apply ||--o{ approval_record : approvals
  visit_apply ||--o| pass_code : pass
  pass_code ||--o{ access_record : gate_verify
  campus_gate ||--o{ access_record : gate
  visitor ||--o{ blacklist : risk
  sys_user ||--o{ operation_log : audit
  sys_user ||--o{ notice : message
  sys_user ||--o{ sys_user_role : has
  sys_role ||--o{ sys_user_role : assigned
  sys_role ||--o{ sys_role_permission : owns
  sys_permission ||--o{ sys_role_permission : grants

  visitor {
    bigint id PK
    string visitor_name
    string phone
    string id_number
  }

  visit_apply {
    bigint id PK
    string apply_no
    bigint visitor_id FK
    bigint host_user_id FK
    string apply_status
    string access_status
  }

  approval_record {
    bigint id PK
    bigint apply_id FK
    string approval_step
    string approval_result
  }

  pass_code {
    bigint id PK
    bigint apply_id FK
    string pass_code
    string pass_status
  }

  access_record {
    bigint id PK
    bigint apply_id FK
    bigint entry_gate_id FK
    string access_status
  }

  campus_gate {
    bigint id PK
    string gate_name
    string gate_type
  }

  blacklist {
    bigint id PK
    bigint visitor_id FK
    string level
    string status
  }

  department {
    bigint id PK
    string dept_name
  }

  sys_user {
    bigint id PK
    string username
    string real_name
    string user_type
  }

  sys_role {
    bigint id PK
    string role_code
  }

  sys_permission {
    bigint id PK
    string permission_code
  }

  sys_user_role {
    bigint id PK
    bigint user_id FK
    bigint role_id FK
  }

  sys_role_permission {
    bigint id PK
    bigint role_id FK
    bigint permission_id FK
  }

  notice {
    bigint id PK
    bigint receiver_user_id FK
    string title
  }

  operation_log {
    bigint id PK
    bigint operator_user_id FK
    string module_name
  }
```

## 2. 核心业务 E-R 图

对应文件：`diagrams/er_core_business.mmd`

核心业务 E-R 图围绕访客预约主流程设计，展示访客、车辆、随行人员、预约申请、审批记录、通行凭证、出入校记录、校门和黑名单之间的关系。该图建议放在概念结构设计正文。

```mermaid
erDiagram
  visitor ||--o{ visit_apply : submits
  visitor ||--o{ visitor_vehicle : owns
  visitor_vehicle ||--o{ visit_apply : selected_by
  visit_apply ||--o{ visitor_companion : includes
  visit_apply ||--o{ approval_record : records
  visit_apply ||--o| pass_code : issues
  pass_code ||--o{ access_record : verifies
  visit_apply ||--o{ access_record : archives
  campus_gate ||--o{ access_record : handles
  visitor ||--o{ blacklist : risks

  visitor {
    bigint id PK
    string visitor_name
    string id_number UK
    string phone UK
    string company
    string visitor_level
    string status
  }

  visitor_vehicle {
    bigint id PK
    bigint visitor_id FK
    string plate_no UK
    string vehicle_type
    string color
    string brand
    string status
  }

  visitor_companion {
    bigint id PK
    bigint apply_id FK
    string companion_name
    string id_number
    string phone
    string relation_remark
  }

  visit_apply {
    bigint id PK
    string apply_no UK
    bigint visitor_id FK
    bigint host_user_id FK
    bigint department_id FK
    string visit_reason
    string apply_status
    string access_status
  }

  approval_record {
    bigint id PK
    bigint apply_id FK
    string approval_step
    bigint approver_user_id FK
    string approval_result
    datetime approval_time
  }

  pass_code {
    bigint id PK
    bigint apply_id FK
    string pass_code UK
    datetime valid_from
    datetime valid_to
    string pass_status
    int verify_count
  }

  access_record {
    bigint id PK
    bigint apply_id FK
    bigint visitor_id FK
    bigint pass_code_id FK
    bigint entry_gate_id FK
    bigint exit_gate_id FK
    string access_status
    int overtime_flag
  }

  campus_gate {
    bigint id PK
    string gate_code UK
    string gate_name
    string gate_location
    string gate_type
    string status
  }

  blacklist {
    bigint id PK
    bigint visitor_id FK
    string id_number
    string phone
    string reason
    string level
    string status
  }
```

## 3. 用户权限 E-R 图

对应文件：`diagrams/er_user_permission.mmd`

用户权限 E-R 图单独展示组织部门、系统用户、角色、权限以及两张关联表，避免 RBAC 多对多关系干扰核心访客业务图。

```mermaid
erDiagram
  department ||--o{ sys_user : contains
  sys_user ||--o{ sys_user_role : assigned
  sys_role ||--o{ sys_user_role : maps
  sys_role ||--o{ sys_role_permission : grants
  sys_permission ||--o{ sys_role_permission : maps

  department {
    bigint id PK
    bigint parent_id FK
    string dept_code UK
    string dept_name
    bigint leader_user_id FK
    string phone
    string status
  }

  sys_user {
    bigint id PK
    string username UK
    string real_name
    string phone UK
    bigint department_id FK
    string user_type
    string status
  }

  sys_role {
    bigint id PK
    string role_code UK
    string role_name
    string role_desc
    string status
  }

  sys_permission {
    bigint id PK
    string permission_code UK
    string permission_name
    string permission_type
    string route_path
    string api_path
    string status
  }

  sys_user_role {
    bigint id PK
    bigint user_id FK
    bigint role_id FK
    datetime create_time
  }

  sys_role_permission {
    bigint id PK
    bigint role_id FK
    bigint permission_id FK
    datetime create_time
  }
```

## 4. 系统支撑 E-R 图

对应文件：`diagrams/er_system_support.mmd`

系统支撑 E-R 图展示通知、日志、字典、截图记录和报告记录。由于这些实体主要服务于消息提醒、审计、自动截图和报告生成，未放入核心业务 E-R 图。

```mermaid
erDiagram
  dict_type ||--o{ dict_item : contains

  notice {
    bigint id PK
    bigint receiver_user_id FK
    string receiver_type
    string title
    string business_type
    bigint business_id
    string read_status
  }

  operation_log {
    bigint id PK
    bigint operator_user_id FK
    string operator_name
    string module_name
    string operation_type
    string operation_result
    datetime operation_time
  }

  dict_type {
    bigint id PK
    string type_code UK
    string type_name
    string status
    string remark
  }

  dict_item {
    bigint id PK
    bigint type_id FK
    string item_code
    string item_name
    string item_value
    string status
  }

  screenshot_record {
    bigint id PK
    string screenshot_code UK
    string page_name
    string route_path
    string role_code
    string file_path
    string status
  }

  report_record {
    bigint id PK
    string report_code UK
    string report_name
    string markdown_path
    string word_path
    string generate_status
  }
```

## 5. 数据库表关系图

对应文件：`diagrams/table_relation.mmd`

数据库表关系图面向逻辑结构与主外键说明，覆盖主要外键关系。该图比概念 E-R 图更接近 MySQL 表结构，建议作为报告附录或“其它设计图”。

```mermaid
erDiagram
  department ||--o{ sys_user : department_id
  department ||--o{ visit_apply : department_id
  sys_user ||--o{ sys_user_role : user_id
  sys_role ||--o{ sys_user_role : role_id
  sys_role ||--o{ sys_role_permission : role_id
  sys_permission ||--o{ sys_role_permission : permission_id
  visitor ||--o{ visitor_vehicle : visitor_id
  visitor ||--o{ visit_apply : visitor_id
  visitor_vehicle ||--o{ visit_apply : vehicle_id
  visit_apply ||--o{ visitor_companion : apply_id
  sys_user ||--o{ visit_apply : host_user_id
  visit_apply ||--o{ approval_record : apply_id
  sys_user ||--o{ approval_record : approver_user_id
  visit_apply ||--o| pass_code : apply_id
  visit_apply ||--o{ access_record : apply_id
  visitor ||--o{ access_record : visitor_id
  pass_code ||--o{ access_record : pass_code_id
  campus_gate ||--o{ access_record : entry_exit_gate_id
  sys_user ||--o{ access_record : guard_id
  visitor ||--o{ blacklist : visitor_id
  sys_user ||--o{ blacklist : operator_user_id
  sys_user ||--o{ notice : receiver_user_id
  sys_user ||--o{ operation_log : operator_user_id
  dict_type ||--o{ dict_item : type_id
  sys_user ||--o{ screenshot_record : created_by
  sys_user ||--o{ report_record : generated_by

  department {
    bigint id PK
    bigint parent_id FK
    bigint leader_user_id FK
  }
  sys_user {
    bigint id PK
    bigint department_id FK
  }
  sys_role {
    bigint id PK
  }
  sys_permission {
    bigint id PK
    bigint parent_id FK
  }
  sys_user_role {
    bigint id PK
    bigint user_id FK
    bigint role_id FK
  }
  sys_role_permission {
    bigint id PK
    bigint role_id FK
    bigint permission_id FK
  }
  visitor {
    bigint id PK
  }
  visitor_vehicle {
    bigint id PK
    bigint visitor_id FK
  }
  visitor_companion {
    bigint id PK
    bigint apply_id FK
  }
  visit_apply {
    bigint id PK
    bigint visitor_id FK
    bigint host_user_id FK
    bigint department_id FK
    bigint vehicle_id FK
  }
  approval_record {
    bigint id PK
    bigint apply_id FK
    bigint approver_user_id FK
  }
  pass_code {
    bigint id PK
    bigint apply_id FK
  }
  access_record {
    bigint id PK
    bigint apply_id FK
    bigint visitor_id FK
    bigint pass_code_id FK
    bigint entry_gate_id FK
    bigint exit_gate_id FK
  }
  campus_gate {
    bigint id PK
  }
  blacklist {
    bigint id PK
    bigint visitor_id FK
    bigint operator_user_id FK
  }
  notice {
    bigint id PK
    bigint receiver_user_id FK
    bigint business_id
  }
  operation_log {
    bigint id PK
    bigint operator_user_id FK
  }
  dict_type {
    bigint id PK
  }
  dict_item {
    bigint id PK
    bigint type_id FK
  }
  screenshot_record {
    bigint id PK
    bigint created_by FK
  }
  report_record {
    bigint id PK
    bigint generated_by FK
  }
```

## 6. 系统功能模块图

对应文件：`diagrams/system_module.mmd`

系统功能模块图采用层次结构，一级模块包括访客端、被访人端、审批管理、门岗管理、访客记录、安全管理、统计报表、系统管理和自动化支撑。

```mermaid
flowchart TB
  SYS[重庆邮电大学智慧访客预约与出入校管理系统]

  SYS --> V[访客端]
  SYS --> H[被访人端]
  SYS --> A[审批管理]
  SYS --> G[门岗管理]
  SYS --> R[访客记录]
  SYS --> S[安全管理]
  SYS --> T[统计报表]
  SYS --> M[系统管理]
  SYS --> X[自动化支撑]

  subgraph VisitorSide[访客端]
    V1[访客预约]
    V2[状态查询]
    V3[通行凭证]
    V4[历史记录]
  end

  subgraph HostSide[被访人端]
    H1[待确认预约]
    H2[确认预约]
    H3[拒绝预约]
    H4[接待记录]
  end

  subgraph ApprovalSide[审批管理]
    A1[部门待审批]
    A2[审批通过]
    A3[审批拒绝]
    A4[审批记录留痕]
  end

  subgraph GateSide[门岗管理]
    G1[通行码核验]
    G2[入校登记]
    G3[离校登记]
    G4[当前在校访客]
    G5[超时未离校]
  end

  subgraph RecordSide[访客记录]
    R1[预约记录查询]
    R2[出入校记录]
    R3[审批轨迹]
    R4[车辆与随行人员]
  end

  subgraph SecuritySide[安全管理]
    S1[黑名单管理]
    S2[黑名单自动检查]
    S3[异常通行处理]
    S4[操作日志审计]
  end

  subgraph ReportSide[统计报表]
    T1[今日/本周/本月访客]
    T2[近七天趋势]
    T3[部门访客排行]
    T4[校门通行统计]
    T5[审批与风险分布]
  end

  subgraph ManageSide[系统管理]
    M1[用户管理]
    M2[角色权限管理]
    M3[部门管理]
    M4[校门管理]
    M5[字典管理]
  end

  subgraph AutomationSide[自动化支撑]
    X1[自动截图]
    X2[截图清单]
    X3[自动报告生成]
    X4[报告生成记录]
  end

  V --> V1
  V --> V2
  V --> V3
  V --> V4
  H --> H1
  H --> H2
  H --> H3
  H --> H4
  A --> A1
  A --> A2
  A --> A3
  A --> A4
  G --> G1
  G --> G2
  G --> G3
  G --> G4
  G --> G5
  R --> R1
  R --> R2
  R --> R3
  R --> R4
  S --> S1
  S --> S2
  S --> S3
  S --> S4
  T --> T1
  T --> T2
  T --> T3
  T --> T4
  T --> T5
  M --> M1
  M --> M2
  M --> M3
  M --> M4
  M --> M5
  X --> X1
  X --> X2
  X --> X3
  X --> X4
```

## 7. 访客预约审批流程图

对应文件：`diagrams/visitor_workflow.mmd`

访客预约审批流程图展示从访客申请到黑名单检查、被访人确认、部门审批、通行码生成、门岗核验、入校、离校和归档的完整流程，并标出拒绝、过期、超时等异常分支。

```mermaid
flowchart LR
  Start([访客提交预约]) --> Blacklist{黑名单检查}
  Blacklist -->|命中风险| Block[黑名单拦截或风险标记]
  Block --> EndReject([流程终止])

  Blacklist -->|未命中| Host{被访人确认}
  Host -->|拒绝| HostReject[被访人拒绝]
  HostReject --> EndReject
  Host -->|确认| Dept{部门审批}
  Dept -->|拒绝或退回| DeptReject[审批拒绝/退回修改]
  DeptReject --> EndReject
  Dept -->|通过| Pass[生成通行码]

  Pass --> Verify{门岗核验}
  Verify -->|通行码过期| Expired[提示通行码过期]
  Expired --> EndReject
  Verify -->|状态异常| VerifyFail[禁止入校并记录日志]
  VerifyFail --> EndReject
  Verify -->|核验通过| Entry[入校登记]

  Entry --> InCampus[当前在校]
  InCampus --> Timeout{是否超时未离校}
  Timeout -->|是| Overtime[超时未离校预警]
  Overtime --> Leave[离校登记]
  Timeout -->|否| Leave
  Leave --> Archive[访问记录归档]
  Archive --> Report[查询统计与报表]
  Report --> End([流程结束])
```

## 8. 门岗核验入校流程图

对应文件：`diagrams/gate_check_workflow.mmd`

门岗核验流程图以安保人员操作为主线，展示通行码、预约状态、访问时间、黑名单和通行码有效性的逐项校验逻辑。

```mermaid
flowchart LR
  Guard([门岗安保人员]) --> Input[输入通行码/预约号/手机号/证件号]
  Input --> Query[查询预约与通行凭证]
  Query --> Exists{是否存在预约}
  Exists -->|否| NotFound[提示未查询到预约]
  NotFound --> Deny([禁止入校])

  Exists -->|是| Status{预约是否审批通过}
  Status -->|否| NotApproved[提示未审批通过或已拒绝]
  NotApproved --> Deny

  Status -->|是| Time{是否在有效访问时间}
  Time -->|否| TimeFail[提示未到访问时间或已过期]
  TimeFail --> Deny

  Time -->|是| Risk{黑名单检查}
  Risk -->|命中| RiskWarn[风险提示并拦截/人工复核]
  RiskWarn --> Deny

  Risk -->|未命中| Pass{通行码是否有效}
  Pass -->|无效/作废/已过期| PassFail[通行码不可用]
  PassFail --> Deny
  Pass -->|有效| Allow[允许入校]

  Allow --> Entry[记录入校时间、校门、安保人员]
  Entry --> Update[更新访问状态为已入校]
  Update --> Log[写入操作日志]
  Log --> Done([核验完成])
```

## 9. 顶层数据流程图

对应文件：`diagrams/data_flow_level0.mmd`

顶层数据流程图按照数据库课程设计要求展示外部实体、系统处理过程、数据存储和主要数据流。

```mermaid
flowchart LR
  Visitor[外部实体：访客]
  Host[外部实体：被访人]
  Approver[外部实体：部门审批人员]
  Guard[外部实体：门岗安保人员]
  Manager[外部实体：校级管理人员]
  Admin[外部实体：系统管理员]

  P0((智慧访客预约与出入校管理系统))

  D1[(访客与预约数据)]
  D2[(审批与通行数据)]
  D3[(出入校记录数据)]
  D4[(用户权限与基础数据)]
  D5[(日志、截图与报告数据)]

  Visitor -->|预约申请、身份信息| P0
  P0 -->|预约状态、通行凭证| Visitor
  Host -->|确认意见| P0
  P0 -->|待确认预约| Host
  Approver -->|审批意见| P0
  P0 -->|待审批预约| Approver
  Guard -->|核验与登记信息| P0
  P0 -->|核验结果、在校名单| Guard
  Manager -->|统计查询条件| P0
  P0 -->|统计报表| Manager
  Admin -->|基础数据维护| P0
  P0 -->|日志、截图、报告记录| Admin

  P0 <--> D1
  P0 <--> D2
  P0 <--> D3
  P0 <--> D4
  P0 <--> D5
```

## 10. 二层数据流程图

对应文件：`diagrams/data_flow_level1.mmd`

二层数据流程图将预约审批和门岗核验两个核心数据流程展开，明确各处理过程读写的数据存储。

```mermaid
flowchart LR
  Visitor[访客]
  Host[被访人]
  Approver[部门审批人员]
  Guard[门岗安保人员]

  subgraph ApplyApproval[预约审批数据流程]
    P1[1. 受理预约申请]
    P2[2. 黑名单检查]
    P3[3. 被访人确认]
    P4[4. 部门审批]
    P5[5. 生成通行凭证]
  end

  subgraph GateAccess[门岗核验数据流程]
    P6[6. 门岗核验]
    P7[7. 入校登记]
    P8[8. 离校登记]
    P9[9. 超时处理]
  end

  D1[(visitor)]
  D2[(visit_apply)]
  D3[(blacklist)]
  D4[(approval_record)]
  D5[(pass_code)]
  D6[(access_record)]
  D7[(campus_gate)]
  D8[(operation_log)]

  Visitor -->|身份、车辆、随行人员、访问事由| P1
  P1 -->|访客资料| D1
  P1 -->|预约申请| D2
  P1 --> P2
  P2 -->|读取手机号/证件号风险| D3
  P2 -->|风险状态| D2
  P2 -->|待确认申请| P3
  Host -->|确认/拒绝| P3
  P3 -->|确认记录| D4
  P3 -->|预约状态| D2
  P3 -->|待审批申请| P4
  Approver -->|审批通过/拒绝/退回| P4
  P4 -->|审批记录| D4
  P4 -->|审批状态| D2
  P4 -->|审批通过申请| P5
  P5 -->|通行码、有效期| D5

  Guard -->|通行码/预约号/手机号/证件号| P6
  P6 -->|读取预约状态| D2
  P6 -->|读取通行码| D5
  P6 -->|读取黑名单| D3
  P6 -->|读取校门信息| D7
  P6 -->|核验结果| P7
  P7 -->|入校记录| D6
  P7 -->|访问状态已入校| D2
  P7 -->|操作日志| D8
  Guard -->|离校确认| P8
  P8 -->|离校记录| D6
  P8 -->|访问状态已离校| D2
  P8 -->|操作日志| D8
  P8 --> P9
  P9 -->|超时标记| D6
  P9 -->|超时状态| D2
```

## 11. 系统架构图

对应文件：`diagrams/system_architecture.mmd`

系统架构图展示 Vue 前端、Spring Boot 后端、MySQL 数据库、Playwright 自动截图、图文件目录、截图目录和报告输出目录之间的协作关系。

```mermaid
flowchart LR
  subgraph Client[浏览器前端]
    Browser[Vue 3 + Vite]
    UI[Element Plus + ECharts]
  end

  subgraph Backend[Spring Boot 后端]
    API[REST API /api]
    Auth[JWT 鉴权]
    Service[业务服务层]
    Mapper[MyBatis Plus Mapper]
    Swagger[Swagger / Knife4j]
  end

  subgraph Database[数据库]
    MySQL[(MySQL 8\ncqupt_visitor_system)]
  end

  subgraph Automation[自动化支撑]
    Playwright[Playwright 截图脚本]
    ReportScript[LaTeX / Markdown 报告生成脚本]
    DiagramFiles[diagrams/*.mmd / *.dot]
    ScreenshotDir[screenshots/*.png]
    ReportDir[docs / reports 输出]
  end

  Browser -->|Axios + Token| API
  UI --> Browser
  API --> Auth
  API --> Service
  Service --> Mapper
  Mapper --> MySQL
  Swagger --> API

  Playwright -->|自动登录与访问页面| Browser
  Playwright -->|保存截图与 manifest| ScreenshotDir
  DiagramFiles -->|导出 PDF/SVG| ReportScript
  ScreenshotDir --> ReportScript
  MySQL -->|表结构、SQL、统计结果| ReportScript
  ReportScript -->|生成课程设计报告| ReportDir
```

## DOT 与 PDF 导出命令

如果本地已安装 Graphviz，可执行以下命令生成 LaTeX 可直接引用的 PDF 图：

```bash
dot -Tpdf diagrams/er_core_business.dot -o diagrams/export/er_core_business.pdf
dot -Tpdf diagrams/er_user_permission.dot -o diagrams/export/er_user_permission.pdf
dot -Tpdf diagrams/er_system_support.dot -o diagrams/export/er_system_support.pdf
dot -Tpdf diagrams/er_overview.dot -o diagrams/export/er_overview.pdf
dot -Tpdf diagrams/table_relation.dot -o diagrams/export/table_relation.pdf
```

如果使用 Mermaid CLI，可执行：

```bash
mmdc -i diagrams/er_core_business.mmd -o diagrams/export/er_core_business.pdf
mmdc -i diagrams/er_user_permission.mmd -o diagrams/export/er_user_permission.pdf
mmdc -i diagrams/er_system_support.mmd -o diagrams/export/er_system_support.pdf
mmdc -i diagrams/er_overview.mmd -o diagrams/export/er_overview.pdf
mmdc -i diagrams/table_relation.mmd -o diagrams/export/table_relation.pdf
```
