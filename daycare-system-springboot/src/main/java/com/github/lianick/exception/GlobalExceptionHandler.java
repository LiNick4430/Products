package com.github.lianick.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import com.github.lianick.response.ApiResponse;

@RestControllerAdvice	// = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {
	
	// 處理器類別定義 Logger，用於記錄內部錯誤
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	// @ExceptionHandler(UserNoFoundException.class) 	指定要捕獲的異常類型
	// @ResponseStatus(HttpStatus.UNAUTHORIZED)     	設定響應的 HTTP 狀態碼
	// @ResponseBody                                	告訴 Spring 將返回值（ApiResponse）直接寫入 HTTP 響應體
	
	// 處理找不到使用者 的 異常 (401)
	@ExceptionHandler(UserNoFoundException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleUserNoFoundException(UserNoFoundException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 處理找不到 幼兒 的 異常 (401)
	@ExceptionHandler(ChildNoFoundException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleChildNoFoundException(ChildNoFoundException ex) {
		int statusCode = HttpStatus.UNAUTHORIZED.value();
		ErrorCode errorCode = ErrorCode.CHILD_NOT_FOUND;
		// return new ApiResponse<>(statusCode, ex.getMessage(), null);
		return ApiResponse.error(statusCode, errorCode, ex.getMessage());
	}
	
	// 處理 使用 ENUM 卻 找不到 code 的 異常 (401)
	@ExceptionHandler(EnumNoFoundException.class) 	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)     		// 401
	public ApiResponse<?> handleEnumNoFoundException(EnumNoFoundException ex) {
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
