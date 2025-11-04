import { request } from "./apiClient"

// services/userService.js
const BASE_SERVICE = "/email";

// 創建帳號 使用
/**
 * 信箱認證用
 * @param {string} token - 從 URL 獲取的驗證 token
 * @returns {Promise<Object>} message = "帳號啟用成功"
 */
export const verifyAccount = async (token) => {
  return request(`${BASE_SERVICE}/verify?token=${token}`);
};

// 忘記密碼 使用
/**
 * 忘記密碼 輸入帳號後 發送信件
 * @param {string} username - 忘記密碼 的 帳號
 * @returns {Promise<Object>} message = "驗證信寄出成功"
 */
export const forgetPasswordSendEmail = async (username) => {
  return request(`${BASE_SERVICE}/send/password`, "POST", { username });
};

/**
 * 忘記密碼 認證信件
 * @param {string} token - 從 URL 獲取的驗證 token
 * @returns {Promise<Object>} message = "驗證成功, 進入修改密碼網頁"
 */
export const forgetPasswordVerifty = async (token) => {
  return request(`${BASE_SERVICE}/reset/password?token=${token}`);
};