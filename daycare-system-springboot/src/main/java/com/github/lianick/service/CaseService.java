package com.github.lianick.service;

import java.util.List;

import com.github.lianick.model.dto.cases.CaseClassDTO;
import com.github.lianick.model.dto.cases.CaseCompleteDTO;
import com.github.lianick.model.dto.cases.CaseCreateDTO;
import com.github.lianick.model.dto.cases.CaseDTO;
import com.github.lianick.model.dto.cases.CaseFindAdminDTO;
import com.github.lianick.model.dto.cases.CaseFindPublicDTO;
import com.github.lianick.model.dto.cases.CaseQueneDTO;
import com.github.lianick.model.dto.cases.CaseVerifyDTO;
import com.github.lianick.model.dto.cases.CaseWithdrawnDTO;

/** 案件的狀態順序
 * 1. 申請中		-> CaseStatus.APPLIED
 * 成功流程
 * 2. 通過		-> CaseStatus.PASSED
 * 3. 待分發		-> CaseStatus.PENDING
 * 4. 已分發		-> CaseStatus.ALLOCATED
 * 5. 報到/結案	-> CaseStatus.COMPLETED
 * 失敗流程
 * 2. 退件		-> CaseStatus.REJECTED
 * 自願撤銷流程
 * 2. 撤回		-> CaseStatus.WITHDRAWN
 * 
 * 民眾 申請案件
 * 基層 審核案件 -> 通過
 * 管理員 將 通過的 進入 各機構的 代抽籤區
 * 管理員 抽籤後 將抽到的 進入 各機構的 班級
 * 民眾報到後 基層 把 案件 變成 完成
 * 
 * 民眾撤回的申請 還需要幾個工作天審核
 *  */

public interface CaseService {

	// 民眾 專用
	/** 搜尋 全部 (自己)案件 
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	 * */
	List<CaseDTO> findAllByPublic();
	
	/** 搜尋 特定 (自己)案件 
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	 * */
	CaseDTO findByPublic(CaseFindPublicDTO caseFindPublicDTO);
	
	/** 建立 案件 (create ->  申請中)
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	 * */
	CaseDTO createNewCase(CaseCreateDTO caseCreateDTO);
	
	/** 撤回 (自己)案件 (申請中 -> 撤回)
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	 * */
	CaseDTO withdrawnCase(CaseWithdrawnDTO caseWithdrawnDTO);
	
	// 員工 專用
	/** 搜尋 全部 案件
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")  
	 * */
	List<CaseDTO> findAllByAdmin(CaseFindAdminDTO caseFindAdminDTO);
	
	/** 搜尋 特定 案件
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")  
	 * */
	CaseDTO findByAdmin(CaseFindAdminDTO caseFindAdminDTO);
	
	/** 審核 案件 (申請中 -> 通過/退件)
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	CaseDTO verifyCase(CaseVerifyDTO caseVerifyDTO);
	
	/** 進入 候補/抽籤 序列 等候 (通過 -> 待分發)
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')")
	 * */
	CaseDTO intoQueueCase(CaseQueneDTO caseQueneDTO);
	
	/** 被抽到後 班級 (待分發 -> 已分發)
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')")
	 * */
	CaseDTO intoClassCase(CaseClassDTO caseClassDTO);
	
	/** 幼兒 向 班級 報到 (已分發 -> 結案) 
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	void completedCase(CaseCompleteDTO caseCompleteDTO);
	
	
}
