import { request } from "./apiClient"

const BASE_SERVICE = "/auth";

/**
 * 登入
 * @param {string} username 用戶名
 * @param {string} password 密碼
 * @returns {Promise<Object>} message = "登陸成功"
 */
export const login = async (username, password) => {
  return request(`${BASE_SERVICE}/login`, "POST", { username, password });
};

/**
 * 登出
 * @returns {Promise<Object>} message = "登出成功，請清除客戶端 Token"
 */
export const logout = async () => {
  return request(`${BASE_SERVICE}/logout`, "GET", null, true);
};

/**
 * access token 刷新
 * @param {string} refreshToken 刷新 TOKEN
 * @returns {Promise<Object>} message = "Refresh token 更新成功"
 */
export const updateAccessToken = async (refreshToken) => {
  return request(`${BASE_SERVICE}/access/token/refresh`, "POST", { refreshToken });
};

/**
 * 修改前	確認密碼
 * @param {string} username 用戶名
 * @param {string} password 密碼
 * @returns {Promise<Object>} message = "密碼確認成功, 進入修改資料網頁"
 */
export const updateCheckPassword = async (username, password) => {
  return request(`${BASE_SERVICE}/check/password`, "POST", { username, password });
};

/**
 * 修改 民眾帳號前	確認密碼
 * @param {string} username 用戶名
 * @param {string} password 密碼
 * @returns {Promise<Object>} message = "密碼確認成功, 進入修改資料網頁"
 */
export const updatePublicCheckPassword = async (username, password) => {
  return request(`${BASE_SERVICE}/public/check/password`, "POST", { username, password }, true);
};