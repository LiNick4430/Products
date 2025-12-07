# 公托系統 (Daycare Management System) - 全棧作品集 

## 專案簡介與核心亮點 (Project Overview)

這是一個整合**後端 Spring Boot** 與 **前端 React** 的現代全端公托管理系統。專案核心著重於**後端服務的企業級架構、高安全性、以及標準化的 API 設計**，確保系統的穩定性、可維護性和高效率。

> **專案亮點：**
>
> * **企業級安全：** 採用 Spring Security 搭配 **JWT 完整實作**無狀態身份驗證與授權過濾。
> * **複雜流程控制：** 實作**雙階段案件審核與抽籤流程**的狀態機管理。
> * **專業架構：** 嚴格遵循**三層架構**，並利用 DTO 轉換器 (`converter` / `ModelMapper`) 實現資料物件的解耦。
> * **環境標準化：** 透過 **Docker Compose** 管理前端開發與測試輔助服務，確保開發環境快速一致。
---

## 核心技術棧 (Tech Stack)

| 領域 | 技術/框架 | 說明 |
| :--- | :--- | :--- |
| **後端核心** | **Spring Boot** (Java) | 負責所有業務邏輯、API 服務和資料庫互動。 |
| **資料持久層** | **Spring Data JPA/Hibernate** | 簡化資料庫操作，專注於物件關係映射 (ORM)。 |
| **前端核心** | **React** (JavaScript) | 負責用戶介面 (UI) 和使用者互動體驗。 |
| **環境與測試** | **Docker / Docker Compose** | 容器化部署環境。 |
| **輔助工具** | **MailHog** | 提供本地郵件伺服器，用於開發時的郵件通知測試。 |

---

## 後端 Spring Boot 架構深度解析

### 企業級安全機制 (`config` & `util` 層)

專案的核心安全架構建立在 Spring Security 上，通過自定義組件確保 API 存取的嚴謹性。

* **無狀態身份驗證 (JWT 整合)：**
    * 透過 **`JwtAuthenticationFilter`** 在請求到達 Controller 前進行 Token 驗證與用戶身份解析。
    * 深入理解並實作了 [Spring Security + JWT 認證流程] 所示的 JWT 認證流程。
      <img src="docs/images/Spring%20Security%20%2B%20JWT%20認證流程.png" width="500" alt="Spring Security + JWT 認證流程" />
    * `JwtUtil` 集中處理 Token 的生成、解析、以及過期驗證邏輯。
* **安全入口與處理器：**
    * 自定義 **`CustomAccessDeniedHandler`** (處理 403 拒絕訪問) 和 **`JwtAuthenticationEntryPoint`** (處理 401 未經授權) 的標準化響應。
* **密碼安全：**
    * 利用 **`PasswordSecurity`** 實作安全的密碼散列 (Hashing) 與比對機制。

### 專業分層與職責分離

我們追求高可維護性、高內聚、低耦合的程式碼設計。

* **標準化三層架構：** 嚴格區分 **Controller**、**Service**、**Repository** 的職責。
* **資料物件轉換：** 專門使用 **`converter`** 套件，並透過 **`ModelMapperConfig`** 配置，集中處理 DTO 與 Model 之間的資料映射，避免在業務邏輯層混入轉換細節。
* **統一響應與異常處理：**
    * 實作 **`GlobalExceptionHandler`**，並使用 **`response`** 套件中的 **`ApiResponse`** 統一格式化所有 API 的成功或錯誤響應。
    * `exception` 層定義所有自定義業務異常，確保錯誤訊息格式統一且清晰，便於前端處理。

---

## 核心功能模組 (Features)

1.  **用戶與員工權限管理**
    * 支援民眾、普通員工、高層員工三種角色，通過 JWT 進行權限分配和驗證。
    * 高層員工具備員工帳號的創建與權限修改功能。

2.  **公告與資料文件管理**
    * 實作機構公告與規範的 CRUD (增/查/改/刪) 操作。
    * 支援公告附件、機構附件的**上傳與下載**功能。

3.  **複雜案件審核與分發流程 (核心業務邏輯)**
    * 員工根據**雙階段審核與抽籤流程**對案件進行操作。
    * **流程圖佐證：** 案件從申請到分發的複雜狀態機請參考。
      <img src="docs/images/案件審核%20流程圖%20.png" width="500" alt="案件審核流程圖" />

---

## 環境啟動與部署指南 (Getting Started)

### 1. 服務地址

| 服務名稱 | 地址 |
| :--- | :--- |
| **後端服務 API** | `http://localhost:8080` |
| **前端介面 (容器)** | `http://localhost:5173` |
| **MailHog 郵件監控 (容器)** | `http://localhost:8025` |

### 2. 先決條件 (Prerequisites)

* **Java 17+**
* **Node.js (LTS) & npm**
* **Docker & Docker Compose**
* **資料庫環境：** 確保您的本地 MySQL 或 PostgreSQL 資料庫已啟動並配置正確。

### 3. 啟動容器化服務 (React 前端與 MailHog)

此步驟將啟動前端開發環境和郵件測試服務。

* **優化說明：** 前端服務 (`frontend-dev`) 設置了 Volume 掛載 (`./daycare-system-react:/app`) 並採用 `npm run dev -- --host` 指令，確保容器內程式碼與主機同步，實現**熱重載 (HMR)**，提高開發效率。

```bash
# 1. 複製專案
git clone [您的 GITHUB 連結]

# 2. 進入包含 docker-compose.yml 的資料夾 (專案根目錄)
cd Products

# 3. 啟動 React 前端容器與 MailHog
docker-compose up -d --build
```

### 4. 啟動 Spring Boot 後端服務 (獨立運行)
請確保您的 daycare-system-springboot/src/main/resources 下的配置文件已指向正確的本地資料庫。

#### 啟動 Spring Boot 應用程式
#### 透過 IDE (如 IntelliJ IDEA 或 Eclipse) 啟動主類別 

---

## 設計決策與挑戰 (Design Decisions and Challenges)

* **技術選型：** 選擇 Spring Boot 的主要原因在於其成熟的生態系統、內建的安全性框架（Spring Security），以及企業級的穩定性，使其非常適合處理複雜的業務邏輯。
* **最大挑戰：複雜業務流程的狀態機實現**
    * **挑戰：** 如何在後端穩定且可靠地實現如 [案件審核 流程圖 .png] 所示的**多階段審核、抽籤、分發**的複雜狀態轉移。一個錯誤的操作或狀態跳轉可能導致資料錯誤。
    * **解決方案：** 我在 Service 層針對案件狀態實作了**嚴格的狀態檢查機制**（使用 Enum 進行狀態約束），確保只有在滿足特定條件時，才能觸發下一階段的業務邏輯，保證了流程的合規性與資料的一致性。
* **未來改進：**
    * **性能優化：** 引入 **Redis 快取**針對公告內容、機構規範等頻繁查詢但更新較少的靜態資料進行優化，以提升整體系統效能。
    * **監控與日誌：** 整合 Spring Boot Actuator 與 ELK/Prometheus 進行生產環境的監控與日誌追蹤。
 
---

## 設計資料庫 QR Code

<img src="docs/images/Google雲端.png" width="200" alt="QR Code" />
