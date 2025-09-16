package com.github.lianick.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

// 利用 SHA-256 產生 hash
public class PasswordHash {

	// 用於 產生 隨機的 salt
	private static String generateSalt() {
		SecureRandom sr = new SecureRandom();
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}
	
	// 密碼雜湊 涵式
	public static String getHashPassword(String password, String salt) {
		try {
			// 建立一個 SHA-256 演算法
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			
			// 加入 salt
			md.update(salt.getBytes());
			
			// 將密碼字串轉成位元組陣列並進行雜湊運算
			byte[] hashBytes = md.digest(password.getBytes());
			
			// 建立一個 StringBuffer 用來組合雜湊後的16進位字串
			StringBuilder sb = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					sb.append('0');
				}
				sb.append(hex);
			}
			return sb.toString();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	// 測試用
	public static void main(String[] args) {
		
		String password = "1234";	// 明文 密碼
		String salt = generateSalt();
		String hash = getHashPassword(password, salt);
		System.out.printf("password: %s, salt: %s, hash: %s%n", password, salt, hash);
	}
}
