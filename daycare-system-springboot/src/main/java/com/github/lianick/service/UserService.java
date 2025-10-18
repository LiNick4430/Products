package com.github.lianick.service;

import com.github.lianick.model.dto.user.PasswordAwareDTO;
import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.user.UserForgetPasswordDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.dto.user.UserVerifyDTO;
import com.github.lianick.model.eneity.Users;

/*
	業務流程確認
	1. Users 建立/啟用： 建立基礎帳號並啟用（isActive = true）。
	2. Role 決定身份： 根據 Users.role 屬性，你的 Service 層會執行：
		如果 role 是民眾 → 呼叫 UserPublicService.createUserPublic() → 成功創建 UserPublic。
		如果 role 是員工 → 呼叫 UserAdminService.createUserAdmin() → 成功創建 UserAdmin。
	防護： 如果 Service 層有人試圖對一個已是民眾的帳號創建員工紀錄，admin_user 表會因為 Primary Key 衝突而拋出資料庫錯誤（如果 Service 層沒有提前檢查的話）。
 * */

public interface UserService {

	// 註冊帳號
	UserRegisterDTO registerUser(UserRegisterDTO userRegisterDTO);
	// 驗證帳號
	UserVerifyDTO veriftyUser(String token);
	// 登陸帳號
	UserLoginDTO loginUser(UserLoginDTO userLoginDTO);
	
	// 忘卻密碼 三步驟
	// 發送驗證信
	UserForgetPasswordDTO forgetPasswordSendEmail(UserForgetPasswordDTO userForgetPasswordDTO);
	// token 驗證
	UserForgetPasswordDTO forgetPasswordVerifty(String token);
	// 通過驗證信後 修改密碼
	UserForgetPasswordDTO forgetPasswordUpdatePassword(UserForgetPasswordDTO userForgetPasswordDTO);
	
	// 修改帳號資料
	// 確認密碼
	UserUpdateDTO updateUserCheckPassword(UserUpdateDTO userUpdateDTO);
	// update 資料
	UserUpdateDTO updateUser(UserUpdateDTO userUpdateDTO);
	
	// 刪除帳號
	void deleteUser(UserDeleteDTO userDeleteDTO);
	
	// 產生 帳號驗證碼 同時 寄出驗證信
	void generateUserToken(Users users, String subject, String apiName);
	
	// 密碼驗證, 泛型 T 必須是 PasswordAwareDTO 或其子類
	<T extends PasswordAwareDTO> Boolean checkPassword (T userDto, Users tableUser);
}
