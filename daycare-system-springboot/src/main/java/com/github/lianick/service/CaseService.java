package com.github.lianick.service;

import java.util.List;

import com.github.lianick.model.dto.WithdrawalRequestDTO;
import com.github.lianick.model.dto.cases.CaseClassDTO;
import com.github.lianick.model.dto.cases.CaseCompleteDTO;
import com.github.lianick.model.dto.cases.CaseCreateDTO;
import com.github.lianick.model.dto.cases.CaseDTO;
import com.github.lianick.model.dto.cases.CaseErrorDTO;
import com.github.lianick.model.dto.cases.CaseFindAdminDTO;
import com.github.lianick.model.dto.cases.CaseFindPublicDTO;
import com.github.lianick.model.dto.cases.CaseLotteryResultDTO;
import com.github.lianick.model.dto.cases.CasePendingDTO;
import com.github.lianick.model.dto.cases.CaseQueneDTO;
import com.github.lianick.model.dto.cases.CaseRejectDTO;
import com.github.lianick.model.dto.cases.CaseVerifyDTO;
import com.github.lianick.model.dto.cases.CaseWaitlistDTO;
import com.github.lianick.model.dto.cases.CaseWithdrawnAdminDTO;
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
	
	/** 撤回 (自己)案件 (建立一個 撤銷請求)
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')") 
	 * */
	WithdrawalRequestDTO withdrawnCase(CaseWithdrawnDTO caseWithdrawnDTO);
	
	// 員工 專用
	/** 搜尋 全部 案件
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")  
	 * */
	List<CaseDTO> findAllByAdmin(CaseFindAdminDTO caseFindAdminDTO);
	
	/** 搜尋 特定 案件
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")  
	 * */
	CaseDTO findByAdmin(CaseFindAdminDTO caseFindAdminDTO);
	
	/** 審核 案件 (APPLIED -> PASSED/REJECTED)
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	CaseDTO verifyCase(CaseVerifyDTO caseVerifyDTO);
	
	/**
	 * 將 通過的案件 進入 待分發 狀態
	 * 案件 (PASSED -> PENDING)
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')") 
	 * */
	List<CaseErrorDTO> advanceToPending(List<CasePendingDTO>  casePendingDTOs);
	
	/** 取出 PENDING 案件 (1ST 或者 1st失敗後 2nd 再跑一次)
	 * 機構審核 CaseOrganization = APPLIED -> PASSED or REJECTED
	 * 假設 CaseOrganization = PASSED		->  建立 LotteryQueue = QUEUED
	 * 假設 CaseOrganization = REJECTED	->  進入下一個 CaseOrganization 或者 PASSED -> REJECTED
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	CaseDTO intoQueueCase(CaseQueneDTO caseQueneDTO);
	
	/**
	 * 取出 LotteryQueue = QUEUED 的 柱列們
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	List<CaseDTO> findQueuedCases();
	
	/**
	 * 處理案件的抽籤結果 (批量操作)。
	 * * 抽籤後:
	 * 1. 抽籤成功 => CaseOrganization = PASSED, LotteryQueue = SELECTED
	 * 2. 抽籤備選 => CaseOrganization = WAITLISTED, LotteryQueue = SELECTED
	 * 3. 抽籤失敗 => CaseOrganization = REJECTED, LotteryQueue = FAILED
	 * * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')")
	 * */
	void processLotteryResults(List<CaseLotteryResultDTO> lotteryResults);
	
	/** 抽籤成功 => CaseOrganization = PASSED, LotteryQueue = SELECTED
	 * 	CASE(PENDING -> ALLOCATED) 進入 有空位的班級
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	void intoClassCase(List<CaseClassDTO> caseClassDTOs);
	
	/** 幼兒 向 班級 報到 (ALLOCATED -> COMPLETED) 
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	void completedCase(List<CaseCompleteDTO>  caseCompleteDTOs);
	
	/**
	 * 機構 的 班級 還有 空位 的時候(報到期限後)
	 * 將 申請該機構的 抽籤備選 => CaseOrganization = WAITLISTED, LotteryQueue = SELECTED 
	 * 變成 CaseOrganization = PASSED
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	void processWaitlistSuccess(List<CaseWaitlistDTO> waitlistDTOs);
	
	/**
	 * 機構 的 班級 無 空位 的時候(報到期限後)
	 * 將 申請該機構的 抽籤備選 => CaseOrganization = WAITLISTED, LotteryQueue = SELECTED 
	 * 變成 CaseOrganization = REJECTED
	 * (假設是 1st 讓 2nd 讓進入 intoQueueCase() 跑流程)
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	void processWaitlistFailure(List<CaseWaitlistDTO> waitlistDTOs);
	
	/**
	 * 檢查 是否 流程失敗 直接把 CASE 進入 REJECTED
	 * 1. CaseOrganizationFirst = REJECTED
	 * 2. CaseOrganizationSecond = null or REJECTED
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER')")
	 * */
	void intoRejected(List<CaseRejectDTO> caseRejectDTOs);
	
	/**
	 * 尋找 所有 撤銷申請 的 案件 (狀態 是 APPLIED 的)
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	List<WithdrawalRequestDTO> findAllWithdrawalRequest();
	
	/** 
	 * 審核 撤銷申請 案件
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	 * */
	void verifyWithdrawnCase(List<CaseWithdrawnAdminDTO> caseWithdrawnAdminDTOs);
}
