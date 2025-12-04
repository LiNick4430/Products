package com.github.lianick.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.LotteryQueueFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.cases.CaseLotteryResultDTO;
import com.github.lianick.model.dto.lotteryQueue.LotteryQueueCreateDTO;
import com.github.lianick.model.dto.lotteryQueue.LotteryQueueDTO;
import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.LotteryQueue;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.model.enums.LotteryQueueStatus;
import com.github.lianick.model.enums.LotteryResultStatus;
import com.github.lianick.repository.LotteryQueueRepository;
import com.github.lianick.service.LotteryQueueService;
import com.github.lianick.util.UserSecurityUtil;
import com.github.lianick.util.validate.UserValidationUtil;

@Service
@Transactional
public class LotteryQueueServiceImpl implements LotteryQueueService{

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
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public List<LotteryQueueDTO> findQueuedCases() {
		// 1. 判斷權限
		Users users = userSecurityUtil.getCurrentUserEntity();
		boolean isManager = userValidationUtil.validateUserIsManager(users);
		
		// 2. 根據權限 執行 搜尋方法(status = QUEUED)
		List<LotteryQueue> lotteryQueues = new ArrayList<>();
		LotteryQueueStatus status = LotteryQueueStatus.QUEUED;
		
		if (isManager) {
			// 管理層
			lotteryQueues = lotteryQueueRepository.findByStatus(status);
		} else {
			// 基層員工
			Organization organization = userSecurityUtil.getOrganizationEntity();
			lotteryQueues = lotteryQueueRepository.findByOrganizationAndStatus(organization, status);
		}
		
		// 3. 轉成 DTO 回傳
		return lotteryQueues.stream()
					.map(lotteryQueue -> modelMapper.map(lotteryQueue, LotteryQueueDTO.class))
					.toList();
	}
	
