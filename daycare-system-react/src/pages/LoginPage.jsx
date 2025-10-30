import React, { useState } from "react";
import "./LoginPage.css";

function LoginPage({ onLogin, isLoggedIn, isLoading, error }) {
  const [username, setUsername] = useState("manager");
  const [password, setPassword] = useState("123456");

  const handleSubmit = (e) => {
    e.preventDefault();
    onLogin(username, password); // 呼叫 onLogin 進行登入驗證
  };

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
            <button type="submit" className="login-button" disabled={isLoading}>
              {isLoading ? "登入中..." : "登入"}
            </button>
          </form>
        </>
      )}

    </div>
  );
}

export default LoginPage;