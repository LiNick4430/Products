package com.github.lianick.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.OrganizationFailureException;
import com.github.lianick.exception.RoleFailureException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.clazz.ClassCreateDTO;
import com.github.lianick.model.dto.clazz.ClassDTO;
import com.github.lianick.model.dto.clazz.ClassDeleteDTO;
import com.github.lianick.model.dto.clazz.ClassFindDTO;
import com.github.lianick.model.dto.clazz.ClassUpdateDTO;
import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.ClassesRepository;
import com.github.lianick.repository.OrganizationRepository;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.service.ClassService;
import com.github.lianick.util.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
@Transactional				// 確保 完整性 
public class ClassServiceImpl implements ClassService{
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private ClassesRepository classesRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	@PreAuthorize("isAuthenticated()")	 
	public List<ClassDTO> findAllClassByOrganization(ClassFindDTO classFindDTO) {
		// 0. 取得 使用方法 的 使用者權限
		Long currentRoleNumber = SecurityUtils.getCurrentRoleNumber();
		
		String currentUsername = SecurityUtils.getCurrentUsername();
		Users tableUser = usersRepository.findByAccount(currentUsername)
		        .orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
		
		// 1. 找尋 特定的 機構 
		List<Organization> organizations = new ArrayList<>();
		
		// 民眾 使用 organizationName 搜尋
		if (currentRoleNumber.equals(1L)) {
			if (classFindDTO.getOrganizationName() == null || classFindDTO.getOrganizationName().isBlank()) {
				throw new ValueMissException("缺少必要的搜尋資料：機構名稱");
			}
			// 民眾搜尋時 不是 用 完整名稱 來搜尋
			String organizationNameLike = "%" +  classFindDTO.getOrganizationName() + "%";
			
			// 搜尋到的結果 可能是 複數
			organizations = organizationRepository.findByNameLike(organizationNameLike);
			
			if (organizations.isEmpty()) {
				throw new OrganizationFailureException("查無包含此名稱的機構");
			}
		} 
		// 基層員工 使用 organizationId 搜尋
		else if (currentRoleNumber.equals(2L)) {
			if (classFindDTO.getOrganizationId() == null) {
				throw new ValueMissException("缺少必要的搜尋資料：機構 ID");
			}
			
			if (!tableUser.getAdminInfo().getOrganization().getOrganizationId().equals(classFindDTO.getOrganizationId())) {
				throw new AccessDeniedException("權限不足: 無法搜尋其他機構的班級");
			}
			
			Organization organization = organizationRepository.findById(classFindDTO.getOrganizationId())
					.orElseThrow(() -> new OrganizationFailureException("機構找不到"));
			
			organizations.add(organization);
		
		} 
		// 管理者 使用 organizationId 搜尋
		else if (currentRoleNumber.equals(3L)) {
			if (classFindDTO.getOrganizationId() == null) {
				throw new ValueMissException("缺少必要的搜尋資料：機構 ID");
			}
			
			Organization organization = organizationRepository.findById(classFindDTO.getOrganizationId())
					.orElseThrow(() -> new OrganizationFailureException("機構找不到"));
			
			organizations.add(organization);
			
		} 
		// 其他角色
		else {
			throw new RoleFailureException("角色錯誤：不支援此權限的班級查詢");
		}
		
		// 把搜尋的 結果 依序轉換後 填入 回傳陣列
		List<ClassDTO> classDTOs = new ArrayList<>();
		
		organizations.forEach(organization -> {
			List<Classes> classesList = classesRepository.findByOrganization(organization);
			
			classesList.forEach(classes -> {
				ClassDTO classDTO = modelMapper.map(classes, ClassDTO.class);
				classDTOs.add(classDTO);
			});
		}); 
		
		return classDTOs;
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
