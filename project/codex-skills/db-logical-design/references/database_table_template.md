# Database Table Design Template

## Table Summary
| Table | Chinese Name | Purpose | Main Relationships |
|---|---|---|---|

## Field Design
| Field | Type | PK | FK | Nullable | Default | Description |
|---|---|---|---|---|---|---|

## Relation Schema
Use this notation:
`table_name(primary_key, normal_field, foreign_key -> referenced_table.referenced_key)`

## Normalization Check
- 1NF: each field stores atomic values.
- 2NF: non-key fields depend on the whole primary key.
- 3NF: non-key fields do not transitively depend on other non-key fields.

## MySQL Type Hints
- IDs: `BIGINT`
- Names: `VARCHAR(50)` or `VARCHAR(100)`
- Status: `VARCHAR(30)` or `TINYINT` with documentation
- Time: `DATETIME`
- Long reason/remark: `VARCHAR(255)` or `TEXT`
