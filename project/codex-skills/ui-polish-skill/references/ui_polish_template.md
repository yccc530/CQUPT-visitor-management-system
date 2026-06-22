# UI Polish Template

## Theme Tokens
| Token | Recommended Value | Usage |
|---|---|---|
| primary | `#1D4ED8` | main buttons, active menu, links |
| primary_soft | `#DBEAFE` | light backgrounds and selected states |
| accent | `#06B6D4` | secondary metrics and chart accents |
| success | `#16A34A` | approved, entered, normal |
| warning | `#F59E0B` | pending, overtime warning |
| danger | `#DC2626` | rejected, blacklist, abnormal |
| text_main | `#111827` | primary text |
| text_muted | `#6B7280` | descriptions |
| border | `#E5E7EB` | dividers and table borders |

## Required Layout
- Sidebar with logo/title and role-aware menu.
- Topbar with breadcrumb, user name, role tag, and logout.
- Dashboard content starts with compact statistic cards.
- Management pages use filter area, table, pagination, and dialogs.
- Workflow pages show current status and next valid actions.

## Component Standards
| Component | Standard |
|---|---|
| Statistic card | icon, label, value, change hint, compact trend |
| Table | sticky operation column if needed, status tags, empty state |
| Form | grouped fields, required marks, validation messages |
| Dialog | clear title, primary action, cancel action, loading state |
| Chart | title, legend, tooltip, report-friendly colors |
| Empty state | contextual message and next action |

## Page Checklist
- Login page has system name, campus context, and clean form validation.
- Dashboard shows statistics, trend chart, department ranking, gate passage chart, and shortcuts.
- Approval and gate pages expose only status-valid actions.
- Report pages have filters and chart/table dual presentation.
- Management pages have create/edit/delete or enable/disable actions where appropriate.