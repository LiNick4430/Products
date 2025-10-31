import React, { useEffect, useState, useRef } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import "./EmailVerifyPage.css"

// 假設 verifyAccount 來自您的服務層
import { verifyAccount } from '../services/emailService';

function EmailVerifyPage() {
  // 1. 呼叫 useSearchParams
  // searchParams 是一個 URLSearchParams 實例
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [status, setStatus] = useState('驗證中...'); // 顯示給使用者看的訊息
  const [isLoading, setIsLoading] = useState(true); // 載入狀態
  const [isSuccess, setIsSuccess] = useState(false); // 驗證是否成功

  const isVerifiedRef = useRef(false); // 使用 Ref 來追蹤是否已執行過

  useEffect(() => {
    // 2. 使用 .get('參數名稱') 方法來提取 token
    // 如果 URL 是 /email/verify?token=XYZ，這裡就會拿到 XYZ
    const token = searchParams.get('token');

    if (!token) {
      setStatus('錯誤：URL 中缺少驗證 Token。');
      setIsLoading(false); // 停止載入，顯示錯誤
      return;
    }

    if (isVerifiedRef.current) {
      // 如果 Ref 已經是 true，表示 API 已經呼叫過，直接退出第二次執行
      return;
    }

    // 確保每次運行時都設定為載入中和清除舊狀態
    setIsLoading(true);
    setStatus('帳號驗證中，請稍候...');

    const handleVerification = async () => {
      isVerifiedRef.current = true;

      try {
        // 執行 API 呼叫 (傳遞 token)
        await verifyAccount(token);

        // --- 成功邏輯 ---
        // 後端 JSON 結構: { code: 200, message: "帳號啟用成功", data: {...} }
        const message = '帳號啟用成功！';
        setStatus(message + ' 3 秒後將自動跳轉至登入頁面...');
        setIsSuccess(true);

        // 成功後延遲 3 秒跳轉
        setTimeout(() => {
          navigate('/login');
        }, 3000);

      } catch (error) {
        // --- 失敗邏輯 ---
        // 捕捉 API 錯誤（例如 Token 已失效、伺服器錯誤）
        const errorMessage = error.message || '驗證失敗，無法連線到伺服器。';
        setStatus('驗證失敗: ' + errorMessage);
        setIsSuccess(false);

      } finally {
        setIsLoading(false); // 無論成功失敗，都要停止載入
      }
    };

    handleVerification();

  }, [searchParams, navigate]);

  // --- 補充的 JSX 渲染邏輯 ---
  // 根據狀態來渲染畫面
  return (
    <div className="verify-container">
      <div className="verify-card">
        <h2>電子郵件驗證</h2>

        {isLoading && (
          <p className="loading-message">
            {status}
          </p>
        )}

        {!isLoading && (
          // 使用三元運算符動態添加成功或錯誤的 CSS Class
          <div className={isSuccess ? "status-box success-box" : "status-box error-box"}>
            <p className="status-message">{status}</p>
            {isSuccess && (
              <p className="link-text">若未自動跳轉，請點擊 <a href="/login" className="link">這裡</a></p>
            )}
          </div>
        )}
      </div>
    </div>
  );

}

export default EmailVerifyPage;