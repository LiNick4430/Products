package com.github.lianick.service;

import java.util.List;

import com.github.lianick.model.dto.documentPublic.DocumentPublicDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicFindDTO;

// 民眾附件
public interface DocumentPublicService {

	/**
	 * 尋找 該 民眾 底下 所有 的 附件
	 * */
	List<DocumentPublicDTO> findAllDocByPublic(DocumentPublicFindDTO documentPublicFindDTO);
	
	/**
	 * 尋找 該 案件 底下 所有 的 附件
	 * */
	List<DocumentPublicDTO> findAllDocByCase(DocumentPublicFindDTO documentPublicFindDTO);
	
	/**
	 * 民眾 建立 新的 附件
	 * */
	
	/**
	 * 民眾 為了案件 建立 新的 附件
	 * */
	
	/**
	 * 員工 審核 附件
	 * */
	
	/**
	 * 刪除 附件
	 * */
	
}
