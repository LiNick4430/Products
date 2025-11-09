package com.github.lianick.service.impl;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.github.lianick.model.dto.clazz.ClassCreateDTO;
import com.github.lianick.model.dto.clazz.ClassDTO;
import com.github.lianick.model.dto.clazz.ClassDeleteDTO;
import com.github.lianick.model.dto.clazz.ClassFindDTO;
import com.github.lianick.model.dto.clazz.ClassUpdateDTO;
import com.github.lianick.service.ClassService;

import jakarta.transaction.Transactional;

@Service
@Transactional				// 確保 完整性 
public class ClassServiceImpl implements ClassService{

	@Override
	@PreAuthorize("isAuthenticated()")
	public List<ClassDTO> findAllClassByOrganization(ClassFindDTO classFindDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public ClassDTO createClass(ClassCreateDTO classCreateDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public ClassDTO updateClass(ClassUpdateDTO classUpdateDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public void deleteClass(ClassDeleteDTO classDeleteDTO) {
		// TODO Auto-generated method stub
		
	}

}
