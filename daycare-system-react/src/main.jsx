import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { BrowserRouter as Router } from "react-router-dom";
import { ToastContainer } from 'react-toastify';
import { AuthProvider } from "./context/AuthContext"

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Router>          {/* 1. 路由層 */}
      <AuthProvider>  {/* 2. 應用程式最高層狀態層 */}
        <App />       {/* 3. 應用程式內容 (Layout & Routes) */}
      </AuthProvider>
    </Router>
    <ToastContainer />{/* 4. 全局 UI 層 (與路由獨立) */}
  </StrictMode>,
)
