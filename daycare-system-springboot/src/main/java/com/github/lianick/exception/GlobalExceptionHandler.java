package com.github.lianick.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;

import com.github.lianick.response.ApiResponse;

import jakarta.persistence.OptimisticLockException;

@RestControllerAdvice	// = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {
	
	// 處理器類別定義 Logger，用於記錄內部錯誤
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	// @ExceptionHandler(UserNoFoundException.class) 	指定要捕獲的異常類型
	// @ResponseStatus(HttpStatus.UNAUTHORIZED)     	設定響應的 HTTP 狀態碼
	// @ResponseBody                                	告訴 Spring 將返回值（ApiResponse）直接寫入 HTTP 響應體
	
	// 處理找不到使用者 的 異常 (401)
	@ExceptionHandler(UserNotFoundException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleUserNotFoundException(UserNotFoundException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 處理找不到 幼兒 的 異常 (401)
	@ExceptionHandler(ChildNotFoundException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleChildNotFoundException(ChildNotFoundException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.CHILD_NOT_FOUND;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 處理找不到 Priority 的 異常 (401)
	@ExceptionHandler(PriorityNotFoundException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handlePriorityNotFoundException(PriorityNotFoundException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.PRIORITY_NOT_FOUND;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 處理 使用 ENUM 卻 找不到 code 的 異常 (401)
	@ExceptionHandler(EnumNotFoundException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleEnumNotFoundException(EnumNotFoundException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.ENUM_NOT_FOUND;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 使用者已經存在 的 異常 (401)
	@ExceptionHandler(UserExistException.class) 	
	@ResponseStatus(HttpStatus.CONFLICT)     			// 409      	
	public ApiResponse<?> handleUserExistException(UserExistException ex) {
		int statusCode = HttpStatus.CONFLICT.value();
		ErrorCode errorCode = ErrorCode.USER_IS_EXIST;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 角色(Role) 相關 的 異常 (401)
	@ExceptionHandler(RoleFailureException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleRoleFailureException(RoleFailureException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.ROLE_FAILURE;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 機構(Organization) 相關 的 異常 (401)
	@ExceptionHandler(OrganizationFailureException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleOrganizationFailureException(OrganizationFailureException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.ORGANIZATION_FAILURE;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 公告(Announcement) 相關 的 異常 (401)
	@ExceptionHandler(AnnouncementFailureException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleAnnouncementFailureException(AnnouncementFailureException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.ANNOUNCEMENT_FAILURE;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 規範(Regulation) 相關 的 異常 (401)
	@ExceptionHandler(RegulationFailureException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleRegulationFailureException(RegulationFailureException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.REGULATION_FAILURE;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 班級(Classes) 相關 的 異常 (401)
	@ExceptionHandler(ClassesFailureException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleClassesFailureException(ClassesFailureException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.CLASSES_FAILURE;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 案件(Cases) 相關 的 異常 (401)
	@ExceptionHandler(CaseFailureException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleCaseFailureException(CaseFailureException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.CASE_FAILURE;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 撤銷序列(WithdrawalRequests) 相關 的 異常 (401)
	@ExceptionHandler(WithdrawalRequestFailureException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleWithdrawalRequestFailureException(WithdrawalRequestFailureException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.WITHDRAWALREQUEST_FAILURE;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 無法發送電子信箱 的 異常 (500)
	@ExceptionHandler(MailSendFailureException.class) 		
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   // 500
	public ApiResponse<?> handleMailSendFailureException(MailSendFailureException ex) {
		// 明確記錄錯誤，並包含完整的異常物件 ex
		logger.error("服務器內部錯誤：無法發送電子郵件", ex);
		
		int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
		ErrorCode errorCode = ErrorCode.MAIL_SEND_FAILURE;
		// return new ApiResponse<>(statusCode, "服務器內部錯誤：無法發送電子郵件，請稍後重試。" , null);
		return ApiResponse.error(statusCode, errorCode, "服務器內部錯誤：無法發送電子郵件，請稍後重試。");
	}
	
	// token 驗證相關 的 錯誤 (1. 無效不存在 2. 過期 3. 已經被使用)
	@ExceptionHandler(TokenFailureException.class) 		
	@ResponseStatus(HttpStatus.BAD_REQUEST)   			// 400
	public ApiResponse<?> handleTokenFailureException(TokenFailureException ex) {
		int statusCode = HttpStatus.BAD_REQUEST.value();
		ErrorCode errorCode = ErrorCode.TOKEN_FAILURE;
		// return new ApiResponse<>(statusCode, ex.getMessage() , null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 回報數值缺少 相關 的 錯誤 (400)
	@ExceptionHandler(ValueMissException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)   			// 400
	public ApiResponse<?> handleValueMissException(ValueMissException ex) {
		int statusCode = HttpStatus.BAD_REQUEST.value();
		ErrorCode errorCode = ErrorCode.VALUES_MISS;
		// return new ApiResponse<>(statusCode, ex.getMessage() , null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 格式 相關 的 錯誤 (400)
	@ExceptionHandler(FormatterFailureException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)   			// 400
	public ApiResponse<?> handleFormatterFailureException(FormatterFailureException ex) {
		int statusCode = HttpStatus.BAD_REQUEST.value();
		ErrorCode errorCode = ErrorCode.FORMATTER_FAILURE;
		// return new ApiResponse<>(statusCode, ex.getMessage() , null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// JSON 轉換 相關 的 錯誤 (400)
	@ExceptionHandler(JsonFailureException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)   			// 400
	public ApiResponse<?> handleJsonFailureException(JsonFailureException ex) {
		int statusCode = HttpStatus.BAD_REQUEST.value();
		ErrorCode errorCode = ErrorCode.JSON_FAILURE;
		// return new ApiResponse<>(statusCode, ex.getMessage() , null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 檔案 儲存相關 的 錯誤 (400)
	@ExceptionHandler(FileStorageException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)   			// 400
	public ApiResponse<?> handleFileStorageException(FileStorageException ex) {
		int statusCode = HttpStatus.BAD_REQUEST.value();
		ErrorCode errorCode = ErrorCode.FILE_STORAGE_FAILURE;
		// return new ApiResponse<>(statusCode, ex.getMessage() , null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// JWT 權限不足的 錯誤
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN) // 返回 403 狀態碼
	public ApiResponse<Void> handleAccessDeniedException(AccessDeniedException ex) {
		int statusCode = HttpStatus.FORBIDDEN.value();
		ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
		return ApiResponse.error(statusCode, errorCode, "權限不足，無法訪問此資源");	 // 自定義的錯誤訊息
	}
	
	// 處理 樂觀鎖 的衝突 409
	@ExceptionHandler({
		OptimisticLockException.class,					// JPA 標準：更新時 version 不匹配（@Version 衝突）。
		StaleObjectStateException.class,				// Hibernate flush 過程發現 version 不一致或 row 已被改動。
		StaleStateException.class,						// Hibernate update/delete 實際影響 row = 0，可能是資料已被修改或刪除。
		ObjectOptimisticLockingFailureException.class,	// Spring Data 封裝 JPA/Hibernate 樂觀鎖衝突的 RuntimeException。
		ConcurrencyFailureException.class,  			// Spring Data JPA 頂層封裝的並發失敗錯誤，一般是底層樂觀鎖衝突或資料同時被修改。
	    OptimisticLockingFailureException.class  		// Spring ORM 層級封裝的樂觀鎖衝突，對應 JPA/Hibernate 的 version 不匹配。
	})
	@ResponseStatus(HttpStatus.CONFLICT)
	public ApiResponse<Void> handleOptimisticLockException(Exception ex) {
		int statusCode = HttpStatus.CONFLICT.value();
		ErrorCode errorCode = ErrorCode.DATA_CONFLICT;
		return ApiResponse.error(statusCode, errorCode, "資料已被其他使用者修改，請重新讀取後再試。");	 // 自定義的錯誤訊息	
	}
	
	// 處理所有未被明確定義的 RuntimeException (預防萬一, 最終保護)
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) 	// 500
	public ApiResponse<?> handleGenericRuntimeException(RuntimeException ex) {
		// 明確記錄錯誤，並包含完整的異常物件 ex
		logger.error("發生未預期的伺服器錯誤！", ex);
		
		int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
		ErrorCode errorCode = ErrorCode.RUNTIME_ERROR;
		// return new ApiResponse<>(statusCode, "發生未預期的伺服器錯誤，請聯繫管理員。" , null);
		return ApiResponse.error(statusCode, errorCode, "發生未預期的伺服器錯誤，請聯繫管理員。");
	}
}
