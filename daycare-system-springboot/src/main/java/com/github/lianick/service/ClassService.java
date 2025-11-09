package com.github.lianick.service;

import java.util.List;

import com.github.lianick.model.dto.clazz.ClassCreateDTO;
import com.github.lianick.model.dto.clazz.ClassDTO;
import com.github.lianick.model.dto.clazz.ClassDeleteDTO;
import com.github.lianick.model.dto.clazz.ClassFindDTO;
import com.github.lianick.model.dto.clazz.ClassUpdateDTO;

public interface ClassService {

	/** 搜尋 該 機構 中 班級 資料 
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	List<ClassDTO> findAllClassByOrganization(ClassFindDTO classFindDTO);
	
	/** 建立 該 機構 的 新 班級<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	ClassDTO createClass(ClassCreateDTO classCreateDTO);
	
	/** 更新 特定班級 的 現有人數<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	ClassDTO updateClass(ClassUpdateDTO classUpdateDTO);
	
	/** 刪除 特定班級<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	void deleteClass(ClassDeleteDTO classDeleteDTO);
}
