package com.github.lianick.converter;

import java.util.Optional;

import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lianick.exception.RoleFailureException;
import com.github.lianick.model.eneity.Role;
import com.github.lianick.repository.RoleRepository;

@Component
public class RoleNumberToRoleConveter extends AbstractConverter<Long, Role> {

	@Autowired
	private RoleRepository roleRepository;
	
	@Override
	protected Role convert(Long source) {
		if (source == null) {
			return null;
		}
		// 1. 根據傳入的 RoleType 號碼 查找 Role 實體
		Optional<Role> optRole = roleRepository.findById(source);
		// 2. 如果找不到，可以拋出業務異常，或返回 null
		if(optRole.isEmpty()) {
			throw new RoleFailureException("無效的Role ID = " + source);
		}
		return optRole.get();
	}

}
