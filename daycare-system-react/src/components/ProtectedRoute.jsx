import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; // 引入您的 useAuth Hook

// 該組件接收它應該渲染的實際頁面組件作為 'element'
function ProtectedRoute({ element: Component, requiredRoles, ...rest }) {
  // 從 AuthContext 獲取登入狀態和載入狀態
  const { isLoggedIn, isLoading, roleName } = useAuth();

  // 1. 處理載入狀態：當 App 正在檢查本地 Token 時
  if (isLoading) {
    // 顯示一個載入畫面，避免閃爍或快速跳轉
    return <div>載入中...</div>;
  }

  // 2. 檢查登入狀態
  if (!isLoggedIn) {
    // 未登入 -> 導向登入頁面
    toast.error("請先登入以訪問此頁面。", { position: "top-center", autoClose: 3000 });
    return <Navigate to="/login" replace />;
  }

  // 3. 檢查角色權限 (只有當 requiredRoles 存在時才檢查)
  if (requiredRoles && Array.isArray(requiredRoles) && requiredRoles.length > 0) {

    // 將 roleName 轉換為陣列以便檢查 (因為 roleName 可能是單一字串)
    const userRoles = [roleName];

    // 檢查用戶是否擁有任一所需角色
    const hasPermission = userRoles.some(role => requiredRoles.includes(role));

    if (!hasPermission) {
      // 權限不足 -> 導向首頁或顯示錯誤
      toast.error("權限不足，無法訪問此頁面。", { position: "top-center", autoClose: 3000 });
      return <Navigate to="/" replace />;
    }
  }

  // 4. 登入且權限檢查通過 -> 渲染目標組件
  return <Component {...rest} />;
}

export default ProtectedRoute;