package com.github.lianick.model.dto.userPublic;

import java.time.LocalDate;
import java.util.List;

import com.github.lianick.model.dto.child.ChildDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicDTO {	// 搜尋 該民眾 的時候用的

	private Long id;						// 帳號ID/民眾ID
	private String username;				// 帳號名
	
	// 基本資料
	private String name;
	private String nationalIdNo;
	private LocalDate birthdate;
	private String registeredAddress;
	private String mailingAddress;
	
	private List<ChildDTO> childDTOs;
	// private Set<DocumentPublic> documents;
}
