// src/hooks/usePublicInfoStatus.js
import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { getUserPublicDetails } from "../services/userPublicService"; // 引入 API 服務
import { toast } from 'react-toastify';

/**
 * 檢查 ROLE_PUBLIC 用戶的基本資料是否已設置。
 * 非 ROLE_PUBLIC 用戶或已設置的用戶，狀態視為 true。
 */
const usePublicInfoStatus = () => {
  const { accessToken, roleName } = useAuth();
  // null: 載入中; true: 已設置 (或非民眾); false: 尚未設置
  const [isPublicInfoSet, setIsPublicInfoSet] = useState(null);
  const [isLoadingStatus, setIsLoadingStatus] = useState(true);

  const [publicDetails, setPublicDetails] = useState(null);

  useEffect(() => {
    // 只有在登入且是民眾角色時才需要檢查
    if (roleName === "ROLE_PUBLIC" && accessToken) {
      setIsLoadingStatus(true);

      // 需要 定義一個內部 async 函數 才可以使用 await
      const checkStatus = async () => {
        try {
          const publicResponse = await getUserPublicDetails(accessToken);
          setPublicDetails(publicResponse.data); // 將資料儲存起來
          setIsPublicInfoSet(true);
        } catch (e) {
          // 捕獲 await 拋出的錯誤
          if (e.message && e.message.includes("錯誤類型：USER_NOT_FOUND")) {
            setPublicDetails(null); // 將 資料 設定為空
            setIsPublicInfoSet(false); // 尚未填寫
          } else {
            console.error("檢查民眾資料完整性時發生預期外錯誤:", e);
            toast.error("資料檢查失敗，請稍後再試。", { autoClose: 3000 });
            setPublicDetails(null); // 將 資料 設定為空
            setIsPublicInfoSet(true);
          }
        } finally {
          setIsLoadingStatus(false);
        }
      };

      checkStatus();

    } else {
      // 非民眾用戶，直接視為資料已設置
      setPublicDetails(null); // 非民眾時，民眾資料為空
      setIsPublicInfoSet(true);
      setIsLoadingStatus(false);
    }
  }, [accessToken, roleName]);

  return { isPublicInfoSet, isLoadingStatus, publicDetails };
};

export default usePublicInfoStatus;