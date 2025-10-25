package com.github.lianick.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lianick.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * 負責處理 JWT 生成 解析
 * */
@Component
public class JwtUtil {

	@Autowired
	private JwtProperties jwtProperties;
	
	// 取得簽名用的 Key, 只能用於 HMAC 演算法（如 HS256）
	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	/**
     * 從用戶資訊生成 JWT Token
     * @param username 用戶帳號
     * @param userId 用戶ID
     * @return JWT 字串
     */
	public String generateToken(String username, Long userId, Long roleNumber, String roleName) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userId);
		claims.put("roleNumber", roleNumber);
		claims.put("roleName", roleName);
		
		return Jwts.builder()
				.setClaims(claims)														// 自訂資料（userId, roleNumber）
				.setSubject(username)													// 帳號名稱
				.setIssuedAt(new Date(System.currentTimeMillis()))						// 發行時間
				.setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))	// 到期時間
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)					// 簽名 JWT，以防篡改。
				.compact();
	}
	
	/**
     * 解析 JWT Token 並返回 Claims (宣告)
     * @param token JWT 字串
     * @return Claims
     * 
     * 若簽名錯誤或 Token 過期，這邊會丟出例外（如 JwtException、ExpiredJwtException）
     */
	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
}
