package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.model.dto.regulation.RegulationCreateDTO;
import com.github.lianick.model.dto.regulation.RegulationDTO;
import com.github.lianick.model.dto.regulation.RegulationDeleteDTO;
import com.github.lianick.model.dto.regulation.RegulationFindDTO;
import com.github.lianick.model.dto.regulation.RegulationUpdateDTO;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Regulations;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.model.enums.RegulationType;
import com.github.lianick.repository.RegulationsRepository;
import com.github.lianick.service.RegulationService;
import com.github.lianick.util.UserSecurityUtil;
import com.github.lianick.util.validate.OrganizationValidationUtil;
import com.github.lianick.util.validate.RegulationValidationUtil;

@Service
@Transactional				// 確保 完整性 
public class RegulationServiceImpl implements RegulationService{

	@Autowired
	private RegulationsRepository regulationsRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	@Autowired
	private UserSecurityUtil userSecurityUtil;
	
	@Autowired
	private RegulationValidationUtil regulationValidationUtil;
	
	@Autowired
	private OrganizationValidationUtil organizationValidationUtil;
	
	@Override
	public List<RegulationDTO> findAll(RegulationFindDTO regulationFindDTO) {
		// 0. 檢測完整性
		regulationValidationUtil.validateFindByOrganization(regulationFindDTO);
		
		// 1. 尋找
		Organization organization = entityFetcher.getOrganizationById(regulationFindDTO.getOrganizationId());
		List<Regulations> regulations = regulationsRepository.findAllByOrganization(organization);
		
		// 2. 轉成 DTO
		return regulations.stream()
					.map(regulation -> {
						RegulationDTO regulationDTO = modelMapper.map(regulation, RegulationDTO.class);
						return regulationDTO;
					}).toList();
	}

	@Override
	public RegulationDTO findByRegulationId(RegulationFindDTO regulationFindDTO) {
		// 0. 檢測完整性
		regulationValidationUtil.validateFindById(regulationFindDTO);
		
		// 1. 尋找
		Regulations regulations = entityFetcher.getRegulationsById(regulationFindDTO.getId());
		
		// 2. 轉成 DTO
		return modelMapper.map(regulations, RegulationDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public RegulationDTO createRegulation(RegulationCreateDTO regulationCreateDTO) {
		// 0. 檢測完整性 並取出 RegulationType
		RegulationType type = regulationValidationUtil.validateCreateFields(regulationCreateDTO);
		
		// 1. 是否有權限
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		Organization organization = entityFetcher.getOrganizationById(regulationCreateDTO.getOrganizationId());
		Long organizationId = organization.getOrganizationId();
		organizationValidationUtil.validateOrganizationPermission(tableUser, organizationId);
		
		// 2. 判斷是否有 同機構 同type 且 尚未刪除 的 Regulation
		regulationValidationUtil.validateOrganizationAndRegulationType(organization, type);
		
		// 3. 尋找是否有 同機構 同type 但是 已經被軟刪除 的 Regulation
		Optional<Regulations> optRegulation = regulationsRepository.findByOrganizationAndTypeAndIsDelete(organizationId, type.name());
		
		// 4. 建立 Regulations 並儲存
		Regulations regulations = null;
		
		if (optRegulation.isPresent()) {
			// 假設存在 就拿以前的ID出來 使用
			regulations = optRegulation.get();
			regulations.setDeleteAt(null);
		} else {
			// 不存在 就建立新的
			regulations = new Regulations();
			regulations.setOrganization(organization);
			regulations.setType(type);
		}
		
		regulations.setContent(regulationCreateDTO.getContent());
		regulations = regulationsRepository.save(regulations);
		
		// 5. 轉成 DTO
		return modelMapper.map(regulations, RegulationDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public RegulationDTO updateRegulation(RegulationUpdateDTO regulationUpdateDTO) {
		// 0. 檢測完整
		regulationValidationUtil.validateUpdate(regulationUpdateDTO);
		
		// 1. 是否有權限
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		Regulations regulations = entityFetcher.getRegulationsById(regulationUpdateDTO.getId());
		Long organizationId = regulations.getOrganization().getOrganizationId();
		organizationValidationUtil.validateOrganizationPermission(tableUser, organizationId);
		
		// 2. 更新內文 並 回存
		regulations.setContent(regulationUpdateDTO.getNewContent());
		regulations = regulationsRepository.save(regulations);
		
		// 3. 轉成 DTO
		return modelMapper.map(regulations, RegulationDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public void deleteRegulation(RegulationDeleteDTO regulationDeleteDTO) {
		// 0. 檢測完整性
		regulationValidationUtil.validateDelete(regulationDeleteDTO);
		
		// 1. 是否有權限
		Users tableUser = userSecurityUtil.getCurrentUserEntity();
		Regulations regulations = entityFetcher.getRegulationsById(regulationDeleteDTO.getId());
		Long organizationId = regulations.getOrganization().getOrganizationId();
		organizationValidationUtil.validateOrganizationPermission(tableUser, organizationId);
		
		// 2. 執行 軟刪除
		LocalDateTime now = LocalDateTime.now();
		regulations.setDeleteAt(now);
		
		// 3. 回存
		regulationsRepository.save(regulations);
	}
}
