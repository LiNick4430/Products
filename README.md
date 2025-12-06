## 🚀 公托系統開發專案大綱與環境指南

這是一個整合後端 Spring Boot、前端 React 的現代全端專案，並透過 Docker 進行標準化環境管理。

### 核心技術棧

| 領域 | 技術/框架 | 說明 |
| :--- | :--- | :--- |
| **後端核心** | **Spring Boot** (Java) | 負責業務邏輯、資料庫互動及 API 服務。 |
| **前端核心** | **React** (JavaScript) | 負責用戶介面 (UI) 和使用者互動體驗。 |
| **環境管理** | **Docker / MailHog** | 用於環境一致性，並提供本地郵件伺服器進行開發測試。 |

### 後端架構重點

* **安全性 (Security):** 採用 **Spring Security** 框架，實現基於 **JWT (JSON Web Token)** 的身份驗證與授權過濾機制。
* **統一響應：** 實作 **`GlobalExceptionHandler`** 和 **`ApiResponse`** 統一格式，確保前端能以標準化結構接收所有 API 響應（無論成功或錯誤）。

