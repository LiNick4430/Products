package com.github.lianick.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminCreateDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminUpdateDTO;

/*
	業務流程確認
	2. Role 決定身份： 根據 Users.role 屬性，你的 Service 層會執行：
		如果 role 是民眾 → 呼叫 UserPublicService.createUserPublic() → 成功創建 UserPublic。
		如果 role 是員工 → 呼叫 UserAdminService.createUserAdmin() → 成功創建 UserAdmin。
	防護： 如果 Service 層有人試圖對一個已是民眾的帳號創建員工紀錄，admin_user 表會因為 Primary Key 衝突而拋出資料庫錯誤（如果 Service 層沒有提前檢查的話）。
	
	在 Service Interface 上加上 @PreAuthorize 是良好的實踐，它定義了服務的安全契約。
* */
public interface UserAdminService {

	// 主管 尋找 員工們 的資料
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	List<UserAdminDTO> findAllUserAdmin();
	
	// 主管 尋找 特定 員工 的資料
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	UserAdminDTO findByUsername(UserAdminDTO userAdminDTO);
	
	// 主管 創建 自己 的 資料
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	UserAdminDTO createUserAdmin(UserAdminCreateDTO userAdminCreateDTO);
	
	// 主管 更新 特定員工 的 資料
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	UserAdminUpdateDTO updateUserAdmin(UserAdminUpdateDTO userAdminUpdateDTO);
	
	// 主管 刪除 特定的員工資料 (非本人)
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	void deleteUserAdmin(UserDeleteDTO userDeleteDTO);
	
}
