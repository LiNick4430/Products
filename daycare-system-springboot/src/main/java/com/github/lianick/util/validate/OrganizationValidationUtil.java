package com.github.lianick.util.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.exception.FileStorageException;
import com.github.lianick.exception.OrganizationFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.organization.OrganizationCreateDTO;
import com.github.lianick.model.dto.organization.OrganizationDocumentDTO;
import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.OrganizationRepository;

/**
 * 負責處理 Organization 相關的 完整性 檢測
 * */
@Service
public class OrganizationValidationUtil {

	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private UserValidationUtil userValidationUtil;
	
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
	 * OrganizationDocumentDTO 的 完整性
	 * @param isUpload = true 需要 OrganizationId(id) + file, false 需要 OrganizationId(id) + DoucmnetId
	 * */
	public void validateDocument(OrganizationDocumentDTO organizationDocumentDTO, MultipartFile file, Boolean isUpload) {
		if (isUpload) {
			if (organizationDocumentDTO.getId() == null) {
				throw new ValueMissException("缺少必要的機構上傳資料 (機構ID)");
			}
			if (file.isEmpty()) {
				throw new FileStorageException("檔案錯誤：上傳檔案不存在");
			}
		} else {
			if (organizationDocumentDTO.getId() == null || organizationDocumentDTO.getDocumentId() == null) {
				throw new ValueMissException("缺少必要的機構下載資料 (機構ID, 附件ID)");
			}
		}
	}
	
	/**
	 * Users 的 Organization 權限 的問題
	 * */
	public void validateOrganizationPermission(Users users, Long organizationId) {
		// 基本檢查
	    if (organizationId == null) {
	        throw new ValueMissException("缺少必要的判斷資料 (機構ID)。");
	    }
	    if (users == null || users.getRole() == null) {
	        throw new AccessDeniedException("權限不足，無法獲取用戶角色資訊。");
	    }
		
		// 判斷是否為主管(最高權限)（角色名稱 = "ROLE_MANAGER"）
		boolean isManager = userValidationUtil.validateUserIsManager(users);

		// 非主管等級 要進一步判斷
		if (!isManager) {
			// 民眾帳號，無權執行此操作
			if (users.getAdminInfo() == null) {
				throw new AccessDeniedException("權限不足，您非管理層成員。");
			}
			// 員工帳號 但 所屬機構 不符 沒有對應權限
			if (!users.getAdminInfo().getOrganization().getOrganizationId().equals(organizationId)) {
				throw new AccessDeniedException("操作身份錯誤，您無權修改非所屬機構資料");
			}
		}
	}
	
	/**
	 * Organization 和 class 是否有關連
	 * */
	public void validateOrganizationAndClass(Organization organization, Classes classes) {
		if (!classes.getOrganization().getOrganizationId().equals(organization.getOrganizationId())) {
			throw new OrganizationFailureException("並非機構關聯班級");
		}
	}
}
