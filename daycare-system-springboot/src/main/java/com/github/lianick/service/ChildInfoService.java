package com.github.lianick.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.github.lianick.model.dto.userPublic.ChildCreateDTO;
import com.github.lianick.model.dto.userPublic.ChildDTO;
import com.github.lianick.model.dto.userPublic.ChildDeleteDTO;
import com.github.lianick.model.dto.userPublic.ChildUpdateDTO;
import com.github.lianick.model.eneity.UserPublic;

// 負責處理 幼兒資料
public interface ChildInfoService {
	
	// 用 JWT 找尋 自己的 public 帳號
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	UserPublic findUserPublic();

	// 尋找 全部的幼兒資料
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	List<ChildDTO> findAllChildByUserPublic();
	
	// 尋找 特定的幼兒資料
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	ChildDTO findChildByUserPublic(ChildDTO childDTO);
	
	// 設定 民眾底下 新幼兒資料
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	ChildCreateDTO createChildInfo(ChildCreateDTO childCreateDTO);
	
	// 更新 民眾底下 特定幼兒資料
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	ChildUpdateDTO updateChildInfo(ChildUpdateDTO childUpdateDTO);
	
	// 刪除 民眾底下 特定幼兒資料
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	void deleteChildInfo(ChildDeleteDTO childDeleteDTO);
}
