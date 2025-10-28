package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.OrganizationFailureException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.organization.OrganizationCreateDTO;
import com.github.lianick.model.dto.organization.OrganizationDTO;
import com.github.lianick.model.dto.organization.OrganizationDeleteDTO;
import com.github.lianick.model.dto.organization.OrganizationFindDTO;
import com.github.lianick.model.dto.organization.OrganizationUpdateDTO;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.OrganizationRepository;
import com.github.lianick.repository.UserAdminRepository;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.service.OrganizationService;
import com.github.lianick.service.UserService;
import com.github.lianick.util.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
@Transactional				// 確保 完整性 
public class OrganizationServiceImpl implements OrganizationService{

	@Autowired
	private UserService userService;
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UserAdminRepository userAdminRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public List<OrganizationDTO> findOrganization(OrganizationFindDTO organizationFindDTO) {
		// 0. 提取 + 預處理資料
		String name = organizationFindDTO.getName();
		String address = organizationFindDTO.getAddress();
		
		// 檢查是否所有查詢條件都為空或空白
		boolean isNamePresent = name != null && !name.isBlank();
		boolean isAddressPresent = address != null && !address.isBlank();
		
		if (!isNamePresent && !isAddressPresent) {
			// 如果沒有任何查詢條件，返回空列表
			return List.of();
		}
		
		// 1. 為 LIKE 查詢添加通配符
		String nameLike = isNamePresent ? "%" + name + "%" : null;
		String addressLike = isAddressPresent ? "%" + address + "%" : null;
		
		// 2. 執行 Repository 查詢
		List<Organization> finaList;
		
		if (nameLike != null && addressLike != null) {
			finaList = organizationRepository.findByNameAndAddressLike(nameLike, addressLike);
		} else if (nameLike != null) {
			finaList = organizationRepository.findByNameLike(nameLike);
		} else {	// 只剩 address != null
			finaList = organizationRepository.findByAddressLike(addressLike);
		}
		
		// 3. Entity -> DTO 轉換
		if (finaList.isEmpty()) {
			// 如果任何查詢結果，返回空列表
			return List.of();
		}
		
		return finaList.stream()
					.map(organization -> modelMapper.map(organization, OrganizationDTO.class))
					.toList();
	}

	@Override
	public OrganizationDTO createOrganization(OrganizationCreateDTO organizationCreateDTO) {
		// 0. 檢查資料完整性
		if (organizationCreateDTO.getName() == null || organizationCreateDTO.getName().isBlank() ||
				organizationCreateDTO.getAddress() == null || organizationCreateDTO.getAddress().isBlank() ||
				organizationCreateDTO.getPhoneNumber() == null || organizationCreateDTO.getPhoneNumber().isBlank() ||
				organizationCreateDTO.getEmail() == null || organizationCreateDTO.getEmail().isBlank() ) {
			// 敘述 和 傳真號碼 不是必要
			throw new ValueMissException("缺少必要的機構建立資料 (機構名稱、地址、電話號碼、電子信箱)");
		}
		if (organizationRepository.findByName(organizationCreateDTO.getName()).isPresent()) {
			throw new OrganizationFailureException("建立錯誤:機構名稱 不可以重複");
		}
		if (organizationRepository.findByPhoneNumber(organizationCreateDTO.getPhoneNumber()).isPresent()) {
			throw new OrganizationFailureException("建立錯誤:機構電話 不可以重複");
		}
		if (organizationRepository.findByEmail(organizationCreateDTO.getEmail()).isPresent()) {
			throw new OrganizationFailureException("建立錯誤:機構信箱 不可以重複");
		}
		
		// 1. 建立機構
		Organization organization = modelMapper.map(organizationCreateDTO, Organization.class);
		
		// 2. 儲存
		organization = organizationRepository.save(organization);
		
		// 3. Entity -> DTO
		OrganizationDTO organizationDTO = modelMapper.map(organization, OrganizationDTO.class);
		
		return organizationDTO;
	}

