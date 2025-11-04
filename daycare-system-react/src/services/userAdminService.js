import { request } from "./apiClient"

// services/userService.js
const BASE_SERVICE = "/admin/user";

/**
 * 主管/員工 取得 自己的資料
 * @returns {Promise<Object>} message = "找尋自己 成功"
 */
export const getUserAdminDetails = async () => {
  return request(`${BASE_SERVICE}/me`, "GET", null, true);
};

/**
 * 主管 尋找 全部員工帳號
 * @returns {Promise<Object>} message = "尋找 全部員工帳號 成功"
 */
export const findAllUserPublic = async () => {
  return request(`${BASE_SERVICE}/find/all`, "GET", null, true);
};

/**
 * 主管 尋找 特定員工帳號
 * @param {String} username - 目標帳號
 * @returns {Promise<Object>} message = "尋找 特定員工帳號 成功"
 */
export const findUserPublic = async (username) => {
  return request(`${BASE_SERVICE}/find`, "POST", { username }, true);
};

/**
 * 主管 設定 員工基本資料
 * @param {String} username - 帳號
 * @param {String} password - 信箱
 * @param {String} email - 電子信箱
 * @param {String} phoneNumber - 電話號碼
 * @param {Number} roleNumber - 角色號碼
 * @param {String} name - 員工姓名
 * @param {String} jobTitle - 員工職稱
 * @param {Number} organizationId - 所屬機構 ID
 * @returns {Promise<Object>} message = "設定 員工基本資料 成功, 請通知員工 進入信箱 啟動帳號"
 */
export const createUserAdmin = async (username, password, email, phoneNumber, roleNumber, name, jobTitle, organizationId) => {
  return request(`${BASE_SERVICE}/create`, "POST", { username, password, email, phoneNumber, roleNumber, name, jobTitle, organizationId }, true);
};

/**
 * 主管 更新 員工基本資料	
 * @param {String} username - 被更新的 帳號目標
 * @param {Number} newRoleNumber - 新角色ID
 * @param {String} newName - 新 姓名
 * @param {String} newJobTitle - 新 職稱
 * @param {Number} newOrganizationId - 新 機構 ID
 * @returns {Promise<Object>} message = "設定 員工基本資料 成功"
 */
export const updateUserAdmin = async (username, newRoleNumber, newName, newJobTitle, newOrganizationId) => {
  return request(`${BASE_SERVICE}/update`, "POST", { username, newRoleNumber, newName, newJobTitle, newOrganizationId }, true);
};

/**
 * 主管 刪除 特定員工帳號
 * @param {String} username - 目標 帳號
 * @returns {Promise<Object>} message = "刪除 特定員工帳號 成功"
 */
export const deleteUserAdmin = async (username) => {
  return request(`${BASE_SERVICE}/delete`, "DELETE", { username }, true);
};