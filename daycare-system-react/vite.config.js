import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],

  // *** 新增以下設定來解決 Docker/WSL 檔案監聽問題 ***
  server: {
    // 確保伺服器監聽所有網路介面
    host: true,

    // 配置 HMR/檔案監聽
    watch: {
      // 啟用輪詢 (Polling) 機制
      usePolling: true,
      // 這裡可以選擇性地增加輪詢間隔，預設通常是 100ms
    }
  }
  // **********************************************
})
