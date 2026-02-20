# database

Room database setup.

## Files

- `AppDatabase.kt` — Room database class. Currently contains AlarmEntity only (v1). New entities added per iteration.
- `Converters.kt` — Room TypeConverters for serializing complex types (days of week, etc.).

## Subfolders

- `migrations/` — Room database migration classes (empty until schema changes in Iteration 2.3+).
