package com.github.lianick.model.dto;

public interface PasswordAwareDTO {

	/**
     * 獲取使用者輸入的明文密碼 (Raw Password)
     * @return 明文密碼
     */
    String getRawPassword();
}
