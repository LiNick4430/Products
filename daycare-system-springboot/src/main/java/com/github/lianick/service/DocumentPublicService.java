package com.github.lianick.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.model.dto.documentPublic.DocumentPublicCreateDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicDeleteDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicFindDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicLinkDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicVerifyDTO;

// 民眾附件
public interface DocumentPublicService {

	/**
	 * 尋找 該 民眾 底下 所有 的 附件<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	 * */
	List<DocumentPublicDTO> findAllDocByPublic();
	
	/**
	 * 尋找 該 案件 底下 所有 的 附件<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	List<DocumentPublicDTO> findAllDocByCase(DocumentPublicFindDTO documentPublicFindDTO);
	
	/**
	 * 民眾 建立 新的 附件<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	 * */
	DocumentPublicDTO createDocumentByPublic(DocumentPublicCreateDTO documentPublicCreateDTO, MultipartFile file);
	
	/**
	 * 民眾 為了 案件 建立 新的 附件<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	 * */
	DocumentPublicDTO createDocumentByCase(DocumentPublicCreateDTO documentPublicCreateDTO, MultipartFile file);
	
	/**
	 * 民眾 為了 案件 關連 舊有的 附件<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	 * */
	DocumentPublicDTO documentLinkCase(DocumentPublicLinkDTO documentPublicLinkDTO);
	
	/**
	 * 員工 審核 附件<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	 * */
	DocumentPublicDTO verifyDocument(DocumentPublicVerifyDTO documentPublicVerifyDTO);
	
	/**
	 * 刪除 附件<p>
	 * 需要 @PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	 * */
	void deleteDocument(DocumentPublicDeleteDTO documentPublicDeleteDTO);
	
}
