package com.github.lianick.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.exception.CaseFailureException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.eneity.CaseOrganization;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.enums.CaseOrganizationStatus;
import com.github.lianick.model.enums.PreferenceOrder;
import com.github.lianick.repository.CaseOrganizationRepository;
import com.github.lianick.service.CaseOrganizationService;

@Service
@Transactional		// 確保 完整性 
public class CaseOrganizationServiceImpl implements CaseOrganizationService{

	@Autowired
	private CaseOrganizationRepository caseOrganizationRepository;
	
	@Override
	public CaseOrganization createOrganization(Cases cases, Organization organization, Boolean isFirst) {
		// 0. 檢查完整性 與 重複創建
		if (cases == null || organization == null || isFirst == null) {
			throw new ValueMissException("缺少必要資訊(案件, 機構, 關聯)");
		}
		
		// 1. 判斷是否經存在
		if (caseOrganizationRepository.findByCasesAndOrganization(cases.getCaseId(), organization.getOrganizationId()).isPresent()) {
			throw new CaseFailureException("機構關係已經存在");
		}
		
		// 2. 判斷 是否是已經存在的 順序
		PreferenceOrder preferenceOrder = isFirst ? PreferenceOrder.FIRST : PreferenceOrder.SECOND;
		if (caseOrganizationRepository.findByCasesAndPreferenceOrder(cases.getCaseId(), preferenceOrder.getCode()).isPresent()) {
			throw new CaseFailureException("機構關係已經存在");
		}
		
		// 3. 建立關係
		CaseOrganization caseOrganization = new CaseOrganization();
		caseOrganization.setCases(cases);
		caseOrganization.setOrganization(organization);
		caseOrganization.setPreferenceOrder(preferenceOrder);
		caseOrganization.setStatus(CaseOrganizationStatus.APPLIED);
		// caseOrganization = caseOrganizationRepository.save(caseOrganization);
		
		return caseOrganization;
	}

}
