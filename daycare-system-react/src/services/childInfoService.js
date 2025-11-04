import { request } from "./apiClient"

// services/userService.js
const BASE_SERVICE = "/child";

/**
 * 尋找 民眾底下 全部幼兒資料
 * @returns {Promise<Object>} message = "尋找 全部嬰兒資料 成功"
 */
export const findAllChild = async () => {
  return request(`${BASE_SERVICE}/find/all`, "GET", null, true);
};

/**
 * 尋找 民眾底下 特定幼兒資料
 * @param {Number} id - 目標 幼兒 ID
 * @returns {Promise<Object>} message = "尋找 特定幼兒資料 成功"
 */
export const findChild = async (id) => {
  return request(`${BASE_SERVICE}/find`, "POST", { id }, true);
};

/**
 * 設定 民眾底下 新幼兒資料
 * @param {String} name - 幼兒姓名
 * @param {String} nationalIdNo - 身分證字號
 * @param {String} birthdate - 生日(YYYY-MM-DD)
 * @param {String} gender - 性別
 * @returns {Promise<Object>} message = "設定 新幼兒資料 成功"
 */
export const setNewChild = async (name, nationalIdNo, birthdate, gender) => {
  return request(`${BASE_SERVICE}/information`, "POST", { name, nationalIdNo, birthdate, gender }, true);
};

/**
 * 更新 民眾底下 特定幼兒資料
 * @param {String} newName - 新 幼兒姓名
 * @returns {Promise<Object>} message = "更新 幼兒資料 成功"
 */
export const updateChild = async (newName) => {
  return request(`${BASE_SERVICE}/update`, "POST", { newName }, true);
};

/**
 * 刪除 民眾底下 特定幼兒資料
 * @param {Number} id - 目標 幼兒 ID
 * @returns {Promise<Object>} message = "刪除 幼兒資料 成功"
 */
export const deleteChild = async (id) => {
  return request(`${BASE_SERVICE}/delete`, "DELETE", { id }, true);
};