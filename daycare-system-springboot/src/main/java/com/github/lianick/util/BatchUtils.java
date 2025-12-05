package com.github.lianick.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lianick.model.dto.cases.CaseErrorDTO;

/**
 * 批量處理 方法
 * */
public class BatchUtils {

	private static final Logger logger = LoggerFactory.getLogger(BatchUtils.class);
	
	/**	用於 處理 Case 的 單一ReadOnly大交易 包裝 大量獨立 小交易 的 泛用方法
	 * @param <T> 					-> DTO
	 * @param items 				-> DTO List
	 * @param batchSize				-> 批量 單一最大次數
	 * @param singleProcessor		-> 主要方法
	 * @param idExtractor			-> DTO 取出 ID方法
	 * @param sleepMillis			-> 中斷休息 毫秒
	 * @return List<CaseErrorDTO> 	-> 處理失敗的資料與錯誤訊息
	 * */
	public static <T> List<CaseErrorDTO> processInBatches(
			List<T> items,
			int batchSize,					
			Consumer<T> singleProcessor,		// 傳入 方法
			Function<T, Long> idExtractor,		// 傳入 取出ID 的方法
			long sleepMillis ) {
		
		logger.info("批次處理開始，共 {} 筆資料，批次大小 {}", items.size(), batchSize);
		
		List<CaseErrorDTO> errorList  = new ArrayList<>();	//失敗 案件 集合
		
		for (int i = 0; i < items.size(); i += batchSize) {
			int endIndex = Math.min(i + batchSize, items.size());
			List<T> batch = items.subList(i, endIndex);
			
			logger.info("處理第 {}~{} 筆資料", i + 1, endIndex);
			
			for (T item : batch) {
				try {
					singleProcessor.accept(item);
		        } catch (Exception e) {
		        	Long id = idExtractor.apply(item); // 用呼叫端提供的方法取得 ID
	                logger.error("案件 {} 處理失敗: {}", id, e.getMessage(), e);
	                errorList.add(new CaseErrorDTO(id, e.getMessage()));
		        }
			}
			
			if (sleepMillis > 0) {
				try {
					logger.debug("批次暫停 {} 毫秒", sleepMillis);
				    Thread.sleep(sleepMillis); // 0.2 秒
				} catch (InterruptedException e) {
				    Thread.currentThread().interrupt(); // 保留中斷狀態
				    logger.warn("批次暫停被中斷", e);
				}
			}
		}
		
		logger.info("批次處理結束，共 {} 筆成功，{} 筆失敗",
				items.size() - errorList.size(), errorList.size());
		
		return errorList;
	}
}
