package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.CaseFailureException;
import com.github.lianick.exception.ClassesFailureException;
import com.github.lianick.model.dto.WithdrawalRequestDTO;
import com.github.lianick.model.dto.cases.CaseAllocationDTO;
import com.github.lianick.model.dto.cases.CaseCompleteDTO;
import com.github.lianick.model.dto.cases.CaseCreateDTO;
import com.github.lianick.model.dto.cases.CaseDTO;
import com.github.lianick.model.dto.cases.CaseErrorDTO;
import com.github.lianick.model.dto.cases.CaseFindAdminDTO;
import com.github.lianick.model.dto.cases.CaseFindPublicDTO;
import com.github.lianick.model.dto.cases.CaseLotteryResultDTO;
import com.github.lianick.model.dto.cases.CasePendingDTO;
import com.github.lianick.model.dto.cases.CaseQueueDTO;
import com.github.lianick.model.dto.cases.CaseRejectDTO;
import com.github.lianick.model.dto.cases.CaseVerifyDTO;
import com.github.lianick.model.dto.cases.CaseWaitlistDTO;
import com.github.lianick.model.dto.cases.CaseWithdrawnAdminDTO;
import com.github.lianick.model.dto.cases.CaseWithdrawnDTO;
import com.github.lianick.model.dto.lotteryQueue.LotteryQueueCreateDTO;
import com.github.lianick.model.dto.reviewLog.ReviewLogCreateDTO;
import com.github.lianick.model.eneity.CaseOrganization;
import com.github.lianick.model.eneity.CasePriority;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.LotteryQueue;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.ReviewLogs;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.model.eneity.WithdrawalRequests;
import com.github.lianick.model.enums.ApplicationMethod;
import com.github.lianick.model.enums.CaseOrganizationStatus;
import com.github.lianick.model.enums.CaseStatus;
import com.github.lianick.model.enums.LotteryQueueStatus;
import com.github.lianick.model.enums.LotteryResultStatus;
import com.github.lianick.repository.CasesRepository;
import com.github.lianick.repository.ClassesRepository;
import com.github.lianick.repository.LotteryQueueRepository;
import com.github.lianick.service.CaseOrganizationService;
import com.github.lianick.service.CasePriorityService;
import com.github.lianick.service.CaseService;
import com.github.lianick.service.LotteryQueueService;
import com.github.lianick.service.ReviewLogService;
import com.github.lianick.service.WithdrawalRequestService;
import com.github.lianick.util.BatchUtils;
import com.github.lianick.util.CaseNumberUtil;
import com.github.lianick.util.UserSecurityUtil;
import com.github.lianick.util.validate.CaseValidationUtil;
import com.github.lianick.util.validate.ClassValidationUtil;
import com.github.lianick.util.validate.OrganizationValidationUtil;
import com.github.lianick.util.validate.UserValidationUtil;

import jakarta.persistence.EntityManager;

@Service
@Transactional		// 確保 完整性 
public class CaseServiceImpl implements CaseService {
	
	private static final int BATCH_MAX_SIZE = 50; 	// 單次流程上限
	private static final long SLEEP_MILLIS = 200;	// 批量方法 休息時間 (毫秒)
	private static final int DEADLINE_DAY = 7;		// 報到截止日期 = 分配日期 + N 天
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private CasesRepository casesRepository;
	
	@Autowired
	private ClassesRepository classesRepository;
	
	@Autowired
	private LotteryQueueRepository lotteryQueueRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	@Autowired
	private UserSecurityUtil userSecurityUtil;
	
	@Autowired
	private UserValidationUtil userValidationUtil;
	
	@Autowired
	private CaseValidationUtil caseValidationUtil;

	@Autowired
	private OrganizationValidationUtil organizationValidationUtil;
	
	@Autowired
	private ClassValidationUtil classValidationUtil;
	
	@Autowired
	private CaseNumberUtil caseNumberUtil;
	
	@Autowired
	private CaseOrganizationService caseOrganizationService;
	
	@Autowired
	private CasePriorityService casePriorityService;
	
	@Autowired
	private WithdrawalRequestService withdrawalRequestService;
	
	@Autowired
	private ReviewLogService reviewLogService;
	
	@Autowired
	private LotteryQueueService lotteryQueueService;
	
	
	// ------------------
	// ----- 民眾 專用 -----
	// ------------------
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
									.map(oneCase -> modelMapper.map(oneCase, CaseDTO.class))
									.toList();
		
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

