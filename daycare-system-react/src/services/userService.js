import { request } from "./apiClient"

// services/userService.js
const BASE_SERVICE = "/user";

/**
 * 獲取 帳號 基本資料
 * @returns {Promise<Object>} 包含新 TOKEN 的 API 回應
 */
export const getUserDetails = async () => {
  return request(`${BASE_SERVICE}/me`, "GET", null, true);
};

/**
 * 申請 新的 帳號
 * @param {String} username - 帳號
 * @param {String} password - 密碼
 * @param {String} email - 信箱
 * @param {String} phoneNumber - 電話
 * @param {Number} roleNumber - 角色編號
 * @returns {Promise<Object>} 包含帳號 等資料 的 API 回應 (不包含密碼)
 */
export const register = async (username, password, email, phoneNumber, roleNumber) => {
  return request(`${BASE_SERVICE}/register`, "POST", { username, password, email, phoneNumber, roleNumber });
};

