import { useState } from 'react'
import './App.css'
import {
  Routes,
  Route,
} from "react-router-dom";

// 
import { AuthProvider } from "./context/AuthContext"

import Navbar from "./components/NavBar"  // 導航列 
import Footer from "./components/Footer"  // 頁尾 

import Home from "./pages/Home"           // 首頁 
import LoginPage from "./pages/LoginPage" // 登入頁
import Register from "./pages/Register"   // 註冊頁
import RegisterSuccessPage from './pages/RegisterSuccessPage'; // 註冊成功頁
import EmailVerifyPage from "./pages/EmailVerifyPage" // 信箱認證頁

function App() {
  return (
    // 使用 AuthProvider 包裹 AppContent
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}

function AppContent() {
  return (
    <>
      {/* 上方 導覽列 */}
      <Navbar />

      {/* 中間 內容 */}
      <div className='content'>
        <Routes>
          {/* 首頁路由 */}
          <Route path="/" element={<Home />} />

          {/* 登陸路由 */}
          <Route path="/login" element={<LoginPage />} />

          {/* 註冊路由 */}
          <Route path="/register" element={<Register />} />

          {/* 註冊成功後 路由 */}
          <Route path="/register-success" element={<RegisterSuccessPage />} />

          {/* 帳號認證 路由 */}
          <Route path="/email/verify" element={<EmailVerifyPage />} />

        </Routes>
      </div>

      {/* 下方 頁尾 */}
      <Footer />
    </>
  )

}

export default App
