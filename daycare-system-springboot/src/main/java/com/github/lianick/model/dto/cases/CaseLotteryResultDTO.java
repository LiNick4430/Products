package com.github.lianick.model.dto.cases;

import com.github.lianick.model.enums.LotteryResultStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaseLotteryResultDTO {

	private Long caseId; 
	private Long organizationId; 				// 哪個機構的志願產生了結果 (1st or 2nd)
	private LotteryResultStatus resultStatus; 	// 抽籤結果 (e.g., SUCCESS, WAITLIST, FAILED)
}
