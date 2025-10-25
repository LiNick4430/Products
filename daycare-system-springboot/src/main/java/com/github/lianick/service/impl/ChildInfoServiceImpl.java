package com.github.lianick.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.lianick.model.dto.userPublic.ChildCreateDTO;
import com.github.lianick.model.dto.userPublic.ChildDTO;
import com.github.lianick.model.dto.userPublic.ChildDeleteDTO;
import com.github.lianick.model.dto.userPublic.ChildUpdateDTO;
import com.github.lianick.service.ChildInfoService;

import jakarta.transaction.Transactional;

@Service
@Transactional				// 確保 完整性
public class ChildInfoServiceImpl implements ChildInfoService{

	@Override
	public List<ChildDTO> findAllChild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChildDTO findChild(ChildDTO childDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChildCreateDTO createChildInfo(ChildCreateDTO childCreateDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChildUpdateDTO updateChildInfo(ChildUpdateDTO childUpdateDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteChildInfo(ChildDeleteDTO childDeleteDTO) {
		// TODO Auto-generated method stub
		
	}

}
