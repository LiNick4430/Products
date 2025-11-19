package com.github.lianick.util.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.OrganizationFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.organization.OrganizationCreateDTO;
import com.github.lianick.model.dto.organization.OrganizationUpdateDTO;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.OrganizationRepository;

/**
 * 負責處理 Organization 相關的 完整性 檢測
 * */
@Service
public class OrganizationValidationUtil {

	@Autowired
	private OrganizationRepository organizationRepository;
	
	/**
	 * OrganizationCreateDTO 的 完整性 與 唯一性
	 * */
	public void validateCreateFields(OrganizationCreateDTO organizationCreateDTO) {
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
	}
	
	/**
	 * OrganizationUpdateDTO 權限 的問題
	 * */
	public void validateUpdate(Users users, OrganizationUpdateDTO organizationUpdateDTO) {
		// 判斷是否為主管(最高權限)（角色名稱 = "ROLE_MANAGER"）
		boolean isManager = users.getRole().getName().equals("ROLE_MANAGER");

		// 非主管等級 要進一步判斷
		if (!isManager) {
			// 民眾帳號，無權執行此操作
			if (users.getAdminInfo() == null) {
				throw new AccessDeniedException("權限不足，您非管理層成員。");
			}
			// 員工帳號 但 所屬機構 不符 沒有對應權限
			if (users.getAdminInfo().getOrganization().getOrganizationId() != organizationUpdateDTO.getId()) {
				throw new AccessDeniedException("操作身份錯誤，您無權修改非所屬機構資料");
			}
		}
	}
}
