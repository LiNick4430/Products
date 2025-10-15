package com.github.lianick.service;

import com.github.lianick.exception.TokenFailureException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.model.dto.PasswordAwareDTO;
import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.user.UserForgetPasswordDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.dto.user.UserVerifyDTO;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.response.ApiResponse;

/*
	業務流程確認
	1. Users 建立/啟用： 建立基礎帳號並啟用（isActive = true）。
	2. Role 決定身份： 根據 Users.role 屬性，你的 Service 層會執行：
		如果 role 是民眾 → 呼叫 UserPublicService.create() → 成功創建 UserPublic。
		如果 role 是員工 → 呼叫 UserAdminService.create() → 成功創建 UserAdmin。
	防護： 如果 Service 層有人試圖對一個已是民眾的帳號創建員工紀錄，admin_user 表會因為 Primary Key 衝突而拋出資料庫錯誤（如果 Service 層沒有提前檢查的話）。
 * */

public interface UserService {

	// 註冊帳號
	ApiResponse<UserRegisterDTO> registerUser(UserRegisterDTO userRegisterDTO);
	// 驗證帳號
	ApiResponse<Void> veriftyUser(UserVerifyDTO userVerifyDTO) throws TokenFailureException;
	// 登陸帳號
	ApiResponse<UserLoginDTO> loginUser(UserLoginDTO userLoginDTO) throws UserNoFoundException;
	
	// 忘卻密碼 三步驟
	// 發送驗證信
	ApiResponse<Void> forgetPasswordSendEmail(UserForgetPasswordDTO userForgetPasswordDTO) throws UserNoFoundException;
	// token 驗證
	ApiResponse<Void> forgetPasswordVerifty(UserForgetPasswordDTO userForgetPasswordDTO) throws TokenFailureException;
	// 通過驗證信後 修改密碼
	ApiResponse<Void> forgetPasswordUpdatePassword(UserForgetPasswordDTO userForgetPasswordDTO) throws TokenFailureException, UserNoFoundException;
	
	// 修改帳號資料 二步驟
	// 確認密碼
	ApiResponse<Void> updateUserCheckPassword(UserUpdateDTO userUpdateDTO);
	// 假設要 更新信箱 的 信箱驗證方法
	ApiResponse<Void> updateUserVeriftyEmail(UserUpdateDTO userUpdateDTO);
	// update 資料
	ApiResponse<Void> updateUser(UserUpdateDTO userUpdateDTO);
	
	// 刪除帳號
	ApiResponse<Void> deleteUser(UserDeleteDTO userDeleteDTO) throws UserNoFoundException;
	
	// 產生 帳號驗證碼 同時寄出驗證信
	void generateUserToken(Users users, String subject, String apiName);
	
	// 密碼驗證, 泛型 T 必須是 PasswordAwareDTO 或其子類
	<T extends PasswordAwareDTO> Boolean checkPassword (T userDto, Users tableUser);
}
