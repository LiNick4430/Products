import React, { useState, useCallback } from "react";
import { useAuth } from "../context/AuthContext"
import { useNavigate } from 'react-router-dom';
import { toast } from "react-toastify";
import "./UserPublicInformation.css";

import { setUserPublicInformation } from "../services/userPublicService"

function UserPublicInformation() {

  // 讀取 不變的資料 
  const { accessToken } = useAuth();

  // 獨立的本地載入狀態和錯誤狀態
  const [isFetchingData, setIsFetchingData] = useState(false); // 初始為 false
  const [fetchError, setFetchError] = useState(null);
  const navigate = useNavigate();

  // 基本資料
  const [name, setName] = useState();
  const [nationalIdNo, setNationalIdNo] = useState();
  const [birthdate, setBirthdate] = useState();
  const [registeredAddress, setRegisteredAddress] = useState();
  const [mailingAddress, setMailingAddress] = useState();

  // 輔助 State：將戶籍地址設為通訊地址 (方便操作)
  const [isSameAddress, setIsSameAddress] = useState(false);

  // 處理表單提交
  const handleSubmit = (e) => {
    e.preventDefault();
    // 在這裡可以加入簡單的前端驗證，例如檢查欄位是否為空

    const finalMailingAddress = isSameAddress ? registeredAddress : mailingAddress;

    handleSetPublicInformation(
      name,
      nationalIdNo,
      birthdate,
      registeredAddress,
      finalMailingAddress,
      accessToken
    );
  }

  const handleSetPublicInformation = useCallback(async (name, nationalIdNo, birthdate, registeredAddress, mailingAddress, accessToken) => {
    setIsFetchingData(true);
    setFetchError(null);

    try {
      const response = await setUserPublicInformation(name, nationalIdNo, birthdate, registeredAddress, mailingAddress, accessToken);

      // 註冊成功後的邏輯
      console.log(`註冊成功訊息: ${response.message}`);

      // 登入成功提示
      toast.success(`註冊成功`, {
        position: "top-center",   // 定義位置
        autoClose: 3000,          // 3秒後 自動關閉
      });

      // 導航到 
      navigate("/user/me");

    } catch (e) {
      // 處理網路錯誤或 API 服務拋出的錯誤
      console.error("註冊時發生錯誤", e);
      setFetchError(e.message || "網路或服務器連接錯誤");

      toast.error(`註冊失敗: ${e.message || "網路錯誤"}`, {
        position: "top-center",   // 定義位置
        autoClose: 3000,          // 3秒後 自動關閉
      });

    } finally {
      setIsFetchingData(false);
    }

  }, [accessToken, navigate])

  return (
    <div className="public-info-container">
      <h2>民眾基本資料填寫</h2>
      <p className="subtitle">請填寫您的國民身份證資訊、生日及地址以啟用所有服務。</p>

      {/* 錯誤提示 */}
      {fetchError && <div className="error-message">{fetchError}</div>}

      <form onSubmit={handleSubmit} className="public-info-form">
        {/* 姓名 */}
        <div className="form-group">
          <label htmlFor="name">姓名</label>
          <input
            id="name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            disabled={isFetchingData}
            required
          />
        </div>

        {/* 國民身分證字號 */}
        <div className="form-group">
          <label htmlFor="nationalIdNo">國民身分證字號</label>
          <input
            id="nationalIdNo"
            type="text"
            value={nationalIdNo}
            onChange={(e) => setNationalIdNo(e.target.value.toUpperCase())} // 轉大寫
            disabled={isFetchingData}
            required
          />
        </div>

        {/* 生日 */}
        <div className="form-group">
          <label htmlFor="birthdate">生日 (YYYY-MM-DD)</label>
          <input
            id="birthdate"
            type="date" // 使用 date 類型
            value={birthdate}
            onChange={(e) => setBirthdate(e.target.value)}
            disabled={isFetchingData}
            required
          />
        </div>

        {/* 戶籍地址 */}
        <div className="form-group">
          <label htmlFor="registeredAddress">戶籍地址</label>
          <input
            id="registeredAddress"
            type="text"
            value={registeredAddress}
            onChange={(e) => setRegisteredAddress(e.target.value)}
            disabled={isFetchingData}
            required
          />
        </div>

        {/* 通訊地址 Checkbox */}
        <div className="form-group checkbox-group">
          <input
            id="sameAddress"
            type="checkbox"
            checked={isSameAddress}
            onChange={(e) => setIsSameAddress(e.target.checked)}
            disabled={isFetchingData}
          />
          <label htmlFor="sameAddress">通訊地址與戶籍地址相同</label>
        </div>

        {/* 通訊地址 輸入框 (如果地址不相同則顯示) */}
        {!isSameAddress && (
          <div className="form-group">
            <label htmlFor="mailingAddress">通訊地址</label>
            <input
              id="mailingAddress"
              type="text"
              value={mailingAddress}
              onChange={(e) => setMailingAddress(e.target.value)}
              disabled={isFetchingData}
              required={!isSameAddress} // 如果不勾選相同，則必填
            />
          </div>
        )}

        {/* 提交按鈕 */}
        <button
          type="submit"
          className="btn-primary"
          disabled={isFetchingData}
        >
          {isFetchingData ? "資料送出中..." : "確認填寫並啟用帳號"}
        </button>
      </form>
    </div>
  );
}

export default UserPublicInformation;