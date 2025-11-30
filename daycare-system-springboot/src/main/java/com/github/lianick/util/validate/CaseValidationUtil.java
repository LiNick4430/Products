package com.github.lianick.util.validate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.lianick.exception.CaseFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.cases.CaseCreateDTO;
import com.github.lianick.model.dto.cases.CaseFindAdminDTO;
import com.github.lianick.model.dto.cases.CaseFindPublicDTO;
import com.github.lianick.model.dto.cases.CasePendingDTO;
import com.github.lianick.model.dto.cases.CaseQueueDTO;
import com.github.lianick.model.dto.cases.CaseVerifyDTO;
import com.github.lianick.model.dto.cases.CaseWithdrawnDTO;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.model.enums.ApplicationMethod;
import com.github.lianick.model.enums.CaseOrganizationStatus;
import com.github.lianick.model.enums.CaseStatus;

/**
 * 負責處理 Case 相關的 完整性 檢測
 * */
@Service
public class CaseValidationUtil {
	
	@Autowired
	private UserValidationUtil userValidationUtil;
	
	/**
	 * CaseFindPublicDTO 的完整性
	 * */
	public void validateFindPublic(CaseFindPublicDTO caseFindPublicDTO) {
		if (caseFindPublicDTO.getId() == null) {
			throw new ValueMissException("缺少特定資料(案件ID)");
		}
	}
	
	/**
	 * 判斷 UserPublic 是否有權限 觀看這個案件
	 * */
	public void validatePublicAndCase(UserPublic userPublic, Cases cases) {
		if (userPublic == null || cases == null) {
			throw new ValueMissException("缺少必要資訊(使用者, 案件)");
		}
		if (!cases.getChildInfo().getUserPublic().equals(userPublic)) {
			throw new CaseFailureException("您沒有權限查找這個案件");
		}
	}
	
	/**
	 * CaseCreateDTO 的 完整性 + 並取出 AapplicationMethod
	 * */
	public ApplicationMethod validateCreateFields(CaseCreateDTO caseCreateDTO) {
		if (caseCreateDTO.getApplicationMethod() == null || caseCreateDTO.getApplicationMethod().isBlank() ||
				caseCreateDTO.getChildId() == null || caseCreateDTO.getOrganizationIdFirst() == null
				) {
			throw new ValueMissException("缺少必要資訊(申請方式, 幼兒ID, 第一志願機構ID)");
		}
		
		try {
			return ApplicationMethod.formCode(caseCreateDTO.getApplicationMethod());
		} catch (IllegalArgumentException e) {
			throw new CaseFailureException("申請方式 類型錯誤");
		}
	}
	
	/**
	 * 判斷 UserPublic ChildInfo 是否相關聯
	 * */
	public void validatePublicAndChildInfo(UserPublic userPublic, ChildInfo childInfo) {
		if (userPublic == null || childInfo == null) {
			throw new ValueMissException("缺少必要資訊(使用者, 幼兒)");
		}
		if (!childInfo.getUserPublic().equals(userPublic)) {
			throw new CaseFailureException("您沒有權限建立此案件");
		}
	}
	
	/**
	 * CaseWithdrawnDTO 的完整性
	 * */
	public void validateWithdrawnCase(CaseWithdrawnDTO caseWithdrawnDTO) {
		if (caseWithdrawnDTO.getId() == null || 
				caseWithdrawnDTO.getReason() == null || caseWithdrawnDTO.getReason().isBlank()
				) {
			
			throw new ValueMissException("缺少特定資料(案件ID, 撤銷原因)");
		}
	}
	
	/**
	 * 檢查 CASE 的 狀態 是否 符合 withdrawnCase
	 * */
	public void validateCaseStatusInWithdrawnCase(Cases cases) {
		CaseStatus caseStatus = cases.getStatus();
		
		if (caseStatus.equals(CaseStatus.WITHDRAWN)) {
			throw new CaseFailureException("案件 已經被 撤銷");
		}
		if (caseStatus.equals(CaseStatus.REJECTED)) {
			throw new CaseFailureException("案件 已經退件 無法撤銷");
		}
		if (caseStatus.equals(CaseStatus.COMPLETED)) {
			throw new CaseFailureException("案件 已經結案 無法撤銷");
		}
	}
	
	/**
	 * 大量搜尋時 檢查 CaseFindAdminDTO 的 完整性 並取出 狀態
	 * */
	public CaseStatus validateFindAdminAll(CaseFindAdminDTO caseFindAdminDTO) {
		if (caseFindAdminDTO.getCaseStatus() == null || caseFindAdminDTO.getCaseStatus().isBlank()) {
			throw new ValueMissException("缺少必要資訊(案件狀態)");
		}
		
		try {
			return CaseStatus.fromCode(caseFindAdminDTO.getCaseStatus());
		} catch (IllegalArgumentException e) {
			throw new CaseFailureException("案件狀態 類型錯誤");
		}
	}
	
