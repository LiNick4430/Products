# Products 說明 
使用 **Spring Boot** 框架進行開發，並搭配 **Docker 容器化的 MailHog** 進行本地郵件測試，避免開發過程中發送真實郵件。

## 🔧 環境設定與安裝指南 (Local Environment Setup)
### 1. 安裝 Docker
1.  **下載並安裝 Docker Desktop：**
    * 請前往 [Docker 官方網站](https://www.docker.com/products/docker-desktop/) 下載並安裝。
2.  **帳號建議：**
    * 建議使用 **Personal (個人)** 模式建立帳號。

### 2. (Windows) 升級 wsl 版本
1. 開啟 CMD / PowerShell (以**管理員權限**執行)。
2.  執行更新指令：
    ```bash
    wsl --update --web-download
    ```

### 3. 建立 mailhog
此步驟僅需在**首次**設定環境時執行一次。

1.  開啟 CMD 或 PowerShell。
2.  執行以下指令：
    ```bash
    docker run -d --name mailhog -p 1025:1025 -p 8025:8025 mailhog/mailhog
    ```
    
#### 指令參數說明：
| 參數 (Command) | 說明 (Description) |
| :--- | :--- |
| `docker run` | 使用 `mailhog/mailhog` 映像檔 (Image) 建立並啟動容器 (若本機沒有會自動下載)。 |
| `-d` | **Detached mode (分離模式)**，讓容器在背景運行，不佔用終端機。 |
| `--name mailhog` | 為容器指定名稱為 `mailhog`，方便後續管理。 |
| `-p 1025:1025` | **SMTP 埠口對應**：將主機 (Host) 的 1025 埠口對應到容器內 MailHog 服務的 1025 埠口。您的 Spring Boot 應用程式將郵件發送到此埠口。 |
| `-p 8025:8025` | **Web UI 埠口對應**：將主機 (Host) 的 8025 埠口對應到容器內 MailHog 網頁介面的 8025 埠口。 |
| `mailhog/mailhog` | 用來建立容器的 Docker 映像檔（Image）名稱。 |

### 4. 管理  mailhog
只要 **Docker Desktop** 在運行，您就可以管理 MailHog 容器。

1.  **開啟 Docker Desktop 應用程式。**
2.  點選左側導航欄的 **Containers** (容器) 區塊。
3.  您可以看到類似以下的容器資訊：
    | Name | Image | Port(s) | Actions |
    | :--- | :--- | :--- | :--- |
    | `mailhog` | `mailhog/mailhog` | `1025:1025 8025:8025` | (Start/Stop/Restart 按鈕) |
4.  利用 Actions 欄位中的圖示按鈕來控制 `mailhog` 服務的 **啟動** 或 **停止**。

#### 🔗 存取 MailHog 網頁介面
您可以在瀏覽器中輸入以下網址，查看所有攔截到的測試郵件： http://localhost:8025
