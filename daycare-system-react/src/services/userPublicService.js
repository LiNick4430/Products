import { request } from "./apiClient"

// services/userService.js
const BASE_SERVICE = "/public/user";

/**
 * 民眾 取得 自己的資料
 * @returns {Promise<Object>} message = "找尋自己 成功"
 */
export const getUserPublicDetails = async () => {
  return request(`${BASE_SERVICE}/me`, "GET", null, true);
};

/**
 * 尋找	全部民眾帳號
 * @returns {Promise<Object>} message = "搜尋 全部民眾資料成功"
 */
export const findAllUserPublic = async () => {
  return request(`${BASE_SERVICE}/find/all`, "GET", null, true);
};

/**
 * 尋找	特定民眾帳號
 * @param {String} username - 目標帳號
 * @returns {Promise<Object>} message = "搜尋 民眾資料成功"
 */
export const findUserPublic = async (username) => {
  return request(`${BASE_SERVICE}/find`, "POST", { username }, true);
};

/**
 * 設定 民眾基本資料
 * @param {String} name - 民眾姓名
 * @param {String} nationalIdNo - 身分證字號
 * @param {String} birthdate - 生日(YYYY-MM-DD)
 * @param {String} registeredAddress - 戶籍地址
 * @param {String} mailingAddress - 通訊地址
 * @returns {Promise<Object>} message = "民眾資料 建立成功"
 */
export const setUserPublicInformation = async (name, nationalIdNo, birthdate, registeredAddress, mailingAddress) => {
  return request(`${BASE_SERVICE}/information`, "POST", { name, nationalIdNo, birthdate, registeredAddress, mailingAddress }, true);
};

/**
 * 更新 民眾基本資料
 * @param {String} newName - 新 民眾姓名
 * @param {String} newRegisteredAddress - 新 戶籍地址
 * @param {String} newMailingAddress - 新 通訊地址
 * @returns {Promise<Object>} message = "民眾資料 更新成功"
 */
export const updateUserPublic = async (newName, newRegisteredAddress, newMailingAddress) => {
  return request(`${BASE_SERVICE}/update`, "POST", { newName, newRegisteredAddress, newMailingAddress }, true);
};

/**
 * 刪除 民眾基本資料
 * @param {String} username - 帳號
 * @param {String} password - 密碼
 * @returns {Promise<Object>} message = "民眾帳號 刪除成功"
 */
export const deleteUserPublic = async (username, password) => {
  return request(`${BASE_SERVICE}/delete`, "DELETE", { username, password }, true);
};