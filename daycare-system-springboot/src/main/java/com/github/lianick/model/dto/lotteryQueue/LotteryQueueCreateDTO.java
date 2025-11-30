package com.github.lianick.model.dto.lotteryQueue;

import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.Organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LotteryQueueCreateDTO {
	private Cases cases;
	private ChildInfo childInfo;
	private Organization organization;
}
