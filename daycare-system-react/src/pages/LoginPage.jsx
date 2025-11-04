import React, { useState } from "react";
import { useAuth } from "../context/AuthContext"
import { useNavigate } from 'react-router-dom';
import "./LoginPage.css";

function LoginPage() {

  const { handleLogin, isLoading, error, isLoggedIn } = useAuth();

  const [username, setUsername] = useState("manager");
  const [password, setPassword] = useState("123456");

  const navigate = useNavigate(); // 2. 實例化 useNavigate

  const handleSubmit = (e) => {
    e.preventDefault();
    handleLogin(username, password); // 呼叫 onLogin 進行登入驗證
  };

  const handleForgetPasswordClick = (e) => {
    e.preventDefault(); // 防止按鈕觸發表單提交
    navigate("/forget/password"); // 3. 點擊時跳轉到忘記密碼頁面
  }

  return (
    <div className="login-page">
      {isLoggedIn ? (
        <>
          <h2>登入成功</h2>
        </>
      ) : (
        <>
          <h2>登入</h2>
          {/* 顯示錯誤訊息 效果是 當 error=true 則 顯示 右邊的 效果 */}
          {error && <div className="error-message">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="username">帳號：</label>
              <input
                type="text"
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="password">密碼：</label>
              <input
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <div className="button-group">
              <button type="submit" className="login-button" disabled={isLoading}>
                {isLoading ? "登入中..." : "登入"}
              </button>
              <button type="button" className="forgot-password-button" onClick={handleForgetPasswordClick}>
                忘記密碼
              </button>
            </div>
          </form>
        </>
      )}

    </div>
  );
}

export default LoginPage;