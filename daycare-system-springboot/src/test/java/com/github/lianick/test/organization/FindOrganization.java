package com.github.lianick.test.organization;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.lianick.model.eneity.Organization;
import com.github.lianick.repository.OrganizationRepository;

import jakarta.transaction.Transactional;

@SpringBootTest		// 載入 Spring Boot 應用程式的完整上下文
@Transactional		// 確保整個測試類別在事務中運行
public class FindOrganization {

	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Test
	public void find() {
		// 變數
		Long organizationId = 1L;
		String organizationName = "陽光寶貝";
		String organizationAddress = "台中";
		
		// 找尋目標機構 ID
		Optional<Organization> optOrganization = organizationRepository.findById(organizationId);
		if (optOrganization.isEmpty()) {
			System.out.printf("機構 ID = %d, 錯誤", organizationId);
			return;
		}
		System.out.println(optOrganization.get().getName());
		// 找尋目標機構 包含名字	需要手動在前後加上 %
		List<Organization> organizations = organizationRepository.findByNameLike("%" + organizationName + "%");
		if (organizations.isEmpty()) {
			System.out.printf("機構 名稱 不包含 %s, 錯誤", organizationName);
			return;
		}
		organizations.forEach(organization -> {
			System.out.println(organization.getName());
		});
		// 找尋目標機構 包含 地址
		List<Organization> organizations2 = organizationRepository.findByAddressLike("%" + organizationAddress + "%");
		if (organizations2.isEmpty()) {
			System.out.printf("機構 地址 不包含 %s, 錯誤", organizationAddress);
			return;
		}
		organizations2.forEach(organization -> {
			System.out.println(organization.getName());
		});
		
	}
}
