package com.github.lianick.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lianick.exception.ErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter	// JSON 序列化（將 Java 物件轉為 JSON）依賴於 Getter 方法
@Schema(description = "統一的 API 回傳格式") //建立 Server 與 Client 在傳遞資料上的統一結構與標準(含錯誤)
public class ApiResponse<T> {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	// private boolean success;		// 成功與否
	@Schema(description = "HTTP 狀態碼", example = "200")
	private int code;				// 使用 HTTP Status Code (例如 401, 403, 200)
	
	@Schema(description = "自定義錯誤狀態碼(僅在錯誤時出現)", nullable = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ErrorCode errorCode;	// 自定義的 程式錯誤碼 (非 200 才有)
	
	@Schema(description = "成功/錯誤 訊息說明", example = "登陸成功")
	private String message;			// 訊息
	
	@Schema(description = "實際回傳的資料內容", nullable = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;					// 回傳的資料 (200 才有)
	
	// 200 成功 的 建構式 
	private ApiResponse (int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
	
	// 其他 錯誤 的 建構式
	private ApiResponse (int code, ErrorCode errorCode, String message) {
		this.code = code;
		this.errorCode = errorCode;
		this.message = message;
	}
	
	// 成功 回應的方法
	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<T>(HttpStatus.OK.value(), message, data);
	}
	
	// 錯誤 回應的方法
	public static <T> ApiResponse<T> error(int code, ErrorCode errorCode, String message) {
        return new ApiResponse<T>(code, errorCode, message);
	}
	
	// 把 API 錯誤回應 轉成 字串 的方法
	public static String toErrorJsonString(int code, ErrorCode errorCode, String message) {
		try {
			return objectMapper.writeValueAsString(new ApiResponse<>(code, errorCode, message));
		} catch (Exception e) {
			return "{\"code\":" + code + 
		               ",\"errorCode\":\"" + errorCode + 
		               "\",\"message\":\"" + message + 
		               "\",\"data\":null}";
		}
	}
}