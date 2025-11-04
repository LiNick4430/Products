import { useState, useCallback, useEffect, createContext, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify'

import { login, logout, updateAccessToken } from "../services/authService"
import { getUserDetails, register, resetPassword } from "../services/userService"
import { forgetPasswordSendEmail } from "../services/emailService"

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
  // ----- 定義狀態 -----
  const [isLoggedIn, setIsLoggedIn] = useState(false);  // 是否登入
  const [isLoading, setIsLoading] = useState(false);    // 載入狀態
  const [error, setError] = useState(null);             // 錯誤訊息狀態

  const [accessToken, setAccessToken] = useState(null);
  const [refreshToken, setRefreshToken] = useState(null);
  const [roleName, setRoleName] = useState(null);
  const [username, setUsername] = useState(null);
  const navigate = useNavigate();

  // ----- 邏輯方法 -----
  // 登入處理 Callback
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

      // 登入成功後的邏輯
      console.log(`登入成功訊息: ${response.message}`);

      // 登入成功提示
      toast.success(`登入成功，歡迎回來`, {
        position: "top-center",   // 定義位置
        autoClose: 3000,          // 3秒後 自動關閉
      });

      // 導航到 應用程式的主頁
      navigate("/");

    } catch (e) {
      // 處理網路錯誤或 API 服務拋出的錯誤
      console.error("登入時發生錯誤", e);
      setError(e.message || "網路或服務器連接錯誤");

      toast.error(`登入失敗: ${e.message || "網路錯誤"}`, {
        position: "top-center",   // 定義位置
        autoClose: 3000,          // 3秒後 自動關閉
      });

    } finally {
      setIsLoading(false);
    }
  }, [navigate]);

  // 登出處理 Callback
  const handleLogout = useCallback(async () => {
    setIsLoading(true); // 開始載入
    setError(null);

    try {
      // 呼叫 API 服務
      const response = await logout(accessToken);

      // 登出成功後的邏輯
      console.log(`登出成功訊息: ${response.message}`);
    } catch (e) {
      // 僅記錄 API 錯誤，因為無論如何都要清除本地狀態
      console.error("API 登出時發生錯誤，但仍清除本地狀態。", e);
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

      // 登出成功提示
      toast.success(`登出成功`, {
        position: "top-center",   // 定義位置
        autoClose: 3000,          // 3秒後 自動關閉
      });

      // 導航到 應用程式的主頁
      navigate("/");

      setIsLoading(false);
    }
  }, [accessToken, navigate]);

  // 註冊處理 Callback
  const handleRegister = useCallback(async (username, password, email, phoneToSubmit, roleNumber) => {
    setIsLoading(true); // 開始載入
    setError(null);

    try {
      // 呼叫 API 服務
      const response = await register(username, password, email, phoneToSubmit, roleNumber);

      // 註冊成功後的邏輯
      console.log(`註冊成功訊息: ${response.message}`);

      // 註冊成功提示
      toast.success("註冊成功，將轉導到新頁面", {
        position: "top-center",
        autoClose: 3000,
      });

      // 導航到 一個提示使用者去檢查信箱的頁面
      navigate("/register/success");
    } catch (e) {
      // 處理 API 服務拋出的錯誤
      console.error("註冊時發生錯誤", e);
      setError(e.message || "網路或服務器連接錯誤");

      // 註冊失敗提示
      toast.error(`註冊失敗: ${e.message || "網路錯誤"}`, {
        position: "top-center",
        autoClose: 3000,
      });

    } finally {
      setIsLoading(false); // 無論成功失敗，都關閉載入狀態
    }
  }, [navigate]);

  // Token 檢查與刷新邏輯 Callback
  const checkAndSetAuth = useCallback(async () => {
    // 1. 嘗試從 localStorage 讀取 TOKEN
    const storeAccessToken = localStorage.getItem("accessToken");
    const storeRefreshToken = localStorage.getItem("refreshToken");
    const storeRoleName = localStorage.getItem("roleName");
    const storeUsername = localStorage.getItem("username");

    // 如果缺少任一 token 或資料，直接返回
    if (!storeAccessToken || !storeRefreshToken || !storeRoleName || !storeUsername) {
      return;
    }

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
          const newTokens = await updateAccessToken(storeRefreshToken); // 呼叫 更新 ACCESS TOKEN 服務

          // 5. 刷新成功的場合
          setAccessToken(newTokens.data.accessToken);
          setRefreshToken(newTokens.data.refreshToken);
          setRoleName(newTokens.data.roleName);
          setUsername(newTokens.data.username);
          setIsLoggedIn(true);

          // 記得更新 localStorage
          localStorage.setItem("accessToken", newTokens.data.accessToken);
          localStorage.setItem("refreshToken", newTokens.data.refreshToken);
          localStorage.setItem("roleName", newTokens.data.roleName);
          localStorage.setItem("username", newTokens.data.username);

          console.log("Token 刷新成功，恢復登入狀態。")
        } catch (errorToken) {
          // 6. 刷新失敗的場合 (Refresh Token 也失效)
          console.log("Token 刷新失敗，需要重新登入。", errorToken.message);

          // 登入過期 提示
          toast.error(`登入已過期，請重新登入`, {
            position: "top-center",   // 定義位置
            autoClose: 3000,          // 3秒後 自動關閉
          });

          // 執行登出邏輯 (清除所有 Token 和狀態)
          handleLogout(); // 註意：這裡呼叫的是上面定義的 handleLogout
        }
      } else {
        // 7. 處理其他錯誤 (e.g., 無效的 Token, 網路錯誤)
        console.log("驗證 Access Token 遇到其他錯誤，執行登出。", e.message);

        // 登入過期 提示
        toast.error(`登入已過期，請重新登入`, {
          position: "top-center",   // 定義位置
          autoClose: 3000,          // 3秒後 自動關閉
        });

        // 執行登出邏輯 (清除所有 Token 和狀態)
        handleLogout();
      }
    } finally {
      setIsLoading(false);
    }

  }, [handleLogout])

  // 忘記密碼 發送信件
  const handleForgetPassword = useCallback(async (username) => {
    setIsLoading(true);
    setError(null);

    try {
      // 執行 發信動作
      const response = await forgetPasswordSendEmail(username);

      // 如果發信成功
      console.log(`發信成功: ${response.message}`);

      toast.success(`發信成功 請到信箱收取`, {
        position: "top-center",   // 定義位置
        autoClose: 3000,          // 3秒後 自動關閉
      });

      // 跳回 登入頁面
      navigate("/login");

    } catch (e) {
      // 處理 API 服務拋出的錯誤
      console.error("發信時發生錯誤", e);
      setError(e.message || "網路或服務器連接錯誤");

      // 發信失敗提示
      toast.error(`發信失敗: ${e.message || "網路錯誤"}`, {
        position: "top-center",
        autoClose: 3000,
      });
    } finally {
      setIsLoading(false);
    }
  }, [navigate]);

  // 忘記密碼 修改密碼
  const handleForgetPasswordResetPassword = useCallback(async (username, password, token) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await resetPassword(username, password, token);

      // 如果密碼修改 成功
      console.log(`密碼修改成功: ${response.message}`);

      toast.success(`密碼修改成功 請到登陸頁面重新登入`, {
        position: "top-center",   // 定義位置
        autoClose: 3000,          // 3秒後 自動關閉
      });

      // 跳回 登入頁面
      navigate("/login");
    } catch (e) {
      // 處理 API 服務拋出的錯誤
      console.error("修改時發生錯誤", e);
      setError(e.message || "網路或服務器連接錯誤");

      // 發信失敗提示
      toast.error(`修改失敗: ${e.message || "網路錯誤"}`, {
        position: "top-center",
        autoClose: 3000,
      });
    } finally {
      setIsLoading(false);
    }
  }, [navigate]);

  // 第一次進入頁面 的 預先處理
  useEffect(() => {
    // 1. 嘗試從 localStorage 讀取 TOKEN
    const storeAccessToken = localStorage.getItem("accessToken");
    const storeRefreshToken = localStorage.getItem("refreshToken");
    const storeRoleName = localStorage.getItem("roleName");
    const storeUsername = localStorage.getItem("username");

    // 2. 如果存在所有必要的 Token 和資料
    if (storeAccessToken && storeRefreshToken && storeRoleName && storeUsername) {
      // 3. 執行獨立的驗證和刷新邏輯
      checkAndSetAuth();
    }

  }, [checkAndSetAuth]);

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
    handleForgetPassword,
    handleForgetPasswordResetPassword,

  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};