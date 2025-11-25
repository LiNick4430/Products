package com.github.lianick.service.impl;

import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.model.dto.cases.CaseClassDTO;
import com.github.lianick.model.dto.cases.CaseCompleteDTO;
import com.github.lianick.model.dto.cases.CaseCreateDTO;
import com.github.lianick.model.dto.cases.CaseDTO;
import com.github.lianick.model.dto.cases.CaseFindAdminDTO;
import com.github.lianick.model.dto.cases.CaseFindPublicDTO;
import com.github.lianick.model.dto.cases.CaseQueneDTO;
import com.github.lianick.model.dto.cases.CaseVerifyDTO;
import com.github.lianick.model.dto.cases.CaseWithdrawnDTO;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.repository.CasesRepository;
import com.github.lianick.service.CaseService;
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
		
		
		// 1. 檢查權限
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		
		return null;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public CaseDTO createNewCase(CaseCreateDTO caseCreateDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	public CaseDTO withdrawnCase(CaseWithdrawnDTO caseWithdrawnDTO) {
		// TODO Auto-generated method stub
		return null;
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
