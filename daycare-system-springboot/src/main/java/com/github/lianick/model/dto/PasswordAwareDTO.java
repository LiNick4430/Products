package com.github.lianick.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface PasswordAwareDTO {

	/**
     * 獲取使用者輸入的明文密碼 (Raw Password)
     * @return 明文密碼
     */
	// 告訴 Jackson 在序列化（回傳 JSON）時忽略這個方法
	@JsonIgnore
    String getRawPassword();
}
