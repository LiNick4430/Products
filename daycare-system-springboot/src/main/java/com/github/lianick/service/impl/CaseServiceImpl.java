package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import com.github.lianick.util.CaseNumberUtil;
import com.github.lianick.util.UserSecurityUtil;
import com.github.lianick.util.validate.CaseValidationUtil;
import com.github.lianick.util.validate.ClassValidationUtil;
import com.github.lianick.util.validate.OrganizationValidationUtil;
import com.github.lianick.util.validate.UserValidationUtil;

@Service
@Transactional		// 確保 完整性 
public class CaseServiceImpl implements CaseService {

	// 建立 Logger
	private static final Logger logger = LoggerFactory.getLogger(CaseServiceImpl.class);
	private static final int BATCH_MAX_SIZE = 50; 	// 單次流程上限
	
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
		Cases cases = entityFetcher.getCasesById(caseVerifyDTO.getId());
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
		List<CaseErrorDTO> caseErrorDTOs = new ArrayList<>();	//失敗 案件 集合
		
		for (int i = 0; i < casePendingDTOs.size(); i += BATCH_MAX_SIZE) {
			List<CasePendingDTO> batch = casePendingDTOs.subList(
					i, Math.min(i + BATCH_MAX_SIZE , casePendingDTOs.size()));
			
			for (CasePendingDTO casePendingDTO : batch) {
				try {
		            oneCaseToPending(casePendingDTO, users.getAdminInfo());
		        } catch (Exception e) {
		            // 可以記錄錯誤，但繼續處理下一個案件
		        	logger.error("案件 {} 處理失敗: {}", casePendingDTO.getId(), e.getMessage(), e);
		        	caseErrorDTOs.add(new CaseErrorDTO(casePendingDTO.getId(), e.getMessage()));
		        }
			}
			
			try {
			    Thread.sleep(200); // 0.2 秒
			} catch (InterruptedException e) {
			    Thread.currentThread().interrupt(); // 保留中斷狀態
			    logger.warn("批次暫停被中斷", e);
			}
		}
		
		return caseErrorDTOs;
	}
	
	/** 單一處理 案件(PASSED -> PENDING) 方法 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)	// 每處理一個案件都建立新事務，即使外層批次事務失敗，也不會回滾已完成的單一案件。
	private void oneCaseToPending(CasePendingDTO casePendingDTO, UserAdmin userAdmin) {
		// 0. 檢查完整性
		caseValidationUtil.validateCasePending(casePendingDTO);
		Cases cases = entityFetcher.getCasesById(casePendingDTO.getId());
		
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
		Cases cases = entityFetcher.getCasesById(caseQueueDTO.getId());
		Organization organization = entityFetcher.getOrganizationById(caseQueueDTO.getOrganizationId());
		
		// 和 案件 是否有關連
		caseValidationUtil.validateUserAndCase(users, cases);
		// 和 目標機構 是否有關連
		organizationValidationUtil.validateOrganizationPermission(users, organization.getOrganizationId());
		// case 是不是 PENDING 狀態
		caseValidationUtil.validateCaseStatus(cases, CaseStatus.PENDING);
		
		// 2. 取出 CaseOrganization
		CaseOrganization caseOrganization = entityFetcher.getCaseOrganizationByCaseIdAndOrganizationId(cases.getCaseId(), organization.getOrganizationId());
		
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
		List<CaseErrorDTO> caseErrorDTOs = new ArrayList<>();	//失敗 案件 集合
		
		for (int i = 0; i < caseAllocationDTOs.size(); i += BATCH_MAX_SIZE) {
			List<CaseAllocationDTO> batch = caseAllocationDTOs.subList(
					i, Math.min(i + BATCH_MAX_SIZE , caseAllocationDTOs.size()));
			
			for (CaseAllocationDTO caseAllocationDTO : batch) {
				try {
					oneCaseToAllocation(caseAllocationDTO, userAdmin, isManager, organization);
		        } catch (Exception e) {
		            // 可以記錄錯誤，但繼續處理下一個案件
		        	logger.error("案件 {} 處理失敗: {}", caseAllocationDTO.getCaseId(), e.getMessage(), e);
		        	caseErrorDTOs.add(new CaseErrorDTO(caseAllocationDTO.getCaseId(), e.getMessage()));
		        }
			}
			
			try {
			    Thread.sleep(200); // 0.2 秒
			} catch (InterruptedException e) {
			    Thread.currentThread().interrupt(); // 保留中斷狀態
			    logger.warn("批次暫停被中斷", e);
			}
		}
		
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
					LotteryQueue lotteryQueue = entityFetcher.getLotteryQueueByCaseIdAndOrganizationId(caseId, organizationId);
		        	
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
		
		// 7. 修改 cases 和 classes 資料
		cases.setStatus(newCaseStatus);
		classes.setCurrentCount(classes.getCurrentCount()+1);
		
		// 8. 將審核紀錄DTO 全部記錄成 審核紀錄 並且 存入 case
		cases.getReviewHistorys().addAll(caseOrganizationReviewLogs);
		cases.getReviewHistorys().addAll(lotteryQueueReviewLogs);
		cases.getReviewHistorys().add(caseReviewLog);
		
		classesRepository.save(classes);
		casesRepository.save(cases);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public void completedCase(List<CaseCompleteDTO> caseCompleteDTOs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public void processWaitlistSuccess(List<CaseWaitlistDTO> waitlistDTOs) {
		// TODO Auto-generated method stub
		
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
		List<CaseLotteryResultDTO> successList = new ArrayList<>();
		List<CaseLotteryResultDTO> waitList = new ArrayList<>();
		List<CaseLotteryResultDTO> failedList = new ArrayList<>();
		
		lotteryResults.forEach(lotteryResult -> {
			if (lotteryResult.getResultStatus() == LotteryResultStatus.SUCCESS) {
				successList.add(lotteryResult);
			} else if (lotteryResult.getResultStatus() == LotteryResultStatus.WAITLIST) {
				waitList.add(lotteryResult);
			} else if (lotteryResult.getResultStatus() == LotteryResultStatus.FAILED) {
				failedList.add(lotteryResult);
			}
		});
		
		// 2. 開始大量處理
		successList.forEach(lotteryResult -> oneCaseInLottery(lotteryResult, userAdmin));
		waitList.forEach(lotteryResult -> oneCaseInLottery(lotteryResult, userAdmin));
		failedList.forEach(lotteryResult -> oneCaseInLottery(lotteryResult, userAdmin));
	}
	
	/** 單一處理 案件(CaseOrganization + LotteryQueue) 方法 */
	private void oneCaseInLottery(CaseLotteryResultDTO lotteryResult, UserAdmin userAdmin) {
		// 0. 檢查完整性
		caseValidationUtil.validateCaseLottery(lotteryResult);
		
		// 1. 取得必要的東西
		Cases cases = entityFetcher.getCasesById(lotteryResult.getCaseId());
		CaseOrganization caseOrganization = entityFetcher.getCaseOrganizationByCaseIdAndOrganizationId(lotteryResult.getCaseId(), lotteryResult.getOrganizationId());
		LotteryQueue lotteryQueue = entityFetcher.getLotteryQueueByCaseIdAndOrganizationId(lotteryResult.getCaseId(), lotteryResult.getOrganizationId());
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
