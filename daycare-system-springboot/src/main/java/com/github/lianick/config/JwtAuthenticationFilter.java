package com.github.lianick.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.lianick.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{	// OncePerRequestFilter：Spring 提供的基礎類別，確保 每個請求只執行一次過濾器。

	// 【新】定義 Logger 實例
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	@Autowired
	private JwtUtil jwtUtil;

	public static final String AUTH_ERROR_ATTRIBUTE = "authError";
	private static final String HEADER_STRING = "Authorization";	// HTTP 請求中存放 JWT 的 Header 名稱（標準是 Authorization）。
	private static final String TOKEN_PREFIX = "Bearer";			// JWT 前的前綴詞，通常是 Bearer <token>。

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {	// 過濾器的主體邏輯，在每次請求時被呼叫。

		String header = request.getHeader(HEADER_STRING);
		String username = null;
		String authToken = null;
		
		// 1. 從 Header 中 提取 Token
		if (header != null && header.startsWith(TOKEN_PREFIX)) {
			authToken = header.replace(TOKEN_PREFIX + " ", "");

			try {
				// 2. 解析 Token, 提取 Claims
				Claims claims = jwtUtil.extractAllClaims(authToken);
				username = claims.getSubject();
			} catch (ExpiredJwtException e) {
				// Token 過期
				logger.warn("JWT Token 過期: {}", e);
				
				// *** 關鍵步驟：將例外存入 Request 屬性中 ***
				request.setAttribute(AUTH_ERROR_ATTRIBUTE, e);
			} catch (JwtException | IllegalArgumentException e) {
				// Token 簽名錯誤、格式錯誤等
				logger.error("JWT Token 驗證失敗: {}", e);
				
				// *** 關鍵步驟：將例外存入 Request 屬性中 ***
				request.setAttribute(AUTH_ERROR_ATTRIBUTE, e);
			}
		} else {
			logger.warn("缺少 Authorization Header 或 Token 格式錯誤");
		}

		// 3. 驗證通過且 Security Context 中沒有身份驗證物件時，設置身份
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			// 由於我們是無狀態認證，不需要從 DB 重新查詢 UserDetails，
			// 只需要從 Claims 中獲取足夠的資訊來創建身份驗證物件。
			Claims claims = jwtUtil.extractAllClaims(authToken);
			// Long userId = claims.get("userId", Long.class);
			// Long roleNumber = claims.get("roleNumber", Long.class);
			String roleName = claims.get("roleName", String.class);

			// 將角色轉換為 Spring Security 的權限格式
			List<SimpleGrantedAuthority> authorities = Collections.singletonList(
					new SimpleGrantedAuthority(roleName)
					);

			// 創建 Spring Security 的身份驗證物件
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					username,		// Principal (通常是 username)
					null,			// Credentials (Token 驗證模式下通常為 null)
					authorities		// Authorities (用戶權限)
					);	

			// 4. 將身份設置到 Security Context 中
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		// 5. 繼續執行 Filter Chain
		filterChain.doFilter(request, response);

	}

}
