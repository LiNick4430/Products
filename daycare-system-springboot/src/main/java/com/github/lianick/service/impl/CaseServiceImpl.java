package com.github.lianick.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lianick.model.dto.cases.CaseClassDTO;
import com.github.lianick.model.dto.cases.CaseCompleteDTO;
import com.github.lianick.model.dto.cases.CaseCreateDTO;
import com.github.lianick.model.dto.cases.CaseDTO;
import com.github.lianick.model.dto.cases.CaseFindAdminDTO;
import com.github.lianick.model.dto.cases.CaseFindPublicDTO;
import com.github.lianick.model.dto.cases.CaseQueneDTO;
import com.github.lianick.model.dto.cases.CaseVerifyDTO;
import com.github.lianick.model.dto.cases.CaseWithdrawnDTO;
import com.github.lianick.service.CaseService;

@Service
@Transactional		// 確保 完整性 
public class CaseServiceImpl implements CaseService {

	@Override
	public List<CaseDTO> findAllByPublic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CaseDTO findByPublic(CaseFindPublicDTO caseFindPublicDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CaseDTO createNewCase(CaseCreateDTO caseCreateDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CaseDTO withdrawnCase(CaseWithdrawnDTO caseWithdrawnDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CaseDTO> findAllByAdmin(CaseFindAdminDTO caseFindAdminDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CaseDTO findByAdmin(CaseFindAdminDTO caseFindAdminDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CaseDTO verifyCase(CaseVerifyDTO caseVerifyDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CaseDTO intoQueueCase(CaseQueneDTO caseQueneDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CaseDTO intoClassCase(CaseClassDTO caseClassDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void completedCase(CaseCompleteDTO caseCompleteDTO) {
		// TODO Auto-generated method stub
		
	}

}
