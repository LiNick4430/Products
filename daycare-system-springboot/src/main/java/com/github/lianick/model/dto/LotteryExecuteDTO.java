package com.github.lianick.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LotteryExecuteDTO {

	private Long organizationId;		// 執行 抽獎行為 的 機構
}
