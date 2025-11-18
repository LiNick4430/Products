package com.github.lianick.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用於 DocumentUtil 的 DTO
 * */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {	
	private String targetLocation;
	private String orignalFileName;
}
