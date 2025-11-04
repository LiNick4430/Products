package com.github.lianick.service;

import java.util.List;

import com.github.lianick.model.dto.child.ChildCreateDTO;
import com.github.lianick.model.dto.child.ChildDTO;
import com.github.lianick.model.dto.child.ChildDeleteDTO;
import com.github.lianick.model.dto.child.ChildUpdateDTO;

// 負責處理 幼兒資料
public interface ChildInfoService {
	/** 尋找 全部的幼兒資料<p>
	 * 需要 @PreAuthorize("isAuthenticated()")
	 * */
	List<ChildDTO> findAllChildByUserPublic();
	
	/** 尋找 特定的幼兒資料<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	 * */
	ChildDTO findChildByUserPublic(ChildDTO childDTO);
	
	/** 設定 民眾底下 新幼兒資料<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	 * */
	ChildCreateDTO createChildInfo(ChildCreateDTO childCreateDTO);
	
	/** 更新 民眾底下 特定幼兒資料<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	 * */
	ChildUpdateDTO updateChildInfo(ChildUpdateDTO childUpdateDTO);
	
	/** 刪除 民眾底下 特定幼兒資料<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	 * */
	void deleteChildInfo(ChildDeleteDTO childDeleteDTO);
}
