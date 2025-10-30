import { request } from "./apiClient"
// services/authService.js

const API_BASE_URL = "http://localhost:8080";

/**
 * 登入
 * @param {string} username 用戶名
 * @param {string} password 密碼
 * @returns {Promise<Object>} 包含登入結果的 API 回應
 */
export const login = async (username, password) => {
  return request("/auth/login", "POST", { username, password });
};

/**
 * 登出
 * @returns {Promise<Object>} 包含登出結果的 API 回應
 */
export const logout = async (accessToken) => {
  return request("/auth/logout", "GET", null, true);
};

/**
 * access token 刷新
 * @param {string} refreshToken 刷新 TOKEN
 * @returns {Promise<Object>} 包含新 TOKEN 的 API 回應
 */
export const updateAccessToken = async (refreshToken) => {
  return request("/auth/access/token/refresh", "POST", { refreshToken });
};