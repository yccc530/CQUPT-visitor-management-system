# Fullstack Integration Check Template

## API Contract Matrix
| Module | Frontend API Function | Method | Path | Backend Controller | Request DTO | Response VO | Permission |
|---|---|---|---|---|---|---|---|

## Field Mapping Matrix
| Business Field | Database Column | Java Entity/DTO | Frontend Field | Notes |
|---|---|---|---|---|

## Status Mapping Matrix
| Status Type | Database Value | Backend Constant | Frontend Tag | Valid Actions |
|---|---|---|---|---|

## Integration Bug Record
| ID | Symptom | Cause | Fix | Verification |
|---|---|---|---|---|

## Required Checks
- Login response token field matches Axios interceptor.
- Pagination request and response formats are consistent.
- Date-time format is parseable by frontend and stored correctly in MySQL.
- Role-menu permissions match backend endpoint permissions.
- Approval and access workflow statuses match seed data and UI tags.