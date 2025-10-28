package com.github.lianick.model.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationUpdateDTO {
	
	private Long id;				// 機構ID
	
	private String newName;
	private String newDescription;
	private String newAddress;
	private String newPhoneNumber;
	private String newEmail;
	private String newFax;
}