	// ------------------
	// ----- 員工 專用 -----
	// ------------------
	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public List<CaseDTO> findAllByAdmin(CaseFindAdminDTO caseFindAdminDTO) {
		// 0. 檢測 完整性
		CaseStatus caseStatus = caseValidationUtil.validateFindAdminAll(caseFindAdminDTO);
		
		// 1. 判斷權限
		Users users = userSecurityUtil.getCurrentUserEntity();
		boolean isManager = userValidationUtil.validateUserIsManager(users);
		
		// 2. 根據 要求 案件狀態 與 自身的機構 搜尋
		List<Cases> caseses = new ArrayList<Cases>();
		if (isManager) {
			caseses = casesRepository.findByStatus(caseStatus);
		} else {
			Long organizationId = users.getAdminInfo().getOrganization().getOrganizationId();
			caseses = casesRepository.findByStatusAndOrganizationId(caseStatus, organizationId);
		}
		
		// 3. 轉成 DTO 回傳
		List<CaseDTO> caseDTOs = caseses.stream()
									.map(oneCase -> modelMapper.map(oneCase, CaseDTO.class))
									.toList();
		return caseDTOs;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public CaseDTO findByAdmin(CaseFindAdminDTO caseFindAdminDTO) {
		// 0. 檢測 完整性
		caseValidationUtil.validateFindAdminOne(caseFindAdminDTO);
		
		// 1. 判斷權限
		Users users = userSecurityUtil.getCurrentUserEntity();
		Cases cases = entityFetcher.getCasesById(caseFindAdminDTO.getId());
		caseValidationUtil.validateUserAndCase(users, cases);
		
		// 2. 轉換成 DTO
		return modelMapper.map(cases, CaseDTO.class);
	}

	@Override
	public CaseDTO verifyCase(CaseVerifyDTO caseVerifyDTO) {
		// 0. 檢測 完整性
		CaseStatus newStatus = caseValidationUtil.validateCaseVerify(caseVerifyDTO);
		
		// 1. 判斷權限
		Users users = userSecurityUtil.getCurrentUserEntity();
		Cases cases = entityFetcher.getCasesByIdForUpdate(caseVerifyDTO.getId());
		caseValidationUtil.validateUserAndCase(users, cases);
		
		// 2. 判斷 案件 原本的狀態 是否 APPLIED
		caseValidationUtil.validateCaseStatus(cases, CaseStatus.APPLIED);
		CaseStatus originalStatus = cases.getStatus(); // 先存原本狀態
		
		// 3. 修改 案件狀態
		cases.setStatus(newStatus);
		
		// 4. 建立一個 審核紀錄用 DTO
		LocalDateTime now = LocalDateTime.now();
		ReviewLogCreateDTO<CaseStatus> reviewLogCreateDTO = reviewLogService.toDTO(
				cases, users.getAdminInfo(), 
				originalStatus, newStatus, CaseStatus.class, 
				now, caseVerifyDTO.getMessage());
		
		// 4. 建立一個 審核紀錄
		ReviewLogs reviewLogs = reviewLogService.createNewReviewLog(reviewLogCreateDTO);
		
		// 5. 將 審核紀錄 放入案件 並 回存
		cases.getReviewHistorys().add(reviewLogs);
		cases = casesRepository.save(cases);
		
		// 6. 轉成 DTO 回傳
		return modelMapper.map(cases, CaseDTO.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public List<CaseErrorDTO> advanceToPending(List<CasePendingDTO> casePendingDTOs) {
		// 1. 判斷權限
		Users users = userSecurityUtil.getCurrentUserEntity();
		caseValidationUtil.validateUserCanBatchProcess(users);
		
		// 2. 開始大量處理
		List<CaseErrorDTO> caseErrorDTOs = BatchUtils.processInBatches(
				casePendingDTOs, 
				BATCH_MAX_SIZE, 
				dto -> oneCaseToPending(dto, users.getAdminInfo()), 
				CasePendingDTO::getId, 
				SLEEP_MILLIS);
		
		return caseErrorDTOs;
	}
	
	/** 單一處理 案件(PASSED -> PENDING) 方法 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)	// 每處理一個案件都建立新事務，即使外層批次事務失敗，也不會回滾已完成的單一案件。
	private void oneCaseToPending(CasePendingDTO casePendingDTO, UserAdmin userAdmin) {
		// 0. 檢查完整性
		caseValidationUtil.validateCasePending(casePendingDTO);
		Cases cases = entityFetcher.getCasesByIdForUpdate(casePendingDTO.getId());
		
		// 1. 判斷 案件 原本的狀態 是否 PASSED 並改變成 PENDING
		caseValidationUtil.validateCaseStatus(cases, CaseStatus.PASSED);
		cases.setStatus(CaseStatus.PENDING);
		
		// 2. 補上 審核紀錄 DTO 資訊
		LocalDateTime now = LocalDateTime.now();
		ReviewLogCreateDTO<CaseStatus> reviewLogCreateDTO = reviewLogService.toDTO(
				cases, userAdmin, 
				CaseStatus.PASSED, CaseStatus.PENDING, CaseStatus.class, 
				now, null);
		
		// 3. 建立 審核紀錄
		ReviewLogs reviewLogs = reviewLogService.createNewReviewLog(reviewLogCreateDTO);
		
		// 4. 將 審核紀錄 放入案件 並 回存
		cases.getReviewHistorys().add(reviewLogs);
		casesRepository.save(cases);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public CaseDTO intoQueueCase(CaseQueueDTO caseQueueDTO) {
		// 0. 完整性 並取出 CaseOrganizationStatus
		CaseOrganizationStatus newStatus = caseValidationUtil.validateCaseQuene(caseQueueDTO);
		boolean isPassed = newStatus == CaseOrganizationStatus.PASSED;
		
		// 1. 判斷權限
		Users users = userSecurityUtil.getCurrentUserEntity();
		Cases cases = entityFetcher.getCasesByIdForUpdate(caseQueueDTO.getId());
		Organization organization = entityFetcher.getOrganizationById(caseQueueDTO.getOrganizationId());
		
		// 和 案件 是否有關連
		caseValidationUtil.validateUserAndCase(users, cases);
		// 和 目標機構 是否有關連
		organizationValidationUtil.validateOrganizationPermission(users, organization.getOrganizationId());
		// case 是不是 PENDING 狀態
		caseValidationUtil.validateCaseStatus(cases, CaseStatus.PENDING);
		
		// 2. 取出 CaseOrganization
		CaseOrganization caseOrganization = entityFetcher.getCaseOrganizationByCaseIdAndOrganizationIdForUpdate(cases.getCaseId(), organization.getOrganizationId());
		caseValidationUtil.validateCaseOrganizationStatus(caseOrganization, CaseOrganizationStatus.APPLIED);
		
		// 3. 更新 CaseOrganization 狀態
		CaseOrganizationStatus oldStatus = caseOrganization.getStatus();
		caseOrganization.setStatus(newStatus);
		
		// 4. 若 CaseOrganization 狀態改為 PASSED → 建立第一階段抽籤序列 LotteryQueue(QUEUED)
		LotteryQueue lotteryQueue = null;
		
		if (isPassed) {
			LotteryQueueCreateDTO lotteryQueueCreateDTO = new LotteryQueueCreateDTO();
			lotteryQueueCreateDTO.setCases(cases);
			lotteryQueueCreateDTO.setChildInfo(cases.getChildInfo());
			lotteryQueueCreateDTO.setOrganization(organization);
			
			lotteryQueue = lotteryQueueService.createNewLotteryQueue(lotteryQueueCreateDTO);
		}
		
		// 5. 建立審核紀錄
		LocalDateTime now = LocalDateTime.now();
		ReviewLogCreateDTO<CaseOrganizationStatus> reviewLogCreateDTO = reviewLogService.toDTO(
				cases, users.getAdminInfo(), 
				oldStatus, newStatus, CaseOrganizationStatus.class, 
				now, null);
		
		ReviewLogs reviewLogs = reviewLogService.createNewReviewLog(reviewLogCreateDTO);
		
		// 6. 將 審核紀錄/柱列 放入案件 更新 關聯狀態 並 回存
		if (lotteryQueue != null) {
		    cases.getLotteryQueues().add(lotteryQueue);
		}
		cases.getReviewHistorys().add(reviewLogs);

		cases = casesRepository.save(cases);
		
		// 7. 建立 DTO 回傳
		return modelMapper.map(cases, CaseDTO.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')") 
	public List<CaseErrorDTO> allocateCasesToClasses(List<CaseAllocationDTO> caseAllocationDTOs) {
		// 1. 檢查權限
		UserAdmin userAdmin = userSecurityUtil.getCurrentUserAdminEntity();
		Boolean isManager = userValidationUtil.validateUserIsManager(userAdmin.getUsers());
		Organization organization = userSecurityUtil.getOrganizationEntity();
		
		// 2. 執行大量方法
		List<CaseErrorDTO> caseErrorDTOs = BatchUtils.processInBatches(
				caseAllocationDTOs, 
				BATCH_MAX_SIZE, 
				dto -> oneCaseToAllocation(dto, userAdmin, isManager, organization), 
				CaseAllocationDTO::getCaseId, 
				SLEEP_MILLIS);
		
		return caseErrorDTOs;
	}

	/** 單一處理 案件(PENDING -> ALLOCATED) 方法 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void oneCaseToAllocation(CaseAllocationDTO caseAllocationDTO, UserAdmin userAdmin, Boolean isManager, Organization organization) {
		// 0. 檢查完整性
		caseValidationUtil.validateCaseAllocation(caseAllocationDTO);
		
		// 1. 取出必要的資料
		Cases cases = entityFetcher.getCasesByIdForUpdate(caseAllocationDTO.getCaseId());
		Classes classes = entityFetcher.getClassesByIdForUpdate(caseAllocationDTO.getClassId());
		Organization classOrganization = classes.getOrganization();
		
		// 2. 檢查權限
		if (!isManager) {
			organizationValidationUtil.validateOrganizationAndClass(organization, classes);
		}
		
		// 3. 判斷 案件 原本的狀態 是否 PENDING 並改變成 ALLOCATED
		caseValidationUtil.validateCaseStatus(cases, CaseStatus.PENDING);
		
		// 4. 判斷 班級 是否有空位
		classValidationUtil.validateClassCanAcceptOneMore(classes);
		
		// 5. 把 該案件 其他關連的 機構 改變成 ALLOCATED_TO_OTHER 狀態
		Set<CaseOrganization> allCaseOrganizations = cases.getOrganizations();
		
		//檢查是否有 其他 機構關聯
		List<CaseOrganization> otherCaseOrganizations = allCaseOrganizations.stream()
			    // 篩選出機構 ID 不等於 當前分配機構(classOrganization) ID 的 CaseOrganization
			    .filter(caseOrg -> !caseOrg.getOrganization().getOrganizationId()
			                               .equals(classOrganization.getOrganizationId()))
			    .toList();
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime deadLine = now.plusDays(DEADLINE_DAY);
		
		List<ReviewLogs> caseOrganizationReviewLogs = new ArrayList<>();
		List<ReviewLogs> lotteryQueueReviewLogs = new ArrayList<>();
		
		if (!otherCaseOrganizations.isEmpty()) {
		    for (CaseOrganization otherCaseOrg : otherCaseOrganizations) {
		    	// 改變 狀態為 ALLOCATED_TO_OTHER 並 同步建立審核紀錄
		    	CaseOrganizationStatus oldCaseOrganizationStatus = otherCaseOrg.getStatus();
		    	CaseOrganizationStatus newCaseOrganizationStatus = CaseOrganizationStatus.ALLOCATED_TO_OTHER;
		    	
		        otherCaseOrg.setStatus(newCaseOrganizationStatus);
		        
		        // 建立 審核紀錄 並回存 List
		        ReviewLogCreateDTO<CaseOrganizationStatus> caseOrganizationReviewLogCreateDTO = reviewLogService.toDTO(
		    			cases, userAdmin, 
		    			oldCaseOrganizationStatus, newCaseOrganizationStatus, CaseOrganizationStatus.class, 
		    			now, null);
		        ReviewLogs caseOrganizationReviewLog = reviewLogService.createNewReviewLog(caseOrganizationReviewLogCreateDTO);
		        caseOrganizationReviewLogs.add(caseOrganizationReviewLog);
		        
		        // 6. 把 該案件 其他關連的 機構 的 LotteryQueue 改變成 ALLOCATED_ELSEWHERE 狀態
		        Long caseId = otherCaseOrg.getCases().getCaseId();
		        Long organizationId = otherCaseOrg.getOrganization().getOrganizationId();
		        
		        if (lotteryQueueRepository.existsByCaseAndOrg(caseId, organizationId)) {
		        	// 改變 狀態為 ALLOCATED_ELSEWHERE 並 同步建立審核紀錄
					LotteryQueue lotteryQueue = entityFetcher.getLotteryQueueByCaseIdAndOrganizationIdForUpdate(caseId, organizationId);
		        	
					LotteryQueueStatus oldLotteryQueueStatus = lotteryQueue.getStatus();
					LotteryQueueStatus newLotteryQueueStatus = LotteryQueueStatus.ALLOCATED_ELSEWHERE;
					
		        	lotteryQueue.setStatus(newLotteryQueueStatus);
		        	
		        	ReviewLogCreateDTO<LotteryQueueStatus> lotteryQueueSReviewLogCreateDTO = reviewLogService.toDTO(
		        			cases, userAdmin, 
		        			oldLotteryQueueStatus, newLotteryQueueStatus, LotteryQueueStatus.class, 
		        			now, null);
		        	ReviewLogs lotteryQueueReviewLog = reviewLogService.createNewReviewLog(lotteryQueueSReviewLogCreateDTO);
		        	lotteryQueueReviewLogs.add(lotteryQueueReviewLog);
				}
		    }	
		}
		
		// 7. 建立 cases 的審核紀錄
		CaseStatus oldCaseStatus = cases.getStatus();
		CaseStatus newCaseStatus = CaseStatus.ALLOCATED;
		
		ReviewLogCreateDTO<CaseStatus> caseReviewLogCreateDTO = reviewLogService.toDTO(
				cases, userAdmin, 
				oldCaseStatus, newCaseStatus, CaseStatus.class, 
				now, null);
		ReviewLogs caseReviewLog = reviewLogService.createNewReviewLog(caseReviewLogCreateDTO);
		
		// 8. 修改 cases 和 classes 資料 + 並放入 分配時間 + 報到期限
		cases.setStatus(newCaseStatus);
		cases.setAllocationDate(now);
		cases.setEnrollmentDeadline(deadLine);
		
		classes.setCurrentCount(classes.getCurrentCount()+1);
		
		// 9. 將審核紀錄DTO 全部記錄成 審核紀錄 並且 存入 case
		cases.getReviewHistorys().addAll(caseOrganizationReviewLogs);
		cases.getReviewHistorys().addAll(lotteryQueueReviewLogs);
		cases.getReviewHistorys().add(caseReviewLog);
		
		classesRepository.save(classes);
		casesRepository.save(cases);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public List<CaseErrorDTO> completedCase(List<CaseCompleteDTO> caseCompleteDTOs) {
		// 1. 檢查權限
		UserAdmin userAdmin = userSecurityUtil.getCurrentUserAdminEntity();
		Boolean isManager = userValidationUtil.validateUserIsManager(userAdmin.getUsers());
		Organization userOrganization = userAdmin.getOrganization();
				
		// 2. 大量處理
		List<CaseErrorDTO> caseErrorDTOs = BatchUtils.processInBatches(
				caseCompleteDTOs, 
				BATCH_MAX_SIZE, 
				dto -> oneCaseToCompleted(dto, userAdmin, isManager, userOrganization), 
				CaseCompleteDTO::getCaseId, 
				SLEEP_MILLIS);
		
		return caseErrorDTOs;
	}
	
	/** 單一處理 案件(ALLOCATED -> COMPLETED) 方法 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void oneCaseToCompleted(CaseCompleteDTO caseCompleteDTO, UserAdmin userAdmin, Boolean isManager, Organization userOrganization) {
		// 0. 檢查完整性
		caseValidationUtil.validateCaseComplete(caseCompleteDTO);
		
		// 1. 取出必要的
		Cases cases = entityFetcher.getCasesByIdForUpdate(caseCompleteDTO.getCaseId());
		Classes classes = entityFetcher.getClassesByIdForUpdate(caseCompleteDTO.getClassId());
		
		// 2. 檢查權限
		if (!isManager) {
			organizationValidationUtil.validateOrganizationAndClass(userOrganization, classes);
		}
		
		// 3. 判斷 案件 原本的狀態 是否 ALLOCATED 並改變成 COMPLETED
		caseValidationUtil.validateCaseStatus(cases, CaseStatus.ALLOCATED);
		
		// 4. 建立 審核紀錄
		CaseStatus oldCaseStatus = cases.getStatus();
		CaseStatus newCaseStatus = CaseStatus.COMPLETED;
		LocalDateTime now = LocalDateTime.now();
		
		ReviewLogCreateDTO<CaseStatus> caseReviewLogCreateDTO = reviewLogService.toDTO(
				cases, userAdmin, 
				oldCaseStatus, newCaseStatus, CaseStatus.class, 
				now, null);
		ReviewLogs reviewLogs = reviewLogService.createNewReviewLog(caseReviewLogCreateDTO);
		
		// 5. 更新狀態 + 建立關係 並 回存
		cases.setStatus(newCaseStatus);
		cases.getReviewHistorys().add(reviewLogs);
		casesRepository.save(cases);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public List<CaseErrorDTO> cleanupOverdueAllocations(LocalDateTime today) {
		// 0. 檢查完整性
		caseValidationUtil.validateToday(today);
		
		// 1. 檢查權限
		Users users = userSecurityUtil.getCurrentUserEntity();
		caseValidationUtil.validateUserCanBatchProcess(users);
		UserAdmin userAdmin = userSecurityUtil.getCurrentUserAdminEntity();
		
		// 2. 取出 目標 案件們
		List<Long> caseIds = casesRepository.findOverdueCaseIds(CaseStatus.ALLOCATED.getCode(), today);
		
		// 3. 大量處理
		List<CaseErrorDTO> caseErrorDTOs = BatchUtils.processInBatches(
				caseIds, 
				BATCH_MAX_SIZE, 
				caseId -> oneCaseOverdueAllocation(caseId, userAdmin), 
				Function.identity(), 
				SLEEP_MILLIS);

		return caseErrorDTOs;
	}
	
	/** 單一處理 尚未報到 的 案件 (ALLOCATED -> REJECTED) */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void oneCaseOverdueAllocation(Long CaseId, UserAdmin userAdmin) {
		// 1. 獲取必要的元素 並 鎖定
		Cases cases = entityFetcher.getCasesByIdForUpdate(CaseId);
		caseValidationUtil.validateCaseStatus(cases, CaseStatus.ALLOCATED);
		
		Classes classes = entityFetcher.getClassesByIdForUpdate(cases.getClasses().getClassId());
		
		CaseOrganization caseOrganization = entityFetcher.getCaseOrganizationByCaseIdAndOrganizationIdForUpdate(
				cases.getCaseId(), classes.getOrganization().getOrganizationId());
		caseValidationUtil.validateCaseOrganizationStatus(caseOrganization, CaseOrganizationStatus.PASSED);
		
		LotteryQueue lotteryQueue = entityFetcher.getLotteryQueueByCaseIdAndOrganizationIdForUpdate(
				cases.getCaseId(), classes.getOrganization().getOrganizationId());
		caseValidationUtil.validateLotteryQueueStatus(lotteryQueue, LotteryQueueStatus.SELECTED);
		
		// 2. 建立 必要的 元素
		CaseStatus oleCaseStatus = cases.getStatus();
		CaseStatus newCaseStatus = CaseStatus.REJECTED;
		
		Integer currentCount = classes.getCurrentCount();
		if (currentCount <= 0) {
			throw new ClassesFailureException("目前班級人數 已經等於0");
		}
		
		CaseOrganizationStatus oldCaseOrgStatus = caseOrganization.getStatus();
		CaseOrganizationStatus newCaseOrgStatus = CaseOrganizationStatus.REJECTED;
		
		LotteryQueueStatus oldLotteryQueueStatus = lotteryQueue.getStatus();
		LotteryQueueStatus newLotteryQueueStatus = LotteryQueueStatus.FAILED;
		
		// 3. 班級刪除 並 取消關聯
		classes.setCurrentCount(currentCount-1);
		cases.setClasses(null);
		
		caseOrganization.setStatus(newCaseOrgStatus);
		lotteryQueue.setStatus(newLotteryQueueStatus);
		
		// 4. 建立所有的 審核紀錄
		LocalDateTime now = LocalDateTime.now();
		
		ReviewLogs casesReviewLog = reviewLogService.createNewReviewLog(
				reviewLogService.toDTO(
						cases, userAdmin, 
						oleCaseStatus, newCaseStatus, CaseStatus.class, 
						now, "報到期限已過期，自動拒絕案件分配。"));
		
		ReviewLogs caseOrgReviewLog = reviewLogService.createNewReviewLog(
				reviewLogService.toDTO(
						cases, userAdmin, 
						oldCaseOrgStatus, newCaseOrgStatus, CaseOrganizationStatus.class, 
						now, "因案件逾期未報到，機構關聯狀態變更為拒絕。"));
		
		ReviewLogs lotteryQueueReviewLog = reviewLogService.createNewReviewLog(
				reviewLogService.toDTO(
						cases, userAdmin, 
						oldLotteryQueueStatus, newLotteryQueueStatus, LotteryQueueStatus.class, 
						now, "因案件逾期未報到，抽籤佇列狀態變更為失敗。"));
		
		// 5. 修改案件 最終狀態
		cases.setStatus(newCaseStatus);
		
		// 6. 將審核紀錄 存入案件中
		cases.getReviewHistorys().add(casesReviewLog);
		cases.getReviewHistorys().add(caseOrgReviewLog);
		cases.getReviewHistorys().add(lotteryQueueReviewLog);
		
		// 7. 回存資料庫
		casesRepository.save(cases);
		classesRepository.save(classes);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public List<CaseErrorDTO> processWaitlistSuccess(List<CaseWaitlistDTO> waitlistDTOs) {
		// 1. 檢查權限
		UserAdmin userAdmin = userSecurityUtil.getCurrentUserAdminEntity();
		Boolean isManager = userValidationUtil.validateUserIsManager(userAdmin.getUsers());
		Organization userOrganization = userAdmin.getOrganization();
		
		// 2. 大量執行
		List<CaseErrorDTO> caseErrorDTOs = BatchUtils.processInBatches(
				waitlistDTOs, 
				BATCH_MAX_SIZE, 
				dto -> oneCaseWaitlist(dto, userAdmin, isManager, userOrganization), 
				CaseWaitlistDTO::getCaseId, 
				SLEEP_MILLIS);
		
		return caseErrorDTOs;
	}
	
	/** 機構班級 有空位 的 案件 (PENDING -> ALLOCATED) */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void oneCaseWaitlist(CaseWaitlistDTO waitlistDTO, UserAdmin userAdmin, Boolean isManager, Organization userOrganization) {
		// 0. 完整性
		caseValidationUtil.validateCaseWaitlist(waitlistDTO);
		
		// 1. 取出 必要的鎖定資料 並 判斷狀態 
		Cases cases = entityFetcher.getCasesByIdForUpdate(waitlistDTO.getCaseId());
		caseValidationUtil.validateCaseStatus(cases, CaseStatus.PENDING);
		
		Organization organization = entityFetcher.getOrganizationById(waitlistDTO.getOrganizationId());
		if (!isManager && organization.getOrganizationId() != userOrganization.getOrganizationId()) {
			throw new CaseFailureException("權限不足 無法控制其他機構");
		}
		
		CaseOrganization caseOrganization = entityFetcher.getCaseOrganizationByCaseIdAndOrganizationIdForUpdate(cases.getCaseId(), organization.getOrganizationId());
		caseValidationUtil.validateCaseOrganizationStatus(caseOrganization, CaseOrganizationStatus.WAITLISTED);
		
		LotteryQueue lotteryQueue = entityFetcher.getLotteryQueueByCaseIdAndOrganizationIdForUpdate(cases.getCaseId(), organization.getOrganizationId());
		caseValidationUtil.validateLotteryQueueStatus(lotteryQueue, LotteryQueueStatus.SELECTED);
		
		// 2. 尋找 該機構中 有空位的 班級
		Classes classes = entityFetcher.getClassesByOrganizationIdHasEmptyCapacityForUpdate(organization.getOrganizationId());
		
		// 3. 班級 人數 +1
		Integer currentCount = classes.getCurrentCount();
		if (currentCount >= classes.getMaxCapacity()) {
		    throw new CaseFailureException("班級人數已滿");		// 預防萬一檢測用
		}
		classes.setCurrentCount(currentCount +1);
		
		// 4. 修改 各狀態 並同時建立 審核紀錄
		LocalDateTime now = LocalDateTime.now();
		
		CaseStatus oldCaseStatus = cases.getStatus();
		CaseStatus newCaseStatus = CaseStatus.ALLOCATED;
		
		CaseOrganizationStatus oldCaseOrganizationStatus = caseOrganization.getStatus();
		CaseOrganizationStatus newCaseOrganizationStatus = CaseOrganizationStatus.PASSED;
		
		cases.setStatus(newCaseStatus);
		caseOrganization.setStatus(newCaseOrganizationStatus);
		
		ReviewLogs caseReviewLogs = reviewLogService.createNewReviewLog(
				reviewLogService.toDTO(
						cases, userAdmin, 
						oldCaseStatus, newCaseStatus, CaseStatus.class, 
						now, "備選 進入 正選"));
		ReviewLogs caseOrganizationReviewLogs = reviewLogService.createNewReviewLog(
				reviewLogService.toDTO(
						cases, userAdmin, 
						oldCaseOrganizationStatus, newCaseOrganizationStatus, CaseOrganizationStatus.class, 
						now, "備選 進入 正選"));
		
		// 5. 存入審核紀錄 並 回存
		cases.getReviewHistorys().add(caseReviewLogs);
		cases.getReviewHistorys().add(caseOrganizationReviewLogs);
		
		casesRepository.save(cases);
		classesRepository.save(classes);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public void processWaitlistFailure(List<CaseWaitlistDTO> waitlistDTOs) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public void intoRejected(List<CaseRejectDTO> caseRejectDTOs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<WithdrawalRequestDTO> findAllWithdrawalRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public void verifyWithdrawnCase(List<CaseWithdrawnAdminDTO> caseWithdrawnAdminDTOs) {
		// TODO Auto-generated method stub
		
	}

	// -------------------------------------------
	// ----- 供給 LotteryFacadeService 的 子方法 -----
	// -------------------------------------------
	@Override
	public void processLotteryResults(List<CaseLotteryResultDTO> lotteryResults, UserAdmin userAdmin) {
		// 0. 假設沒有結果 直接跳過
		if (lotteryResults.isEmpty()) {
			return;
		}

		// 1. 讀取回傳來源 依照 LotteryResultStatus 分類 
		List<CaseLotteryResultDTO> allResults = new ArrayList<>();
		allResults.addAll(lotteryResults.stream()
				.filter(r -> r.getResultStatus() == LotteryResultStatus.SUCCESS)
				.toList());
		allResults.addAll(lotteryResults.stream()
				.filter(r -> r.getResultStatus() == LotteryResultStatus.WAITLIST)
				.toList());
		allResults.addAll(lotteryResults.stream()
				.filter(r -> r.getResultStatus() == LotteryResultStatus.FAILED)
				.toList());

		// 2. 開始大量處理
		for (int i = 1; i <= allResults.size(); i++) {

			// 呼叫單一處理方法 (在當前大事務中執行)
			oneCaseInLottery(allResults.get(i-1), userAdmin);

			// 每處理 BATCH_SIZE 個案件，執行一次優化
			if (i % BATCH_MAX_SIZE == 0) {

				entityManager.flush(); 	// 把 Hibernate Session 的變更送到資料庫（但不 commit）

				// 暫停以減緩資料庫壓力
				try {
					Thread.sleep(200); // 0.2 秒
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}

		// final flush
		entityManager.flush();
	}
	
	/** 單一處理 案件(CaseOrganization + LotteryQueue) 方法 */
	private void oneCaseInLottery(CaseLotteryResultDTO lotteryResult, UserAdmin userAdmin) {
		// 0. 檢查完整性
		caseValidationUtil.validateCaseLottery(lotteryResult);

		// 1. 取得必要的東西
		Cases cases = entityFetcher.getCasesByIdForUpdate(lotteryResult.getCaseId());
		caseValidationUtil.validateCaseStatus(cases, CaseStatus.PENDING);

		CaseOrganization caseOrganization = entityFetcher.getCaseOrganizationByCaseIdAndOrganizationId(lotteryResult.getCaseId(), lotteryResult.getOrganizationId());
		caseValidationUtil.validateCaseOrganizationStatus(caseOrganization, CaseOrganizationStatus.PASSED);

		LotteryQueue lotteryQueue = entityFetcher.getLotteryQueueByCaseIdAndOrganizationId(lotteryResult.getCaseId(), lotteryResult.getOrganizationId());
		caseValidationUtil.validateLotteryQueueStatus(lotteryQueue, LotteryQueueStatus.QUEUED);

		LotteryResultStatus status = lotteryResult.getResultStatus();
		Integer alternateNumber = lotteryResult.getAlternateNumber();
		Integer lotteryOrder = lotteryResult.getLotteryOrder();

		// 2. 取得 CaseOrganization 和 LotteryResultStatus 新舊狀態
		CaseOrganizationStatus oldCaseOrganizationStatus = caseOrganization.getStatus();
		CaseOrganizationStatus newCaseOrganizationStatus = null;

		LotteryQueueStatus oldLotteryQueueStatus = lotteryQueue.getStatus();
		LotteryQueueStatus newLotteryQueueStatus = null;

		// 3. 根據 結果狀態 修正數值 
		if (status == LotteryResultStatus.SUCCESS) {
			newCaseOrganizationStatus = CaseOrganizationStatus.PASSED;
			newLotteryQueueStatus = LotteryQueueStatus.SELECTED;
		} else if (status == LotteryResultStatus.WAITLIST) {
			newCaseOrganizationStatus = CaseOrganizationStatus.WAITLISTED;
			newLotteryQueueStatus = LotteryQueueStatus.SELECTED;
			lotteryQueue.setAlternateNumber(alternateNumber);
		} else if (status == LotteryResultStatus.FAILED) {
			newCaseOrganizationStatus = CaseOrganizationStatus.REJECTED;
			newLotteryQueueStatus = LotteryQueueStatus.FAILED;
		}

		caseOrganization.setStatus(newCaseOrganizationStatus);
		lotteryQueue.setStatus(newLotteryQueueStatus);
		lotteryQueue.setLotteryOrder(lotteryOrder);

		// 4. 各自建立 審核紀錄 DTO
		LocalDateTime now = LocalDateTime.now();
		ReviewLogCreateDTO<CaseOrganizationStatus> caseOrganizationReviewLogCreateDTO = reviewLogService.toDTO(
				cases, userAdmin, 
				oldCaseOrganizationStatus, newCaseOrganizationStatus, CaseOrganizationStatus.class, 
				now, null);

		ReviewLogCreateDTO<LotteryQueueStatus> lotteryQueueReviewLogCreateDTO = reviewLogService.toDTO(
				cases, userAdmin, 
				oldLotteryQueueStatus, newLotteryQueueStatus, LotteryQueueStatus.class, 
				now, null);

		// 5. 各自建立 審核紀錄
		ReviewLogs caseOrganizationReviewLog = reviewLogService.createNewReviewLog(caseOrganizationReviewLogCreateDTO);
		ReviewLogs lotteryQueueReviewLog = reviewLogService.createNewReviewLog(lotteryQueueReviewLogCreateDTO);
		cases.getReviewHistorys().add(caseOrganizationReviewLog);
		cases.getReviewHistorys().add(lotteryQueueReviewLog);

		// 6. 統一 回傳
		casesRepository.save(cases);
	}
}
