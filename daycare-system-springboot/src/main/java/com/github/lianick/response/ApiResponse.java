package com.github.lianick.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

//建立 Server 與 Client 在傳遞資料上的統一結構與標準(含錯誤)
@Getter
@Setter
@AllArgsConstructor
@Accessors(chain = true)
public class ApiResponse<T> {

	// private boolean success;	// 成功與否
	private int code;			// HTTP 定義碼
	private String message;		// 訊息
	private T data;				// 回傳的資料 通常是 json
}

/**
	@Accessors(chain = true) lombok 功能, 可以 方法串聯
	
	原本寫法
	ApiResponse<String> response = new ApiResponse<>();
	response.setSuccess(true);
	response.setCode(200);
	response.setMessage("註冊成功");
	response.setData("user-id-123");

	使用功能後
	ApiResponse<String> response = new ApiResponse<>()
								    .setSuccess(true)
								    .setCode(200)
								    .setMessage("註冊成功")
								    .setData("user-id-123");
*/
