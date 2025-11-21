package com.github.lianick.model.dto.organization;

import java.util.List;

import com.github.lianick.model.dto.documentAdmin.DocumentOrganizationDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDTO {

	private Long id;				// 機構ID
	
	private String name;
	private String description;
	private String address;
	private String phoneNumber;
	private String email;
	private String fax;
	
	private List<DocumentOrganizationDTO> documents;
	
}
