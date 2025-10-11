# 階段 1: 基礎環境
# 使用 Node.js 基礎映像檔作為建構環境
FROM node:20-alpine AS development

# 設定容器內的工作目錄
WORKDIR /app

# 複製 package.json 和 package-lock.json (利用 Docker Cache 機制加速)
COPY package*.json ./

# 安裝所有依賴
RUN npm install

# 複製所有專案檔案
COPY . .

# 曝露 Vite 預設的開發埠口
EXPOSE 5173

# 定義容器啟動時執行的命令
# 這裡使用 'dev' 腳本，就是 npm run dev
CMD ["npm", "run", "dev"]