	@Override
	public OrganizationDTO updateOrganization(OrganizationUpdateDTO organizationUpdateDTO) {
		// 1. 取出機構基本資料
		Long currentId = organizationUpdateDTO.getId();
		
		Organization organization = organizationRepository.findById(organizationUpdateDTO.getId())
				.orElseThrow(() -> new OrganizationFailureException("查無機構"));
		
		// 2. 根據 是否有數值 來更新
		// Name
		if (organizationUpdateDTO.getNewName() != null && !organizationUpdateDTO.getNewName().isBlank()) {
			if (organizationRepository.findByNameAndIdNot(organizationUpdateDTO.getNewName(), currentId).isPresent()) {
				throw new OrganizationFailureException("修改錯誤:機構名稱 不可以重複");
			}
			organization.setName(organizationUpdateDTO.getNewName());
		}
		// Description
		if (organizationUpdateDTO.getNewDescription() != null && !organizationUpdateDTO.getNewDescription().isBlank()) {
			organization.setDescription(organizationUpdateDTO.getNewDescription());
		}
		// Address
		if (organizationUpdateDTO.getNewAddress() != null && !organizationUpdateDTO.getNewAddress().isBlank()) {
			organization.setAddress(organizationUpdateDTO.getNewAddress());
		}
		// PhoneNumber
		if (organizationUpdateDTO.getNewPhoneNumber() != null && !organizationUpdateDTO.getNewPhoneNumber().isBlank()) {
			if (organizationRepository.findByPhoneNumberAndIdNot(organizationUpdateDTO.getNewPhoneNumber(), currentId).isPresent()) {
				throw new OrganizationFailureException("修改錯誤:機構電話 不可以重複");
			}
			organization.setPhoneNumber(organizationUpdateDTO.getNewPhoneNumber());
		}
		// Email
		if (organizationUpdateDTO.getNewEmail() != null && !organizationUpdateDTO.getNewEmail().isBlank()) {
			if (organizationRepository.findByEmailAndIdNot(organizationUpdateDTO.getNewEmail(), currentId).isPresent()) {
				throw new OrganizationFailureException("修改錯誤:機構信箱 不可以重複");
			}
			organization.setEmail(organizationUpdateDTO.getNewEmail());
		}
		// Fax
		if (organizationUpdateDTO.getNewFax() != null && !organizationUpdateDTO.getNewFax().isBlank()) {
			organization.setFax(organizationUpdateDTO.getNewFax());
		}
		
		// 3. 回存
		organization = organizationRepository.save(organization);
		
		// 4. Entity -> DTO
		return modelMapper.map(organization, OrganizationDTO.class);
	}

	@Override
	public void deleteOrganization(OrganizationDeleteDTO organizationDeleteDTO) {
		// 1. 檢查是否本人
		String currentName = SecurityUtils.getCurrentUsername();
		if (!currentName.equals(organizationDeleteDTO.getUsername())) {
			throw new AccessDeniedException("操作身份錯誤，您無權以該帳號執行此操作");
		}
		Users users = usersRepository.findByAccount(currentName)
				.orElseThrow(() -> new UserNoFoundException("帳號或密碼錯誤"));
		if (!userService.checkPassword(organizationDeleteDTO, users)) {
			throw new UserNoFoundException("帳號或密碼錯誤");
		}
		
		// 2. 檢查 是否有 機構 和 機構 相關的 員工
		Organization organization = organizationRepository.findById(organizationDeleteDTO.getId())
				.orElseThrow(() -> new OrganizationFailureException("查無機構"));
		if (!userAdminRepository.findByOrganization(organization).isEmpty()) {
			throw new OrganizationFailureException("此機構 還有 員工 無法刪除");
		}
		
		// 3. 執行軟刪除 + 回存
		LocalDateTime now = LocalDateTime.now();
		organization.setDeleteAt(now);
		organization = organizationRepository.save(organization);
	}
}
