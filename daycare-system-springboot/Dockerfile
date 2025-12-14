# ======================================================================
# 階段 1: 建構階段 (Build Stage) - 使用 Maven/JDK 來編譯和打包應用程式
# ======================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

# 設定工作目錄
WORKDIR /app

# 複製 Maven 專案檔案，用於依賴下載（利用 Docker 快取加速）
COPY pom.xml .

# 複製原始碼
COPY src /app/src

# 執行 Maven 建構指令
# 啟用 -Prender-postgres Profile 來包含 PostgreSQL 驅動
# -DskipTests 跳過測試以加快建構速度
RUN mvn clean install -DskipTests -Prender-postgres

# ======================================================================
# 階段 2: 運行階段 (Run Stage) - 使用輕量級 JRE 運行 JAR 檔案
# ======================================================================
# 選擇一個更小的 JRE 基礎映像檔，以減少最終映像檔大小
FROM eclipse-temurin:17-jre-alpine

# 將建構階段產生的 JAR 檔案複製到運行映像檔中
# 您的 JAR 檔案名稱應該是 target/daycare-system-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/daycare-system-0.0.1-SNAPSHOT.jar app.jar

# 將上傳資料的永久儲存 Disk 掛載點建立為資料夾
RUN mkdir -p /data/uploads

# 設定環境變數：Render 服務會自動注入 SPRING_DATASOURCE_URL 等變數 
# ENV SPRING_PROFILES_ACTIVE=render	(搬到 Render 的 環境變數 使用)

# 定義應用程式啟動命令
# 啟動時使用 --spring.profiles.active=render 確保載入 application-render.properties
ENTRYPOINT ["java", "-jar", "app.jar"]