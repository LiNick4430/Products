package com.github.lianick.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDTO {

	private Long id;
	
	private String name;
	private String description;
	private String address;
	private String phoneNumber;
	private String email;
	private String fax;
	
}
