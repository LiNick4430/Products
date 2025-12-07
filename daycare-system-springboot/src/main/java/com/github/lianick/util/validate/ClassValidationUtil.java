package com.github.lianick.util.validate;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.ClassesFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.clazz.ClassCreateDTO;
import com.github.lianick.model.dto.clazz.ClassDeleteDTO;
import com.github.lianick.model.dto.clazz.ClassFindDTO;
import com.github.lianick.model.dto.clazz.ClassLinkCaseDTO;
import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.Users;

/**
 * 負責處理 Class 相關的 完整性 檢測
 * */
@Service
public class ClassValidationUtil {

	/**
	 * 檢查 ClassFindDTO 的 完整性 
	 * */
	public void validateFindByPublic(ClassFindDTO classFindDTO) {
		if (classFindDTO.getOrganizationName() == null || classFindDTO.getOrganizationName().isBlank()) {
			throw new ValueMissException("缺少必要的搜尋資料：機構名稱");
		}
	}
	
	/**
	 * 檢查 ClassFindDTO 的 完整性 
	 * */
	public void validateFindByAdmin(ClassFindDTO classFindDTO) {
		if (classFindDTO.getOrganizationId() == null ) {
			throw new ValueMissException("缺少必要的搜尋資料：機構 ID");
		}
	}
	
	/**
	 * 檢查 ClassCreateDTO 的 完整性
	 * */
	public void validateCreateFields(ClassCreateDTO classCreateDTO) {
		if (classCreateDTO.getName() == null || classCreateDTO.getName().isBlank() ||
				classCreateDTO.getMaxCapacity() == null || 
				classCreateDTO.getAgeMinMonths() == null || 
				classCreateDTO.getAgeMaxMonths() == null || 
				classCreateDTO.getServiceStartMonth() == null || 
				classCreateDTO.getServiceEndMonth() == null || 
				classCreateDTO.getOrganizationId() == null
				) {
			throw new ValueMissException("缺少必要的班級建立資料 (名稱、機構ID、最大人數、公托年齡、服務月份)");
		}
	}
	
	/**
	 * 檢查 ClassLinkCaseDTO 的 完整性
	 * */
	public void validateLinkCase(ClassLinkCaseDTO classLinkCaseDTO) {
		if (classLinkCaseDTO.getId() == null || classLinkCaseDTO.getCaseId() == null ||
				classLinkCaseDTO.getOrganizationId() == null ) {
			throw new ValueMissException("缺少必要的班級連接案件資料 (班級ID、更新人數、機構ID)");
		}
	}
	
	/**
	 * 檢查 ClassDeleteDTO 的 完整性
	 * */
	public void validateDelete(ClassDeleteDTO classDeleteDTO) {
		if (classDeleteDTO.getId() == null || classDeleteDTO.getOrganizationId() == null ||
				classDeleteDTO.getName() == null || classDeleteDTO.getName().isBlank()) {
			throw new ValueMissException("缺少必要的班級刪除資料 (班級ID、班級名稱、機構ID)");
		}
	}
	
	/**
	 * 檢查 該班級是否能再接受一個學生（即空位 > 0）。
	 * */
	public void validateClassCanAcceptOneMore(Classes classes) {
		if ((classes.getCurrentCount() >= classes.getMaxCapacity())) {
			throw new ClassesFailureException("該班級 沒有空位");
		}
	}
	
	/**
	 * RoleNumber 的 權限 (建立 修改 刪除)
	 * */
	public void validateRoleNumber(Long roleNumber,Users users, Long organizationId, String type) {
		if (roleNumber.equals(2L)) {
			// 基層員工 檢查是否為 自己的機構 底下 班級
			Long tableUserOrganizationId = users.getAdminInfo().getOrganization().getOrganizationId();
			if (!tableUserOrganizationId.equals(organizationId)) {
				throw new AccessDeniedException("權限不足: 無法" + type + "其他機構的班級");
			}
		} else if (roleNumber.equals(3L)) {
			// 管理員 直接通過
		} else {
			// 預防萬一 理論上大部分都會被 @PreAuthorize 擋住
			throw new AccessDeniedException("權限不足: 無法" + type + "其他機構的班級");
		}
	}
}
