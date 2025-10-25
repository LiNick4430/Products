package com.github.lianick.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.github.lianick.model.dto.userPublic.ChildCreateDTO;
import com.github.lianick.model.dto.userPublic.ChildDTO;
import com.github.lianick.model.dto.userPublic.ChildDeleteDTO;
import com.github.lianick.model.dto.userPublic.ChildUpdateDTO;

// 負責處理 幼兒資料
public interface ChildInfoService {

	// 尋找 全部的幼兒資料
	@PreAuthorize("isAuthenticated()")	// 確保只有持有有效 JWT 的用戶才能存取
	List<ChildDTO> findAllChild();
	
	// 尋找 民眾底下 特定幼兒資料
	@PreAuthorize("isAuthenticated()")	// 確保只有持有有效 JWT 的用戶才能存取
	ChildDTO findChild(ChildDTO childDTO);
	
	// 設定 民眾底下 新幼兒資料
	@PreAuthorize("isAuthenticated()")	// 確保只有持有有效 JWT 的用戶才能存取
	ChildCreateDTO createChildInfo(ChildCreateDTO childCreateDTO);
	
	// 更新 民眾底下 特定幼兒資料
	@PreAuthorize("isAuthenticated()")	// 確保只有持有有效 JWT 的用戶才能存取
	ChildUpdateDTO updateChildInfo(ChildUpdateDTO childUpdateDTO);
	
	// 刪除 民眾底下 特定幼兒資料
	@PreAuthorize("isAuthenticated()")	// 確保只有持有有效 JWT 的用戶才能存取
	void deleteChildInfo(ChildDeleteDTO childDeleteDTO);
}
