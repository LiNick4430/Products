import { useState, useEffect, useCallback } from 'react'
import './App.css'
import {
  Routes,
  Route,
  useNavigate,
} from "react-router-dom";

// 導航列 相關模組
import Navbar from "./components/NavBar"

// 頁尾 相關模組
import Footer from "./components/Footer"

// 首頁 
import Home from "./pages/Home"

// 登入相關模組 + API
import LoginPage from "./pages/LoginPage"
import { login, logout, updateAccessToken } from "./services/authService"

// 帳號相關模組 + API
import { getUserDetails } from "./services/userService"

function App() {
  const [isLoggedin, setIsLoggedin] = useState(false);  // 是否登入
  const [isLoading, setIsLoading] = useState(false);    // 載入狀態
  const [error, setError] = useState(null);             // 錯誤訊息狀態

  // 將 Token / 角色名稱 / 帳號名稱 儲存在狀態中
  const [accessToken, setAccessToken] = useState(null);
  const [refreshToken, setRefreshToken] = useState(null);
  const [roleName, setRoleName] = useState(null);
  const [username, setUsername] = useState(null);

  // useNavigate()	Hook 呼叫	這是 React Router 提供的一個 Hook，用於獲取導航的功能。
  // const navigate	導航函式	Maps 是一個函式，您可以呼叫它來改變瀏覽器的 URL，無需重新載入整個頁面。
  const navigate = useNavigate();

  // 登入處理
  async function handleLogin(username, password) {
    setIsLoading(true); // 開始載入
    setError(null);

    try {
      // 呼叫 API 服務
      const response = await login(username, password);

      // 儲存 accessToken, refreshToken
      localStorage.setItem("accessToken", response.data.accessToken);
      localStorage.setItem("refreshToken", response.data.refreshToken);
      localStorage.setItem("roleName", response.data.roleName);
      localStorage.setItem("username", response.data.username);
      setAccessToken(response.data.accessToken);
      setRefreshToken(response.data.refreshToken);
      setRoleName(response.data.roleName);
      setUsername(response.data.username);

      // 更新登入狀態
      setIsLoggedin(true);

      // 導航到 應用程式的主頁
      navigate("/");

    } catch (e) {
      // 處理網路錯誤或 API 服務拋出的錯誤
      console.error("登入時發生錯誤", e);
      setError(e.message || "網路或服務器連接錯誤");
    } finally {
      setIsLoading(false);
    }
  }

  // 登出處理
  const handleLogout = useCallback(async () => {
    setIsLoading(true);
    try {
      // 呼叫 API 服務
      await logout(accessToken);

    } catch (e) {
      // 處理網路錯誤或 API 服務拋出的錯誤
      console.error("登入時發生錯誤", e);
      setError(e.message || "網路或服務器連接錯誤");
    } finally {
      // 清除 accessToken, refreshToken
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("roleName");
      localStorage.removeItem("username");
      setAccessToken(null);
      setRefreshToken(null);
      setRoleName(null);
      setUsername(null);

      // 更新登入狀態
      setIsLoggedin(false);

      // 導航到 應用程式的主頁
      navigate("/");

      setIsLoading(false);
    }
  }, [accessToken, navigate]);

  // 第一次進入頁面 的 預先處理
  useEffect(() => {
    // 1. 嘗試從 localStorage 讀取 TOKEN
    const storeAccessToken = localStorage.getItem("accessToken");
    const storeRefreshToken = localStorage.getItem("refreshToken");
    const storeRoleName = localStorage.getItem("roleName");
    const storeUsername = localStorage.getItem("username");

    if (storeAccessToken && storeRefreshToken && storeRoleName && storeUsername) {
      // 1. 檢測 登陸 和 認證
      const checkAndSetAuth = async () => {
        setIsLoading(true);

        try {
          // 1. 使用 Access Token 嘗試 讀取帳號資料
          await getUserDetails(storeAccessToken);

          // 2. 成功 -> TOKEN有效
          setAccessToken(storeAccessToken);
          setRefreshToken(storeRefreshToken);
          setRoleName(storeRoleName);
          setUsername(storeUsername);
          setIsLoggedin(true);

        } catch (e) {
          // 3. 失敗的 可能是 JWT 過期
          if (e.message.includes("JWT_EXPIRED")) {
            console.log("Access Token 已過期，嘗試刷新...");
            // 4. 執行 TOKEN 刷新
            try {
              const newTokens = await updateAccessToken(storeRefreshToken);  // 呼叫 更新 ACCESS TOKEN 服務

              // 5. 刷新成功的場合
              setAccessToken(newTokens.data.accessToken);
              setRefreshToken(newTokens.data.refreshToken);
              setRoleName(newTokens.data.roleName);
              setUsername(newTokens.data.username);
              setIsLoggedin(true);
              console.log("Token 刷新成功，恢復登入狀態。")

            } catch (errorToken) {
              // 6. 刷新失敗的場合
              console.log(errorToken.message);
              // 執行登出邏輯 (清除所有 Token 和狀態)
              handleLogout();
            }
          } else {
            // 7. 處理其他錯誤
            console.log(e.message);
            // 執行登出邏輯 (清除所有 Token 和狀態)
            handleLogout();
          }
        } finally {
          setIsLoading(false);
        }
      };

      checkAndSetAuth();
    }
  }, [handleLogout, navigate]);

  return (
    <>
      {/* 上方 導覽列 */}
      <Navbar username={username} isLoggedIn={isLoggedin} onLogout={handleLogout} />

      {/* 中間 內容 */}
      <div className='content'>
        <Routes>
          {/* 首頁路由 */}
          <Route path="/" element={<Home />} />

          {/* 登陸路由 */}
          <Route path="/login" element={<LoginPage onLogin={handleLogin} isLoggedIn={isLoggedin} isLoading={isLoading} error={error} />} />

        </Routes>
      </div>

      {/* 下方 頁尾 */}
      <Footer />
    </>
  )
}

export default App
