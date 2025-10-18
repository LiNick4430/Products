package com.github.lianick.model.dto.userPublic;

import java.time.LocalDate;
import java.util.Set;

import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.DocumentPublic;

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
	
	// TODO 子表格 之後需要補充
	private Set<ChildInfo> children;
	private Set<DocumentPublic> documents;
}
