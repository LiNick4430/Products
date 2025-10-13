package com.github.lianick.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class TokenUUID {

	// 產生一個新的 UUID 實例
	// UUID uuid = UUID.randomUUID();

	// 將 UUID 轉換為標準的 36 個字元的字串 (包含連字號 "-")
	// String tokenWithHyphens = uuid.toString();

	// 通常用於 Token 時，我們會移除連字號，使其更短、更適合 URL 或資料庫儲存
	// String token = uuid.toString().replace("-", ""); 
	
	public String generateToekn() {

		// 產生 UUID 並移除連字號，得到一個 32 個字元的十六進制字串
		String toekn = UUID.randomUUID().toString().replace("-", "");
		return toekn;
	}
	
}
