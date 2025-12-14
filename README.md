# 公托管理系統（Daycare Management System）

**Spring Boot × React × Docker × PostgreSQL | 全端作品集**

## 📌 專案簡介（Project Overview）

本專案為一套 **後端導向的全端公托管理系統**，整合 **Spring Boot（後端）** 與 **React（前端）**，核心目標在於展示：

企業級後端架構設計能力、複雜業務流程控管，以及雲端部署與環境標準化能力。

系統涵蓋公托機構管理、公告與規範管理、使用者與角色權限控管，並為後續複雜案件審核流程提供穩定可擴充的基礎架構。

## 🌟 專案核心亮點（Key Highlights）

### 🔐 企業級安全機制
- Spring Security + JWT
- 完整實作無狀態驗證（Stateless Authentication）
- 角色 / 權限（RBAC）細緻控管 API 存取

### 🔄 複雜業務流程設計
- 雙階段案件審核 + 抽籤流程
- Service 層以 Enum + 狀態檢查確保流程合法性

### 🧱 清晰可維護的後端架構
- Controller / Service / Repository 三層架構
- DTO 與 Entity 明確分離，避免業務層污染

### ☁️ 雲端部署與環境一致性
- Render 雲端部署（PostgreSQL）
- Docker Multi-stage Build
- Profile 區分 Local / Render 行為
- 雲端環境每次重啟自動重建資料庫（僅保留預設資料）

## 🛠 技術棧（Tech Stack）

| 領域 | 技術 | 說明 |
|----|----|----|
| 後端 | Spring Boot (Java 17) | REST API、商業邏輯、權限控管 |
| ORM | Spring Data JPA / Hibernate | ORM 與交易管理 |
| 資料庫 | MySQL / PostgreSQL | Local / Cloud 環境切換 |
| 前端 | React | UI 與 API 串接 |
| 安全 | Spring Security + JWT | 無狀態驗證 |
| 容器化 | Docker / Docker Compose | 環境標準化 |
| 郵件測試 | MailHog / Mailtrap | 本地 / 雲端郵件測試 |

## 🚀 系統功能模組（Features）

### 1️⃣ 使用者與角色權限管理（RBAC）
- 支援三種角色：民眾（ROLE_PUBLIC）、員工（ROLE_STAFF）、高階主管（ROLE_MANAGER）
- JWT 驗證 + 權限攔截
- 高階主管可管理後台帳號與權限

### 2️⃣ 機構、公告與規範管理
- 機構公告 CRUD
- 規範文件管理
- 支援檔案上傳 / 下載
- 公告草稿 / 發布狀態控制

### 3️⃣ 案件審核與分發流程（核心業務）(正在製作ING)
- 多階段案件審核
- 抽籤與候補邏輯
- Service 層嚴格狀態驗證

📌 案件流程示意圖：
<img src="docs/images/案件審核%20流程圖%20.png" width="500" alt="案件審核流程圖" />

## 🔐 後端安全架構說明（Spring Security + JWT）
- 自訂 JwtAuthenticationFilter
- JwtUtil 負責 Token 生成 / 驗證 / 解析
- 客製化錯誤處理：JwtAuthenticationEntryPoint（401）、CustomAccessDeniedHandler（403）

📌 JWT 認證流程示意：
<img src="docs/images/Spring%20Security%20%2B%20JWT%20認證流程.png" width="500" alt="Spring Security + JWT 認證流程" />

## 🧱 專案架構設計（Backend Architecture）
- Controller：處理 HTTP 請求
- Service：封裝業務邏輯與流程控管
- Repository：資料存取層
- Converter / DTO：使用 ModelMapper，避免 Entity 直接暴露給前端
- GlobalExceptionHandler：統一 API Response 格式，提升前後端協作一致性

## ☁️ 環境與部署策略（Environment & Deployment）

### Profile 設計

| 環境 | Profile | 行為 |
|----|----|----|
| 本地 | local | 保留資料、方便開發 |
| 雲端 | render | 每次重啟重建資料庫 |

### 雲端行為說明（Render）
- spring.jpa.hibernate.ddl-auto=create
- spring.sql.init.mode=always
- 啟動時自動執行 data.sql
- 資料庫只保留系統預設資料

📌 此設計確保：
- 雲端展示環境可重建
- Demo 資料永遠一致
- 避免測試資料污染

## 🧪 本地開發環境啟動（Getting Started）

### 1️⃣ 啟動前端與 MailHog（Docker）
git clone <your-github-repo>
cd Products
docker-compose up -d --build

服務 URL：
- Backend API: http://localhost:8080
- Frontend: http://localhost:5173
- MailHog: http://localhost:8025

### 2️⃣ 啟動後端（Spring Boot）
- 使用 IDE（IntelliJ / Eclipse）
- 啟用 application-local.properties

## 🧠 設計決策與挑戰（Design Decisions）

### 複雜狀態流程設計
- 挑戰：多階段審核 + 抽籤流程，狀態錯誤會導致資料不一致
- 解法：使用 Enum 定義狀態，Service 層強制狀態驗證，不合法操作直接丟出業務例外

### 🔮 未來擴充方向
- Redis 快取（公告 / 規範）
- Spring Actuator + Monitoring
- 完整 CI/CD Pipeline

## 📊 資料庫設計（ERD）
<img src="docs/images/Google雲端.png" width="200" alt="QR Code" />

⚠️ Note: Known technical debt and deployment-related caveats are documented in TECH-DEBT.md.
