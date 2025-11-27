package com.github.lianick.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.CaseFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.eneity.CasePriority;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.Priority;
import com.github.lianick.repository.CasePriorityRepository;
import com.github.lianick.service.CasePriorityService;

@Service
@Transactional
public class CasePriorityServiceImpl implements CasePriorityService{

	@Autowired
	private CasePriorityRepository casePriorityRepository;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	@Override
	public Set<CasePriority> createPriorities(Cases cases, List<Long> priorityIds) {
		// 0. 檢測完整性
		if (cases == null || priorityIds.size() == 0) {
			throw new ValueMissException("缺少必要資訊(案件, 優先度ID)");
		}
		
		Set<CasePriority> casePriorities = new HashSet<CasePriority>();
		// 1. 檢查 優先度ID 是否已經和 案件 建立關聯
		for (Long priorityId : priorityIds) {
			Priority priority = entityFetcher.getPriority(priorityId);
			if (casePriorityRepository.findbyCaseAndPriority(cases.getCaseId(), priority.getPriorityId()).isPresent()) {
				// 案件 和 優先度 已經關聯
				throw new CaseFailureException("優先度( " + priority.getName() + " )已經和案件關聯");
			}
			
			CasePriority casePriority = new CasePriority();
			casePriority.setCases(cases);
			casePriority.setPriority(priority);
			// casePriority = casePriorityRepository.save(casePriority);
			
			casePriorities.add(casePriority);
		}
		
		return casePriorities;
	}

}