	@Override
	@Transactional(readOnly = true)		// 只負責 讀取計算資料 + 產生DTO給後面 節省效能
	public List<CaseLotteryResultDTO> executeLottery(Organization organization) {
		// 1. 取出 該機構 的 所有班級
		List<Classes> organizationClasses = entityFetcher.getClassesListByOrganization(organization);
		
		// 2. 找尋 該機構 的 所有 LotteryQueue = QUEUED 的 柱列
		LotteryQueueStatus status = LotteryQueueStatus.QUEUED;
		List<LotteryQueue> lotteryQueues = lotteryQueueRepository.findByOrganizationAndStatus(organization, status);
		
		// 3. 取出 所有班級 的 班級空位
		Integer totalEmptyCapacity = organizationClasses.stream()
			    // 1. 將 Stream<Classes> 轉換為 IntStream，其中每個元素是該班級的空位數
			    .mapToInt(classes -> classes.getMaxCapacity() - classes.getCurrentCount())
			    // 2. 對 IntStream 中的所有元素求和
			    .sum();
		
		// 4. 檢查 全部空位 和 全部柱列數量, 假設 柱列數量 <= 全部空位 就不用抽籤
		if (lotteryQueues.size() <= totalEmptyCapacity) {
			final int[] orderCounter = {1};
			
			return lotteryQueues.stream()
					.map(queue -> {
						// 1. 讀取並使用當前序號 (orderCounter[0])
						Integer currentOrder = orderCounter[0];
						// 2. 更新計數器(修改陣列中的值)
						orderCounter[0]++;
						
						return new CaseLotteryResultDTO(
								queue.getCases().getCaseId(),
								queue.getOrganization().getOrganizationId(),
								LotteryResultStatus.SUCCESS,
								currentOrder,
								null
								);
					})
					.toList();
		}
		
		// 5. 分配 抽籤 結果 人數		(預計 每一個班級 有兩名 備選)
		Integer successCount = totalEmptyCapacity;	// 成功的總人數
		Integer maxWaitlistCapacity = organizationClasses.size()*2;	// 最大備選人數
		
		Integer totalAcceptanceCapacity = successCount + maxWaitlistCapacity;	// 總錄取名額
		Integer actualQueueSize = lotteryQueues.size();	// 柱列的總數

		Integer actualWaitlistCount;	// 被選的總人數
		Integer failedCount;			// 失敗的總人數(單純計算 後續不會用到)

		if (actualQueueSize <= totalAcceptanceCapacity) {
		    // 申請人數少於總錄取名額，沒有落選者
		    failedCount = 0;
		    actualWaitlistCount = actualQueueSize - successCount; 
		} else {
		    // 申請人數多於總錄取名額，備取人數取上限，其餘落選
		    actualWaitlistCount = maxWaitlistCapacity;
		    failedCount = actualQueueSize - totalAcceptanceCapacity;
		}
		
		// 6. 執行抽籤 (successCount(成功的位置) + actualWaitlistCount(備選的位置) + failedCount(失敗的位置))
		// 建立一個 被打亂的 柱列
		List<LotteryQueue> randomizedQueues = new ArrayList<>(lotteryQueues);
		Collections.shuffle(randomizedQueues);
		
		// 建立一個 回傳結果
		List<CaseLotteryResultDTO> finalResult = new ArrayList<>();
		
		// 總人數
		Integer totalCandidates = randomizedQueues.size();
		
		// 數據完整性檢查
		if (successCount + actualWaitlistCount + failedCount != totalCandidates) {
			throw new LotteryQueueFailureException(
			        String.format("Lottery allocation mismatch. Candidates: %d, Allocated: %d (S:%d + W:%d + F:%d)",
			            totalCandidates, 
			            (successCount + actualWaitlistCount + failedCount),
			            successCount, 
			            actualWaitlistCount, 
			            failedCount)
			    );
		}
		
		// 遍歷 打亂 的 群組
		for (int i = 1; i <= totalCandidates; i++) {	// 單純記數 從 1 開始
			LotteryQueue queue = randomizedQueues.get(i-1);	// 依照 JAVA 取數 從 0 開始 因此需要 -1 
		    LotteryResultStatus resultStatus;
		    Integer alternateNumber = null; // 備選號碼
		    Integer lotteryOrder = i;
			
			if (i <= successCount) {
				// SUCCESS
				resultStatus = LotteryResultStatus.SUCCESS;
			} else if (i <= (successCount + actualWaitlistCount)) {
				// WAITLIST
				resultStatus = LotteryResultStatus.WAITLIST;
				// 備選號碼
				alternateNumber = i - successCount;
			} else {
				// FAILED
				resultStatus = LotteryResultStatus.FAILED;
			}
			
			finalResult.add(new CaseLotteryResultDTO(
					queue.getCases().getCaseId(),
					queue.getOrganization().getOrganizationId(), 
					resultStatus,
					lotteryOrder,
					alternateNumber));
		}
		
		return finalResult;
	}
	
	@Override
	public LotteryQueue createNewLotteryQueue(LotteryQueueCreateDTO lotteryQueueCreateDTO) {
		// 0. 檢查完整性
		if (lotteryQueueCreateDTO.getCases() == null ||
				lotteryQueueCreateDTO.getChildInfo() == null ||
				lotteryQueueCreateDTO.getOrganization() == null) {
			throw new ValueMissException("缺少必要資訊(案件, 機構, 兒童)");
		}
		
		// 1. 檢查是否存在
		if (lotteryQueueRepository.existsByCaseAndOrg(
				lotteryQueueCreateDTO.getCases().getCaseId(), 
				lotteryQueueCreateDTO.getOrganization().getOrganizationId())) {
			throw new LotteryQueueFailureException("抽籤柱列 已經存在");
		}
		
		// 2. 建立 新的抽籤柱列
		LotteryQueue lotteryQueue = new LotteryQueue();
		lotteryQueue.setCases(lotteryQueueCreateDTO.getCases());
		lotteryQueue.setChildInfo(lotteryQueueCreateDTO.getChildInfo());
		lotteryQueue.setOrganization(lotteryQueueCreateDTO.getOrganization());
		lotteryQueue.setLotteryOrder(-1);	// 只有放入柱列 所以是 -1
		lotteryQueue.setStatus(LotteryQueueStatus.QUEUED);
		
		return lotteryQueue;
	}
}
