import React, { useState } from "react";
import { useAuth } from "../context/AuthContext"
import "./Register.css";

function Register() {

  const { handleRegister, isLoading, error, setError } = useAuth();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [roleNumber, setRoleNumber] = useState(1);

  // 保持這個狀態專門用於「前端表單驗證」錯誤
  const [localErrorMessage, setLocalErrorMessage] = useState(null);

  // 專門處理電話號碼輸入和格式化 (XXXX-XXXXXX)
  const handlePhoneChange = (e) => {
    const rawValue = e.target.value.replace(/[^0-9]/g, ""); // 移除所有非數字字符
    let formatteredValue = "";
    if (rawValue.length > 4) {
      formatteredValue = `${rawValue.slice(0, 4)}-${rawValue.slice(4, 10)}`;
    } else {
      formatteredValue = rawValue;
    }
    setPhoneNumber(formatteredValue);
  }

  // 基本信箱驗證函式
  const isValidEmail = (email) => {
    // 簡易的信箱正則表達式，用於前端快速檢查
    return /\S+@\S+\.\S+/.test(email);
  }

  const handleSubmit = (e) => {
    e.preventDefault();

    // 1. 清除上次的前端錯誤
    setLocalErrorMessage(null);
    // 2. 清除 Context 裡的 API 錯誤（由 handleRegister 重新設置）
    setError(null);

    // 1. 前端信箱驗證
    if (!isValidEmail(email)) {
      setLocalErrorMessage("信箱格式不正確，請檢查");
      return;
    }

    // 2. 驗證通過，呼叫父組件傳入的註冊函數
    // 傳遞時將電話號碼中的分隔符 "-" 移除，以確保後端接收到純數字
    const phoneToSubmit = phoneNumber.replace(/-/g, '');

    handleRegister(username, password, email, phoneToSubmit, roleNumber);
  };

  // 決定要顯示哪個錯誤訊息：如果 localErrorMessage 有值就顯示，否則顯示 Context 的 error (API 錯誤)
  const displayError = localErrorMessage || error;

  return (
    <div className="register-page">
      <h2>註冊頁面</h2>

      {/* 統一顯示錯誤訊息 (localErrorMessage 或 Context error) */}
      {displayError && <div className="error-message">{displayError}</div>}

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
        <div className="form-group">
          <label htmlFor="email">信箱：</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="phoneNumber">電話：</label>
          <input
            type="tel"
            id="phoneNumber"
            value={phoneNumber}
            onChange={handlePhoneChange}
            maxLength={11} // 限制最大長度 (例如：09XX-XXXXXX 總共 11 個字符)
            required
          />
        </div>
        <button type="submit" className="register-button" disabled={isLoading}>
          {isLoading ? "註冊中..." : "註冊"}
        </button>
      </form>
    </div>
  )

}

export default Register;