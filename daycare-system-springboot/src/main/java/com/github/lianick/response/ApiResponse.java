package com.github.lianick.response;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//建立 Server 與 Client 在傳遞資料上的統一結構與標準(含錯誤)
@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {

	// private boolean success;	// 成功與否
	private int code;			// 使用 HTTP Status Code (例如 401, 403, 200)
	private String message;		// 錯誤訊息
	private String errorCode;	// 自定義的 程式錯誤碼
	private T data;				// 回傳的資料(200 才有)
	
	// 200 成功的建構式 
	public ApiResponse (int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
	
	// 成功 回應的方法
	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<T>(HttpStatus.OK.value(), message, data);
	}
	
	// 錯誤 回應的方法
	public static <T> ApiResponse<T> error(int code, String errorCode, String message) {
		ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setErrorCode(errorCode);
        response.setMessage(message);
        return response;
	}
}


