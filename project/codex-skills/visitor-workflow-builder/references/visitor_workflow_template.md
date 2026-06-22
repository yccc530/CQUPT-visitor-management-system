# Visitor Workflow Template

## Appointment State Transitions
| Current State | Action | Actor | Preconditions | Next State | Data Written |
|---|---|---|---|---|---|
| 待确认 | 确认 | 被访人 | not blacklisted | 待部门审批 | approval_record |
| 待确认 | 拒绝 | 被访人 | has reason | 被访人已拒绝 | approval_record |
| 待部门审批 | 通过 | 部门审批人员 | valid host confirmation | 审批通过 | approval_record, pass_code |
| 待部门审批 | 拒绝 | 部门审批人员 | has reason | 审批拒绝 | approval_record |

## Access State Transitions
| Current State | Action | Actor | Preconditions | Next State | Data Written |
|---|---|---|---|---|---|
| 未入校 | 入校登记 | 门岗安保 | approved pass code | 已入校 | access_record.entry_time |
| 已入校 | 离校登记 | 门岗安保 | no exit_time yet | 已离校 | access_record.exit_time |
| 已入校 | 超时检查 | 系统/管理人员 | visit_end_time exceeded | 超时未离校 | query/result flag |

## Business Guards
- Check blacklist before accepting appointment.
- Check pass code validity before entry.
- Keep approval history append-only.
- Use clear error messages for invalid transitions.
