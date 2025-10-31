import { useState, useCallback, useEffect, createContext, useContext } from 'react';
import { useNavigate } from 'react-router-dom';

import { login, logout, updateAccessToken } from "../services/authService"
import { getUserDetails, register } from "../services/userService"

// 1. 定義 Context 物件 給它一個預設值，通常是 null 或一個包含預期結構的空物件
const AuthContext = createContext(null);

// 2. useAuth Hook 讓子組件可以「消費」這些數據
export const useAuth = () => {
  // 檢查呼叫 useAuth 的組件是否被 AuthProvider 包裹
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth 必須在 AuthProvider 內部使用');
  }
  return context;
};

// 3. Auth Provider 組件 ( 負責 狀態 和 邏輯 的定義與維護 ) 
// children = 內容插槽 (Content Slot)。
export const AuthProvider = ({ children }) => {
  // 狀態
  const [isLoggedIn, setIsLoggedIn] = useState(false);  // 是否登入
  const [isLoading, setIsLoading] = useState(false);    // 載入狀態
  const [error, setError] = useState(null);             // 錯誤訊息狀態

  const [accessToken, setAccessToken] = useState(null);
  const [refreshToken, setRefreshToken] = useState(null);
  const [roleName, setRoleName] = useState(null);
  const [username, setUsername] = useState(null);
  const navigate = useNavigate();

  // 邏輯
  // 登入處理
  const handleLogin = useCallback(async (username, password) => {
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
      setIsLoggedIn(true);

      // 導航到 應用程式的主頁
      navigate("/");

    } catch (e) {
      // 處理網路錯誤或 API 服務拋出的錯誤
      console.error("登入時發生錯誤", e);
      setError(e.message || "網路或服務器連接錯誤");
    } finally {
      setIsLoading(false);
    }
  }, [navigate]);

  // 登出處理
  const handleLogout = useCallback(async () => {
    setIsLoading(true); // 開始載入
    setError(null);

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
      setIsLoggedIn(false);

      // 導航到 應用程式的主頁
      navigate("/");

      setIsLoading(false);
    }
  }, [accessToken, navigate]);

  // 註冊處理
  const handleRegister = useCallback(async (username, password, email, phoneToSubmit, roleNumber) => {
    setIsLoading(true); // 開始載入
    setError(null);

    try {
      // 呼叫 API 服務
      const response = await register(username, password, email, phoneToSubmit, roleNumber);

      // 註冊成功後的邏輯
      console.log(`註冊成功訊息: ${response.message}`);

      // 導航到 一個提示使用者去檢查信箱的頁面
      navigate("/register-success");
    } catch (e) {
      // 處理 API 服務拋出的錯誤
      console.error("註冊時發生錯誤", e);
      setError(e.message || "網路或服務器連接錯誤");

    } finally {
      setIsLoading(false); // 無論成功失敗，都關閉載入狀態
    }
  }, []);

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
          setIsLoggedIn(true);

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
              setIsLoggedIn(true);
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
  }, [
    handleLogout,
    navigate,
    setAccessToken,
    setRefreshToken,
    setRoleName,
    setUsername,
    setIsLoggedIn,
    setIsLoading,
  ]);

  // 4. 定義 Context 的值
  // 我們將所有狀態、Setter 和未來定義的 handle* 方法都放在這個物件裡
  const contextValue = {
    // 狀態 (供讀取)
    isLoggedIn,
    isLoading,
    error,
    accessToken,
    refreshToken,
    roleName,
    username,

    // 狀態 Setter (供內部或必要時供外部組件使用)
    setIsLoggedIn,
    setIsLoading,
    setError,
    setAccessToken,
    setRefreshToken,
    setRoleName,
    setUsername,

    // 業務邏輯方法 
    handleLogin,
    handleLogout,
    handleRegister,
  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};