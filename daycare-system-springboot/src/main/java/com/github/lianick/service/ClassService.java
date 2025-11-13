package com.github.lianick.service;

import java.util.List;

import com.github.lianick.model.dto.clazz.ClassCreateDTO;
import com.github.lianick.model.dto.clazz.ClassDTO;
import com.github.lianick.model.dto.clazz.ClassDeleteDTO;
import com.github.lianick.model.dto.clazz.ClassFindDTO;
import com.github.lianick.model.dto.clazz.ClassLinkCaseDTO;

public interface ClassService {
	
	/** 用 機構 搜尋 其下 中 班級 資料 
	 * 需要 @PreAuthorize("isAuthenticated()")
	 * */
	List<ClassDTO> findAllClassByOrganization(ClassFindDTO classFindDTO);
	
	/** 建立 該 機構 的 新 班級<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	ClassDTO createClass(ClassCreateDTO classCreateDTO);
	
	/** 班級 和 審核過的案件 建立關連 (同時人數+1) <p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	ClassDTO classLinkCase(ClassLinkCaseDTO classLinkCaseDTO);
	
	/** 刪除 特定班級<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	void deleteClass(ClassDeleteDTO classDeleteDTO);
}
