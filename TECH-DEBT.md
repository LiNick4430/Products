# Technical Debt

## Render startup DDL warning

- Symptom  
  Render 啟動時偶發：
  `unique constraint already exists`

- Root cause  
  Hibernate DDL auto create 與 PostgreSQL catalog sync 時序問題

- Impact  
  不影響 API 啟動與功能（目前為低風險）

- Planned fix  
  - 啟動前 DROP SCHEMA public CASCADE
  - 或導入 Flyway / Liquibase 管理 migration

- Status  
  Deferred (acceptable in Render Free test environment)