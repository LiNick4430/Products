import React, { useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useAuth } from '../context/AuthContext';
import usePublicInfoStatus from '../hooks/usePublicInfoStatus'; // 引入 Hook

function UserPublicCaseInformation() {
  const navigate = useNavigate();
  const { roleName } = useAuth();
  // 使用 Hook 來獲取民眾資料狀態
  const { isPublicInfoSet, isLoadingStatus } = usePublicInfoStatus();

  const toastShownRef = useRef(false);

  // 1. 處理狀態載入
  if (isLoadingStatus) {
    return <div>正在檢查資料完整性...</div>;
  }

  // 2. 檢查民眾權限和資料完整性
  // 只有當用戶是 ROLE_PUBLIC 且 isPublicInfoSet 為 false 時才導航
  if (roleName === "ROLE_PUBLIC" && isPublicInfoSet === false) {

    // 避免重複觸發
    if (!toastShownRef.current) {
      // 提示使用者需要先填寫資料
      toast.warn("請先補齊基本資料才能訪問此頁面。", { position: "top-center", autoClose: 3000 });

      toastShownRef.current = true;
    }
    // 強制導航到 /user/me，由 UserMe 頁面渲染引導畫面
    navigate("/user/me", { replace: true });

    // 返回 null 避免頁面內容閃爍
    return null;
  }

  // 3. 通過檢查，渲染頁面內容
  return (
    <div>
      <h1>民眾案件管理</h1>
      <p>這是只有資料填寫完整的民眾才能看到的內容。</p>
      {/* 頁面主要內容 */}
    </div>
  );
}

export default UserPublicCaseInformation;