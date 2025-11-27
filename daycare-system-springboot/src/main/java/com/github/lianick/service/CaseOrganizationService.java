package com.github.lianick.service;

import com.github.lianick.model.eneity.CaseOrganization;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.Organization;

public interface CaseOrganizationService {

	// 建立
	CaseOrganization createOrganization(Cases cases, Organization organization, Boolean isFirst);
}
