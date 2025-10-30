import { request } from "./apiClient"
// services/userService.js

/**
 * 獲取 帳號 基本資料
 * @returns {Promise<Object>} 包含新 TOKEN 的 API 回應
 */
export const getUserDetails = async (accessToken) => {
  return request("/user/me", "GET", null, true);
};