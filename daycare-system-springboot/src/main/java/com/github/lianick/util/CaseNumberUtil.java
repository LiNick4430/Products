package com.github.lianick.util;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.model.eneity.DailyCount;
import com.github.lianick.repository.DailyCountRepository;

/**
 * 負責 產生 唯一的 CaseNumber
 */
@Service
public class CaseNumberUtil {

	@Autowired
	private DailyCountRepository dailyCountRepository;
	
	private static final String COUNTER_KEY = "CASE_DAILY_COUNT";
	private static final String NUMBER_TOP = "CASE_";
	
	@Transactional
	public String generateNumber() {
		
		// 1. 從資料庫讀取並鎖定計數器紀錄(使用 For Update 鎖)
		Optional<DailyCount> optDailyCount = dailyCountRepository.findByName(COUNTER_KEY);
		LocalDate today = LocalDate.now();
		int newCount;
		DailyCount dailyCount;
		
		// 2. 判斷是否 紀錄首次建立
		if (optDailyCount.isEmpty()) {
			// 首次運行 第一次建立紀錄
			newCount = 1;
			dailyCount = new DailyCount(null, COUNTER_KEY, today, newCount);
		
		} else {
			// 紀錄已經存在
			dailyCount = optDailyCount.get();
			
			// 3. 判斷是否換日
			if (!dailyCount.getCurrentDate().equals(today)) {
				// 換日 重新計算
				newCount = 1;
				dailyCount.setCurrentDate(today);
				dailyCount.setCurrentCount(newCount);
			} else {
				// 非換日 計數器遞增
				newCount = dailyCount.getCurrentCount() + 1;
				dailyCount.setCurrentCount(newCount);
			}
		}
		
		// 4. 更新 並 儲存
		dailyCount = dailyCountRepository.save(dailyCount);
		
		// 5. 根據日期和計數產生字串
		// 2025-11-27 -> 20251127 -> 251127
		String nowString = today.toString().replaceAll("-", "").substring(2);
        // 321 -> 000321
		String countString = String.format("%06d", newCount);
		
		return NUMBER_TOP + nowString + "_" + countString;
	}
}
