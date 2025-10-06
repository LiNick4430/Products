package com.github.lianick.service;

/*
	業務流程確認
	
	1. Users 建立/啟用： 建立基礎帳號並啟用（isActive = true）。
	2. Role 決定身份： 根據 Users.role 屬性，你的 Service 層會執行：
		如果 role 是民眾 → 呼叫 UserPublicService.create() → 成功創建 UserPublic。
		如果 role 是員工 → 呼叫 UserAdminService.create() → 成功創建 UserAdmin。
	防護： 如果 Service 層有人試圖對一個已是民眾的帳號創建員工紀錄，admin_user 表會因為 Primary Key 衝突而拋出資料庫錯誤（如果 Service 層沒有提前檢查的話）。
 * */
public interface UserSrevice {

}