	/**
	 * 單一搜尋時 檢查 CaseFindAdminDTO 的 完整性
	 * */
	public void validateFindAdminOne(CaseFindAdminDTO caseFindAdminDTO) {
		if (caseFindAdminDTO.getId() == null) {
			throw new ValueMissException("缺少必要資訊(案件ID)");
		}
	}
	
	/**
	 * 檢查 CaseVerifyDTO 的 完整性，並返回合法的 CaseStatus (PASSED 或 REJECTED)。
	 * */
	public CaseStatus validateCaseVerify(CaseVerifyDTO caseVerifyDTO) {
		// 檢查空值
		if (caseVerifyDTO.getId() == null || 
				caseVerifyDTO.getNewStatus() == null || caseVerifyDTO.getNewStatus().isBlank()) {
			throw new ValueMissException("缺少必要資訊(案件ID, 案件新狀態)");
		}
		
		// 取出狀態
		CaseStatus status;
		try {
			status = CaseStatus.fromCode(caseVerifyDTO.getNewStatus());
		} catch (IllegalArgumentException e) {
			throw new CaseFailureException("案件狀態 類型錯誤");
		}
		
		// 看看 是不是 合法的 新狀態
		if (status == CaseStatus.PASSED || status == CaseStatus.REJECTED) {
			return status;
		} 
		
		throw new CaseFailureException("案件狀態 類型錯誤(必須是 通過 或者 拒絕)");
	}
	
	/**
	 * CaseQueneDTO 的完整性 並且 取出 CaseOrganizationStatus (PASSED or REJECTED)
	 * */
	public CaseOrganizationStatus validateCaseQuene(CaseQueueDTO caseQueneDTO) {
		if (caseQueneDTO.getId() == null || caseQueneDTO.getOrganizationId() == null || 
				caseQueneDTO.getStatus() == null) {
			throw new ValueMissException("缺少必要資訊(案件ID, 機構ID, 關聯新狀態)");
		}
		
		CaseOrganizationStatus status;
		try {
			status = CaseOrganizationStatus.fromCode(caseQueneDTO.getStatus());
		} catch (IllegalArgumentException e) {
			throw new CaseFailureException("關聯狀態 類型錯誤");
		}
		
		if (status == CaseOrganizationStatus.PASSED || status == CaseOrganizationStatus.REJECTED) {
			return status;
		}
		
		throw new CaseFailureException("關聯狀態 類型錯誤(必須是 通過 或者 未通過)");
	}
	
	/**
	 * 檢查 CasePendingDTO 的完整性
	 * */
	public void validateCasePending(CasePendingDTO casePendingDTO) {
		if (casePendingDTO.getId() == null) {
			throw new CaseFailureException("缺少必要資訊(案件ID)");
		}
	}
	
	/**
	 * 檢查 員工 是否可以控制 此案件
	 * */
	public void validateUserAndCase(Users user, Cases cases) {
		// 1. 假設是管理者 直接通過
		if (userValidationUtil.validateUserIsManager(user)) {
			return;
		}
		
		// 2. 取得使用者 的 organization
		UserAdmin userAdmin = user.getAdminInfo();
		if (userAdmin == null || userAdmin.getOrganization() == null) {
			throw new CaseFailureException("使用者缺少所屬機構資訊");
		}
		Organization userOrganization = userAdmin.getOrganization();
		
		// 3. 比對 案件 的 關聯機構 是不是 有關
		boolean matched = cases.getOrganizations().stream()
				.map(co -> co.getOrganization().getOrganizationId())
				.anyMatch(id -> id.equals(userOrganization.getOrganizationId()));
		
		if (!matched) {
			throw new CaseFailureException("您沒有權限處理/操作此案件，它不屬於您的管轄機構。");
		}
	}
	
	/**
	 * 判斷 原本的案件 是不是 特定的狀態
	 * */
	public void validateCaseStatus(Cases cases, CaseStatus targetStatus) {
		CaseStatus caseStatus = cases.getStatus();
		if (caseStatus == null || caseStatus != targetStatus) {
			throw new CaseFailureException(
					String.format("案件狀態錯誤：目前=%s, 預期=%s。", caseStatus, targetStatus)
			);
		}
	}
	
	/**
	 * 判斷 是否有權限 做大量操作
	 * */
	public void validateUserCanBatchProcess(Users users) {
		if (users == null || !userValidationUtil.validateUserIsManager(users)) {
			throw new CaseFailureException("您沒有權限處理大量案件。");
		}
	}
}
