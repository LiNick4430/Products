import React, { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext"
import { useSearchParams, useNavigate } from "react-router-dom";
import "./UserForgetPassword.css";

function ForgetPassword() {
  // 獲取 URL 參數 (例如：?username=testuser&token=xyz123)
  const [searchParams] = useSearchParams();

  // 獲取 AuthContext 方法和狀態
  const {
    handleForgetPassword,
    handleForgetPasswordResetPassword,
    isLoading,
    error
  } = useAuth();

  // 狀態
  const [username, setUsername] = useState("");
  const [newPassword, setNewPassword] = useState("");

  // 從 URL 讀取 token 和帳號，如果存在，則進入重設階段
  const tokenFromUrl = searchParams.get('token');
  const usernameFromUrl = searchParams.get('username');

  // 判斷目前階段：如果有 Token，則直接進入重設階段 (Step 2)
  const isResetPhase = !!tokenFromUrl && !!usernameFromUrl;

  useEffect(() => {
    // 如果是重設階段，自動填入 URL 中的帳號
    if (isResetPhase) {
      setUsername(usernameFromUrl);
    }
  }, [isResetPhase, usernameFromUrl]);

  // 步驟 1: 提交帳號 (發送郵件)
  const handleSubmitUsername = async (e) => {
    e.preventDefault();
    await handleForgetPassword(username);
  };

  // 步驟 2: 提交新密碼和 Token
  const handleSubmitResetPassword = async (e) => {
    e.preventDefault();
    await handleForgetPasswordResetPassword(usernameFromUrl, newPassword, tokenFromUrl);
  };

  // --- 渲染 Step 1: 輸入帳號 ---
  const renderStep1Form = () => (
    <form onSubmit={handleSubmitUsername}>
      <h2>忘記密碼 (發送郵件)</h2>
      {error && <div className="error-message">{error}</div>}
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
      <button type="submit" className="submit-button" disabled={isLoading}>
        {isLoading ? "發送中..." : "發送重設郵件"}
      </button>
    </form>
  );

  // --- 渲染 Step 2: 只輸入新密碼 (Token 和帳號從 URL 取得) ---
  const renderStep2Form = () => (
    <form onSubmit={handleSubmitResetPassword}>
      <h2>重設密碼</h2>
      {/* 💡 顯示 Token 已認證的提示 */}
      <p className="success-message">帳號 **{usernameFromUrl}** 已認證，請設定新密碼。</p>
      {error && <div className="error-message">{error}</div>}

      <div className="form-group">
        <label htmlFor="newPassword">新密碼：</label>
        <input
          type="password"
          id="newPassword"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          required
        />
      </div>
      <button type="submit" className="submit-button" disabled={isLoading}>
        {isLoading ? "重設中..." : "確認重設密碼"}
      </button>
    </form>
  );

  return (
    <div className="forgetPassword-page">
      {isResetPhase ? renderStep2Form() : renderStep1Form()}
    </div>
  );
}

export default ForgetPassword;