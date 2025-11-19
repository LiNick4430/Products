package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.CaseFailureException;
import com.github.lianick.exception.ClassesFailureException;
import com.github.lianick.exception.OrganizationFailureException;
import com.github.lianick.exception.RoleFailureException;
import com.github.lianick.model.dto.clazz.ClassCreateDTO;
import com.github.lianick.model.dto.clazz.ClassDTO;
import com.github.lianick.model.dto.clazz.ClassDeleteDTO;
import com.github.lianick.model.dto.clazz.ClassFindDTO;
import com.github.lianick.model.dto.clazz.ClassLinkCaseDTO;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.model.enums.CaseStatus;
import com.github.lianick.repository.CasesRepository;
import com.github.lianick.repository.ClassesRepository;
import com.github.lianick.repository.OrganizationRepository;
import com.github.lianick.service.ClassService;
import com.github.lianick.util.SecurityUtil;
import com.github.lianick.util.UserSecurityUtil;
import com.github.lianick.util.validate.ClassValidationUtil;

@Service
@Transactional				// 確保 完整性 
public class ClassServiceImpl implements ClassService{
	
	@Autowired
	private ClassesRepository classesRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private CasesRepository casesRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserSecurityUtil userSecurityUtil;

	@Autowired
	private ClassValidationUtil classValidationUtil;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	@Override
	@PreAuthorize("isAuthenticated()")	 
	public List<ClassDTO> findAllClassByOrganization(ClassFindDTO classFindDTO) {
		// 0. 取得 使用方法 的 使用者權限
		Long currentRoleNumber = SecurityUtil.getCurrentRoleNumber();
		
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		
		// 1. 找尋 特定的 機構 
		List<Organization> organizations = new ArrayList<>();
		
		// 民眾 使用 organizationName 搜尋
		if (currentRoleNumber.equals(1L)) {
			classValidationUtil.validateFindByPublic(classFindDTO);
			
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
			classValidationUtil.validateFindByAdmin(classFindDTO);
			
			if (!tableUser.getAdminInfo().getOrganization().getOrganizationId().equals(classFindDTO.getOrganizationId())) {
				throw new AccessDeniedException("權限不足: 無法搜尋其他機構的班級");
			}
			
			Organization organization = entityFetcher.getOrganizationById(classFindDTO.getOrganizationId());
			
			organizations.add(organization);
		
		} 
		// 管理者 使用 organizationId 搜尋
		else if (currentRoleNumber.equals(3L)) {
			classValidationUtil.validateFindByAdmin(classFindDTO);
			
			Organization organization = entityFetcher.getOrganizationById(classFindDTO.getOrganizationId());
			
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
		// 0. 取得 使用方法 的 使用者權限
		Long currentRoleNumber = SecurityUtil.getCurrentRoleNumber();
		
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		
		// 1. 檢查是否缺乏必要資料
		classValidationUtil.validateCreateFields(classCreateDTO);
		
		// 檢查 機構 是否 存在
		Organization organization = entityFetcher.getOrganizationById(classCreateDTO.getOrganizationId());
		
		// 2. 判定 使用者的 權限
		classValidationUtil.validateRoleNumber(currentRoleNumber, tableUser, classCreateDTO.getOrganizationId(), "建立");
		
		// 3. 建立班級
		Classes classes = modelMapper.map(classCreateDTO, Classes.class);
		classes.setOrganization(organization);
		
		// 4. 儲存
		classes = classesRepository.save(classes);
		
		// 5. Entity -> DTO
		return modelMapper.map(classes, ClassDTO.class);
	}

	
	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public ClassDTO classLinkCase(ClassLinkCaseDTO classLinkCaseDTO) {
		// 0. 取得 使用方法 的 使用者權限
		Long currentRoleNumber = SecurityUtil.getCurrentRoleNumber();
		
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		
		// 1. 檢查是否缺乏必要資料
		classValidationUtil.validateLinkCase(classLinkCaseDTO);
		
		// 檢查 機構 是否 存在
		Organization organization = entityFetcher.getOrganizationById(classLinkCaseDTO.getOrganizationId());
		// 檢查 班級 是否 存在
		Classes classes = entityFetcher.getClassesById(classLinkCaseDTO.getId());
		
		// 檢查 班級 是否在 該機構 底下
		if (!classes.getOrganization().equals(organization)) {
			throw new OrganizationFailureException("機構 查無 班級");
		}
		
		// 檢查 案件 是否 存在 / 通過
		Cases cases = entityFetcher.getCasesById(classLinkCaseDTO.getId());
		if (!cases.getStatus().equals(CaseStatus.PASSED)) {
			throw new CaseFailureException("案件 尚未 通過");
		}
		
		// 檢查 人數 是否會超過
		if (classes.getMaxCapacity() == classes.getCurrentCount()) {
			throw new ClassesFailureException("人數已滿 無法添加");
		}
		
		// 2. 判定 使用者的 權限
		classValidationUtil.validateRoleNumber(currentRoleNumber, tableUser, classLinkCaseDTO.getOrganizationId(), "更新");
		
		// 3. 建立關連 並 新增人數
		cases.setClasses(classes);
		
		if (classes.getCases() == null) {
			classes.setCases(new HashSet<Cases>());
		}
		classes.getCases().add(cases);
		classes.setCurrentCount(classes.getCases().size());		// 在內存中計算並更新 currentCount 
		
		// 4. 回存
		cases = casesRepository.save(cases);
		classes = classesRepository.save(classes);
		
		// 5. Entity -> DTO
		return modelMapper.map(classes, ClassDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public void deleteClass(ClassDeleteDTO classDeleteDTO) {
		// 0. 取得 使用方法 的 使用者權限
		Long currentRoleNumber = SecurityUtil.getCurrentRoleNumber();
		
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		
		// 1. 檢查是否缺乏必要資料
		classValidationUtil.validateDelete(classDeleteDTO);
		
		// 檢查 機構 是否 存在
		Organization organization = entityFetcher.getOrganizationById(classDeleteDTO.getOrganizationId());
		// 檢查 班級 是否 存在
		Classes classes = entityFetcher.getClassesById(classDeleteDTO.getId());
				
		// 檢查 班級 是否在 該機構 底下
		if (!classes.getOrganization().equals(organization)) {
			throw new OrganizationFailureException("機構 查無 班級");
		}
		
		// 檢查 班級ID 是否 和 班級名稱 相符
		if (!classes.getName().equals(classDeleteDTO.getName())) {
			throw new ClassesFailureException("班級名稱與 ID 不匹配，請確認您輸入的名稱是否正確。");
		}
		
		// 2. 判定 使用者的 權限
		classValidationUtil.validateRoleNumber(currentRoleNumber, tableUser, classDeleteDTO.getOrganizationId(), "刪除");
		
		// 3. 檢查 是否有關連的 案件
		Long activeCaseCount = classesRepository.countActiveCasesByClassId(classes.getClassId());
		
		if (activeCaseCount > 0) {
			throw new ClassesFailureException("尚有 " + activeCaseCount + " 個活動中的案件與此班級關聯，無法刪除。請先將所有幼兒退班或轉班。");
		}
		
		// 額外檢查冗餘欄位 (防禦性編程)
		if (classes.getCurrentCount() != 0) {
			// 如果 activeCaseCount == 0, 但 currentCount > 0, 則數據不一致。
			// 由於已經確認沒有活動案件，允許刪除
			// TODO 數據異常點 需要 紀錄LOG
		}
		
		// 4. 執行 軟刪除班級
		LocalDateTime now = LocalDateTime.now();
		classes.setDeleteAt(now);
		
		// 5. 沒有 子關連 表格 (唯一的 案件已經被檢查了)
		
		// 6. 回存
		classesRepository.save(classes);
	}
}
