import { request } from "./apiClient"

const BASE_SERVICE = "/user";

/**
 * 獲取 帳號 基本資料
 * @returns {Promise<Object>} message = "獲取登入資料 成功"
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
 * @returns {Promise<Object>} message = "帳號建立成功, 請驗證信箱"
 */
export const register = async (username, password, email, phoneNumber, roleNumber) => {
  return request(`${BASE_SERVICE}/register`, "POST", { username, password, email, phoneNumber, roleNumber });
};

/**
 * 忘記密碼 設定新的密碼
 * @param {String} username - 帳號
 * @param {String} password - 新密碼
 * @param {string} token - 認證用 TOKEN
 * @returns {Promise<Object>} message = "密碼更新完成, 請使用新密碼登入"
 */
export const resetPassword = async (username, password, token) => {
  return request(`${BASE_SERVICE}/reset/password`, "POST", { username, password, token });
};

/**
 * 更新 帳號資料
 * @param {String} newPassword - 新密碼
 * @param {String} newPhoneNumber - 新電話
 * @returns {Promise<Object>} message = "資料更新完成 請重新登入"
 */
export const UserUpdate = async (newPassword, newPhoneNumber) => {
  return request(`${BASE_SERVICE}/update`, "POST", { newPassword, newPhoneNumber }, true);
};

/**
 * 刪除 帳號資料
 * @param {String} password - 確認密碼
 * @returns {Promise<Object>} message = "帳號刪除成功"
 */
export const UserDelete = async (password) => {
  return request(`${BASE_SERVICE}/delete`, "DELETE", { password }, true);
};