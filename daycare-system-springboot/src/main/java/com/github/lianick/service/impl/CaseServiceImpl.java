package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.model.dto.WithdrawalRequestDTO;
import com.github.lianick.model.dto.cases.CaseClassDTO;
import com.github.lianick.model.dto.cases.CaseCompleteDTO;
import com.github.lianick.model.dto.cases.CaseCreateDTO;
import com.github.lianick.model.dto.cases.CaseDTO;
import com.github.lianick.model.dto.cases.CaseFindAdminDTO;
import com.github.lianick.model.dto.cases.CaseFindPublicDTO;
import com.github.lianick.model.dto.cases.CaseQueneDTO;
import com.github.lianick.model.dto.cases.CaseVerifyDTO;
import com.github.lianick.model.dto.cases.CaseWithdrawnDTO;
import com.github.lianick.model.eneity.CaseOrganization;
import com.github.lianick.model.eneity.CasePriority;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.WithdrawalRequests;
import com.github.lianick.model.enums.ApplicationMethod;
import com.github.lianick.model.enums.CaseStatus;
import com.github.lianick.repository.CasesRepository;
import com.github.lianick.service.CaseOrganizationService;
import com.github.lianick.service.CasePriorityService;
import com.github.lianick.service.CaseService;
import com.github.lianick.service.WithdrawalRequestService;
import com.github.lianick.util.CaseNumberUtil;
import com.github.lianick.util.UserSecurityUtil;
import com.github.lianick.util.validate.CaseValidationUtil;
import com.github.lianick.util.validate.OrganizationValidationUtil;

@Service
@Transactional		// 確保 完整性 
public class CaseServiceImpl implements CaseService {

	@Autowired
	private CasesRepository casesRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	@Autowired
	private UserSecurityUtil userSecurityUtil;
	
	@Autowired
	private OrganizationValidationUtil organizationValidationUtil;
	
	@Autowired
	private CaseValidationUtil caseValidationUtil;

	@Autowired
	private CaseNumberUtil caseNumberUtil;
	
	@Autowired
	private CaseOrganizationService caseOrganizationService;
	
	@Autowired
	private CasePriorityService casePriorityService;
	
	@Autowired
	private WithdrawalRequestService withdrawalRequestService;
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public List<CaseDTO> findAllByPublic() {
		// 1. 檢查權限
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		Set<ChildInfo> childInfos = userPublic.getChildren();
		
		if (childInfos.isEmpty()) {
			return List.of();
		}
		
		// 2. 和 幼兒 有關係的案件們
		List<Cases> cases = casesRepository.findByChildInfoIn(childInfos);
		
		// 3. 轉成 DTO
		List<CaseDTO> caseDTOs = cases.stream()
									.map(oneCase -> {
										CaseDTO caseDTO = modelMapper.map(oneCase, CaseDTO.class);
										return caseDTO;
									}).toList();
		
		return caseDTOs;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public CaseDTO findByPublic(CaseFindPublicDTO caseFindPublicDTO) {
		// 0. 檢查完整性
		caseValidationUtil.validateFindPublic(caseFindPublicDTO);
		
		// 1. 檢查權限
		Cases cases = entityFetcher.getCasesById(caseFindPublicDTO.getId());
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		caseValidationUtil.validatePublicAndCase(userPublic, cases);
		
		// 2. 轉成 DTO 回傳
		return modelMapper.map(cases, CaseDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public CaseDTO createNewCase(CaseCreateDTO caseCreateDTO) {
		// 0. 檢查完整性 並取出 ApplicationMethod
		ApplicationMethod applicationMethod = caseValidationUtil.validateCreateFields(caseCreateDTO);
		
		// 1. 檢查權限
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		ChildInfo childInfo = entityFetcher.getChildInfoById(caseCreateDTO.getChildId());
		caseValidationUtil.validatePublicAndChildInfo(userPublic, childInfo);
		
		// 2. 建立 必要的資訊
		Organization organizationFirst = entityFetcher.getOrganizationById(caseCreateDTO.getOrganizationIdFirst());
		Organization organizationSecond = null;
		if (caseCreateDTO.getOrganizationIdSecond() != null) {
			organizationSecond = entityFetcher.getOrganizationById(caseCreateDTO.getOrganizationIdSecond());
		}
		LocalDateTime now = LocalDateTime.now();
		
		// 3. 建立 Cases 並儲存
		Cases cases = new Cases();
		
		cases.setCaseNumber(caseNumberUtil.generateNumber());
		cases.setApplicationMethod(applicationMethod);
		cases.setApplicationDate(now);
		cases.setChildInfo(childInfo);
		cases.setStatus(CaseStatus.APPLIED);
		
		cases = casesRepository.save(cases);
		
		// 4. 關聯表的建立
		CaseOrganization caseOrganizationFirst = caseOrganizationService.createOrganization(cases, organizationFirst, true);
		cases.getOrganizations().add(caseOrganizationFirst);
		if (organizationSecond != null) {
			CaseOrganization caseOrganizationSecond = caseOrganizationService.createOrganization(cases, organizationSecond, false);
			cases.getOrganizations().add(caseOrganizationSecond);
		}
		
		Set<CasePriority> priorities = casePriorityService.createPriorities(cases, caseCreateDTO.getPriorityIds());
		cases.setPriorities(priorities);
		
		// 5. 最終儲存
		cases = casesRepository.save(cases);
		
		// 6. 回傳
		return modelMapper.map(cases, CaseDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public WithdrawalRequestDTO withdrawnCase(CaseWithdrawnDTO caseWithdrawnDTO) {
		// 0. 檢查完整性
		caseValidationUtil.validateWithdrawnCase(caseWithdrawnDTO);
		
		// 1. 檢查權限 並檢查 案件狀態
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		Cases cases = entityFetcher.getCasesById(caseWithdrawnDTO.getId());
		ChildInfo childInfo = entityFetcher.getChildInfoById(cases.getChildInfo().getChildId());
		
		caseValidationUtil.validatePublicAndChildInfo(userPublic, childInfo);
		caseValidationUtil.validateCaseStatusInWithdrawnCase(cases);
		
		// 2. 嘗試建立新的 撤銷申請 並且存入資料庫
		WithdrawalRequests withdrawalRequests = withdrawalRequestService.createNewWithDrawalRequest(cases, caseWithdrawnDTO.getReason());
		
		// 3. 返回成功建立的 撤銷申請
		return modelMapper.map(withdrawalRequests, WithdrawalRequestDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public List<CaseDTO> findAllByAdmin(CaseFindAdminDTO caseFindAdminDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public CaseDTO findByAdmin(CaseFindAdminDTO caseFindAdminDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public CaseDTO verifyCase(CaseVerifyDTO caseVerifyDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public CaseDTO intoQueueCase(CaseQueneDTO caseQueneDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public CaseDTO intoClassCase(CaseClassDTO caseClassDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public void completedCase(CaseCompleteDTO caseCompleteDTO) {
		// TODO Auto-generated method stub
		
	}

}
