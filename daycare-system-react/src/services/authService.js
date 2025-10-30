import { request } from "./apiClient"

// services/authService.js
const BASE_SERVICE = "/auth";

/**
 * 登入
 * @param {string} username 用戶名
 * @param {string} password 密碼
 * @returns {Promise<Object>} 包含登入結果的 API 回應
 */
export const login = async (username, password) => {
  return request(`${BASE_SERVICE}/login`, "POST", { username, password });
};

/**
 * 登出
 * @returns {Promise<Object>} 包含登出結果的 API 回應
 */
export const logout = async () => {
  return request(`${BASE_SERVICE}/logout`, "GET", null, true);
};

/**
 * access token 刷新
 * @param {string} refreshToken 刷新 TOKEN
 * @returns {Promise<Object>} 包含新 TOKEN 的 API 回應
 */
export const updateAccessToken = async (refreshToken) => {
  return request(`${BASE_SERVICE}/access/token/refresh`, "POST", { refreshToken });
};