import { request } from "./apiClient"

// services/userService.js
const BASE_SERVICE = "/email";

/**
 * 信箱認證用
 * @param {string} token - 從 URL 獲取的驗證 token
 * @returns {Promise<Object>} 
 */
export const verifyAccount = async (token) => {
  return request(`${BASE_SERVICE}/verify?token=${token}`);
};