# Screenshot Automation Template

## Screenshot Manifest Schema
```json
[
  {
    "id": "dashboard",
    "title": "首页统计驾驶舱",
    "route": "/dashboard",
    "role": "school_manager",
    "file": "screenshots/dashboard.png",
    "reportSection": "系统实现",
    "description": "展示今日访客、当前在校、超时未离校、审批统计和访问趋势。"
  }
]
```

## Required Route Plan
| ID | Page | Role | Suggested Route | Filename |
|---|---|---|---|---|
| login | 登录页 | anonymous | `/login` | `01_login.png` |
| dashboard | 首页统计驾驶舱 | school_manager | `/dashboard` | `02_dashboard.png` |
| appointment | 访客预约申请页 | visitor | `/visitor/apply` | `03_appointment.png` |
| approval | 部门审批页 | department_approver | `/approval/department` | `04_approval.png` |
| gate | 门岗核验页 | gate_security | `/gate/verify` | `05_gate_verify.png` |
| entry | 入校登记页 | gate_security | `/access/entry` | `06_entry.png` |
| exit | 离校登记页 | gate_security | `/access/exit` | `07_exit.png` |
| blacklist | 黑名单管理页 | admin | `/blacklist` | `08_blacklist.png` |
| statistics | 统计报表页 | school_manager | `/statistics` | `09_statistics.png` |
| users | 用户管理页 | admin | `/system/users` | `10_users.png` |
| logs | 系统日志页 | admin | `/system/logs` | `11_logs.png` |

## Playwright Checklist
- Use one reusable login helper.
- Clear storage between role switches.
- Wait for a stable app container and page-specific selector.
- Capture full-page PNG.
- Write manifest after successful captures.