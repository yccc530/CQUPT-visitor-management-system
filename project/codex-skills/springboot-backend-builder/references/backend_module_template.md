# Backend Module Template

## Package Layout
```text
backend/src/main/java/.../
  common/
  config/
  controller/
  dto/
  entity/
  exception/
  mapper/
  service/
  service/impl/
  vo/
```

## Entity Checklist
- Match table and field names.
- Use appropriate Java types.
- Include validation where input DTOs need it.

## Service Checklist
- Check business preconditions.
- Write database operations through mapper/repository.
- Return DTO/VO objects where useful.
- Throw meaningful business exceptions.

## Controller Checklist
- Use clear REST paths.
- Validate request body and parameters.
- Return unified response objects.
