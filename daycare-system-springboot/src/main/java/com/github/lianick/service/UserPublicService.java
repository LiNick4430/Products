package com.github.lianick.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.userPublic.UserPublicDTO;
import com.github.lianick.model.dto.userPublic.UserPublicCreateDTO;
import com.github.lianick.model.dto.userPublic.UserPublicUpdateDTO;

/*
	業務流程確認
	2. Role 決定身份： 根據 Users.role 屬性，你的 Service 層會執行：
		如果 role 是民眾 → 呼叫 UserPublicService.createUserPublic() → 成功創建 UserPublic。
		如果 role 是員工 → 呼叫 UserAdminService.createUserAdmin() → 成功創建 UserAdmin。
	防護： 如果 Service 層有人試圖對一個已是民眾的帳號創建員工紀錄，admin_user 表會因為 Primary Key 衝突而拋出資料庫錯誤（如果 Service 層沒有提前檢查的話）。
	
	在 Service Interface 上加上 @PreAuthorize 是良好的實踐，它定義了服務的安全契約。
* */

public interface UserPublicService {

	// 搜尋 全部 民眾帳號
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")		// 只能 這兩個 角色 的 JWT 才能使用
	List<UserPublicDTO> findAllUserPublic();
	
	// 根據 username 搜尋 民眾帳號
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")		// 只能 這兩個 角色 的 JWT 才能使用
	UserPublicDTO findByUsername(UserPublicDTO userPublicDTO);
	
	// 根據 username 填寫 基本資料
	@PreAuthorize("isAuthenticated()")	// 確保只有持有有效 JWT 的用戶才能存取
	UserPublicCreateDTO createUserPublic(UserPublicCreateDTO userPublicSaveDTO);
	
	// 根據 username 更新 基本資料
	// 密碼檢查
	@PreAuthorize("isAuthenticated()")	// 確保只有持有有效 JWT 的用戶才能存取
	UserPublicUpdateDTO updateUserPublicCheckPassword(UserPublicUpdateDTO userPublicUpdateDTO);
	// 正式更新
	@PreAuthorize("isAuthenticated()")	// 確保只有持有有效 JWT 的用戶才能存取
	UserPublicUpdateDTO updateUserPublic(UserPublicUpdateDTO userPublicUpdateDTO);
	
	// 根據 username 刪除 民眾帳號
	@PreAuthorize("isAuthenticated()")	// 確保只有持有有效 JWT 的用戶才能存取
	void deleteUserPublic(UserDeleteDTO userDeleteDTO);
	
}
