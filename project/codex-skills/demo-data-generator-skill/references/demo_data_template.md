# Demo Data Template

## Required Data Coverage
| Area | Minimum Coverage |
|---|---|
| Departments | 6+ departments with hosts and approvers |
| Gates | 4+ gates with security staff |
| Roles | visitor, host, department approver, gate security, admin, school manager |
| Visitors | normal visitors, repeat visitors, blacklisted visitors |
| Vehicles | visitor vehicles with plate numbers |
| Companions | appointments with and without companions |
| Applications | pending, confirmed, rejected, approved, cancelled |
| Access records | not entered, entered, exited, overdue |
| Logs | login, approval, verification, entry, exit, blacklist changes |

## Time Distribution
- Today: enough records for dashboard and current-campus views.
- This week: approval and access records for trend charts.
- This month: department ranking and approval-rate charts.
- Historical: visitor history and query examples.

## Seed SQL Pattern
```sql
-- 1. dictionary and base organization data
-- 2. roles, permissions, users
-- 3. visitors, vehicles, companions
-- 4. visit applications
-- 5. approvals, pass codes, access records
-- 6. blacklist, notices, operation logs
```

## Consistency Rules
- `visit_apply.status` must match `approval_record` history.
- Approved applications may have `pass_code`; rejected applications must not.
- Entry records require approved pass code.
- Exit time must be after entry time.
- Overdue visitors have entry time but no exit time and planned end time in the past.