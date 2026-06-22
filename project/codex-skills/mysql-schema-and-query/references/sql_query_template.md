# SQL Script And Query Template

## create_database.sql
```sql
CREATE DATABASE IF NOT EXISTS database_name
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE database_name;
```

## schema.sql Table Pattern
```sql
CREATE TABLE table_name (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## query_examples.sql Pattern
```sql
-- Query name:
-- Purpose:
-- Used in report section:
SELECT ...
FROM ...
WHERE ...;
```

## Validation Checklist
- Can tables be created from top to bottom?
- Do inserts respect foreign keys?
- Do query filters match indexed fields?
- Do comments explain the business purpose?
