# RBAC Template

## Role Permission Matrix
| Role | Menu | API Permission | Data Scope |
|---|---|---|---|
| visitor | Appointment, My Visits | submit/query own appointments | own records |
| host | Pending Confirmations | confirm/reject hosted applications | hosted records |
| department_approver | Department Approval | approve/reject department visits | department records |
| gate_security | Gate Verification, Entry, Exit | verify pass, register entry/exit | gate records |
| system_admin | System Management | manage users, roles, permissions, departments, gates, blacklist, logs | all |
| school_manager | Statistics, Records | view reports and full visitor records | all read-only |

## Backend Checks
- Authenticate request.
- Load user roles and permissions.
- Compare endpoint permission.
- Check data scope for department or ownership.

## Frontend Checks
- Build route table from permissions.
- Hide unavailable menus.
- Disable state-invalid actions.
