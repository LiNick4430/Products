package com.github.lianick.service;

import java.util.List;
import java.util.Set;

import com.github.lianick.model.eneity.CasePriority;
import com.github.lianick.model.eneity.Cases;

public interface CasePriorityService {

	// 建立 關聯
	Set<CasePriority> createPriorities(Cases cases, List<Long> priorityIds);
}
