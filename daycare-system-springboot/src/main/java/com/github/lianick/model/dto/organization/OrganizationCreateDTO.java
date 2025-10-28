package com.github.lianick.model.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationCreateDTO {

	private Long id;				// 機構ID
	
	private String name;
	private String description;
	private String address;
	private String phoneNumber;
	private String email;
	private String fax;
	
}
