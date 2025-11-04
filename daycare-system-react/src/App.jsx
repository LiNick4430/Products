import { useState } from 'react'
import './App.css'
import { Routes, Route, } from "react-router-dom";

// 頁面 
import Navbar from "./components/NavBar"  // 導航列
import Footer from "./components/Footer"  // 頁尾
import ProtectedRoute from './components/ProtectedRoute'; // 過濾保護的頁面

import Home from "./pages/Home"           // 首頁 
import LoginPage from "./pages/LoginPage" // 登入頁

import Register from "./pages/Register"   // 註冊頁
import RegisterSuccessPage from './pages/RegisterSuccessPage'; // 註冊成功頁
import RegisterEmailVerify from "./pages/RegisterEmailVerify" // 信箱認證頁

import UserForgetPassword from "./pages/UserForgetPassword"  // 忘記密碼
import UserForgetPasswordVerify from "./pages/UserForgetPasswordVerify";  // 忘記密碼驗證
import UserMe from "./pages/UserMe"

import UserPublicInformation from './pages/UserPublicInformation';

function App() {

  return (
    <>
      {/* 上方 導覽列 */}
      <Navbar />

      {/* 中間 內容 */}
      <div className='content'>
        <Routes>
          {/* ---------- 公開路由 ---------- */}
          {/* 首頁路由 */}
          <Route path="/" element={<Home />} />

          {/* 登陸路由 */}
          <Route path="/login" element={<LoginPage />} />

          {/* 註冊路由 */}
          <Route path="/register" element={<Register />} />
          {/* 註冊成功 提示收信 路由 */}
          <Route path="/register/success" element={<RegisterSuccessPage />} />
          {/* 註冊成功 信箱認證 路由 */}
          <Route path="/email/verify" element={<RegisterEmailVerify />} />

          {/* 忘記密碼 主路由 (送信 和 修改) */}
          <Route path="/forget/password" element={<UserForgetPassword />} />
          {/* 忘記密碼 信箱轉送 路由 */}
          <Route path="/email/reset/password" element={<UserForgetPasswordVerify />} />

          {/* ---------- 受保護路由 ---------- */}

          {/* 帳號本人資料 */}
          <Route path="/user/me" element={<ProtectedRoute element={UserMe} />} />

          {/* 更新帳號 基本資料 密碼驗證 */}

          {/* 更新帳號 基本資料 */}

          {/* 刪除帳號 */}

          {/* ---------- 民眾 ---------- */}
          {/* 建立民眾基本資料 */}
          <Route path="/public/user/information" element={<ProtectedRoute element={UserPublicInformation} requiredRoles={["ROLE_PUBLIC"]} />} />

          {/* 更新 民眾基本資料 */}
          <Route path="/public/user/update" element={<ProtectedRoute element={<></>} requiredRoles={["ROLE_PUBLIC"]} />} />

          {/* 設定 新幼兒資料 */}
          <Route path="/child/information/" element={<ProtectedRoute element={<></>} requiredRoles={["ROLE_PUBLIC"]} />} />

          {/* 更新 幼兒資料 */}
          <Route path="/child/update/" element={<ProtectedRoute element={<></>} requiredRoles={["ROLE_PUBLIC"]} />} />

          {/* ---------- 基層人員 ---------- */}
          {/* 尋找 全部民眾  */}
          <Route path="/public/user/find/all" element={<ProtectedRoute element={<></>} requiredRoles={["ROLE_MANAGER", "ROLE_STAFF"]} />} />

          {/* ---------- 管理者 ---------- */}
          {/* 尋找 全部員工 */}
          <Route path="/admin/user/find/all" element={<ProtectedRoute element={<></>} requiredRoles={["ROLE_MANAGER"]} />} />

          {/* 尋找 特定員工 */}
          <Route path="/admin/user/find/" element={<ProtectedRoute element={<></>} requiredRoles={["ROLE_MANAGER"]} />} />

          {/* 建立新的 員工 */}
          <Route path="/admin/user/create" element={<ProtectedRoute element={<></>} requiredRoles={["ROLE_MANAGER"]} />} />

          {/* 更新 員工 資料 */}
          <Route path="/admin/user/update" element={<ProtectedRoute element={<></>} requiredRoles={["ROLE_MANAGER"]} />} />

        </Routes>
      </div>

      {/* 下方 頁尾 */}
      <Footer />
    </>
  )
}

export default App
