import React, { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext"
import { useNavigate } from 'react-router-dom';

import { getUserDetails } from "../services/userService"
import { getUserPublicDetails } from "../services/userPublicService"
import { getUserAdminDetails } from "../services/userAdminService"

import usePublicInfoStatus from '../hooks/usePublicInfoStatus'; // 引入 Hook

import "./UserMe.css";

function UserMe() {
  // 只取出 不變動的 Context 值
  const { accessToken, roleName, username } = useAuth();
  const navigate = useNavigate(); // 2. 實例化 useNavigate

  // 獨立的本地載入狀態和錯誤狀態
  const [isFetchingData, setIsFetchingData] = useState(true); // 初始為 true
  const [fetchError, setFetchError] = useState(null);

  // 用於儲存各級別資料的 State
  const [userData, setUserData] = useState(null);
  // const [publicData, setPublicData] = useState(null);
  const [adminData, setAdminData] = useState(null);

  // 🎯 使用 Hook 獲取民眾相關狀態和資料 🎯
  const {
    isPublicInfoSet, // Hook 已經決定是否填寫
    isLoadingStatus, // Hook 的載入狀態
    publicDetails // Hook 成功取得的民眾資料
  } = usePublicInfoStatus();

  // 處理民眾帳號是否已設置基本資料的狀態
  // 將 isPublicInfoSet 狀態的來源改為 Hook 
  // const [isPublicInfoSet, setIsPublicInfoSet] = useState(isPublicInfoSetFromHook);

  useEffect(() => {
    // 檢查是否有 access token，如果沒有，理論上 ProtectedRoute 會先攔截
    if (!accessToken || !roleName) return;

    // 只有當 Hook 已經檢查完畢，我們才能開始載入其他資料
    if (isLoadingStatus) return;

    const fetchUserDetails = async () => {
      setIsFetchingData(true);
      setFetchError(null);

      // 1. 載入通用的基本資料
      try {
        const commonResponse = await getUserDetails(accessToken);
        setUserData(commonResponse.data);
      } catch (e) {
        console.error("載入通用資料失敗:", e);
        setFetchError(e.message);
        setIsFetchingData(false);
        return; // 終止進一步載入
      }

      // 2. 依照角色 載入資料 (ROLE_PUBLIC 資料已在 Hook 中處理)
      try {
        if (roleName === "ROLE_STAFF" || roleName === "ROLE_MANAGER") {
          const adminResponse = await getUserAdminDetails(accessToken);
          setAdminData(adminResponse.data);
        }
      } catch (e) {
        // 這裡只處理 Admin/Staff 資料載入失敗的情況
        console.error(`載入 ${roleName} 特定資料失敗:`, e);
        setFetchError(e.message);
      } finally {
        setIsFetchingData(false);
      }
    }

    fetchUserDetails();
  }, [accessToken, roleName, isLoadingStatus]);

  // 1. 載入或錯誤提示
  if (isFetchingData) return <div>正在載入您的帳號資料...</div>;
  if (fetchError) return <div className="error">載入失敗: {fetchError}</div>;

  // 2. 民眾帳號的特殊處理 (引導至填寫頁面)
  if (roleName === "ROLE_PUBLIC" && isPublicInfoSet === false) {
    return (
      <div className="user-me-container">
        <h2>👋 {username} 您好，請補齊資料</h2>
        <p>這是您第一次登入或您尚未填寫完整的民眾基本資料。</p>
        <button
          className="btn-primary"
          onClick={() => navigate("/public/user/information")}
        >
          前往填寫基本資料
        </button>
      </div>
    );
  }

  // 3. 一般資料顯示 (通用和角色特定資料)
  return (
    <div className="user-me-container">
      <h2>基本資訊</h2>
      <p><strong>帳號：</strong> {userData?.username || username}</p>
      <p><strong>權限：</strong> {roleName}</p>
      <p><strong>信箱：</strong> {userData.email}</p>
      <p><strong>電話：</strong> {userData.phoneNumber}</p>

      {/* 顯示民眾特定資料 */}
      {/* 使用 Hook 提供的 publicDetails */}
      {publicDetails && (
        <div className="role-details">
          <h2>詳細資料</h2>
          <p>姓名: {publicDetails.name}</p>
          <p>身分證字號: {publicDetails.nationalIdNo}</p>
          <p>生日: {publicDetails.birthdate}</p>
          <p>戶籍地址: {publicDetails.registeredAddress}</p>
          <p>通訊地址: {publicDetails.mailingAddress}</p>
        </div>
      )}

      {/* 顯示員工/管理層特定資料 */}
      {adminData && (
        <div className="role-details">
          <h2>詳細資料</h2>
          <p>姓名: {adminData.name}</p>
          <p>職稱: {adminData.jobTitle}</p>
          <p>機構: {adminData.organizationName}</p>
        </div>
      )}

      {/* 4. 通用操作按鈕 */}
      <div className="action-buttons">
        <button className="btn-secondary" onClick={() => navigate("/user/update/verify")}>
          更新資料
        </button>
        <button className="btn-danger" onClick={() => navigate("/user/delete")}>
          刪除帳號
        </button>
      </div>
    </div>
  );
}

export default UserMe;