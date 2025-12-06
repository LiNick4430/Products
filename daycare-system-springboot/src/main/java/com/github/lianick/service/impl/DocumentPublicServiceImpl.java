package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.exception.FileStorageException;
import com.github.lianick.model.dto.DocumentDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicCreateDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicDeleteDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicFindDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicLinkDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicVerifyDTO;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.DocumentPublic;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.enums.document.DocumentType;
import com.github.lianick.model.enums.document.EntityType;
import com.github.lianick.repository.DocumentPublicRepository;
import com.github.lianick.service.DocumentPublicService;
import com.github.lianick.util.DocumentUtil;
import com.github.lianick.util.UserSecurityUtil;
import com.github.lianick.util.validate.DocumentPublicValidationUtil;

@Service
@Transactional				// 確保 完整性 
public class DocumentPublicServiceImpl implements DocumentPublicService{

	@Autowired
	private DocumentPublicRepository documentPublicRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private DocumentUtil documentUtil;
	
	@Autowired
	private UserSecurityUtil userSecurityUtil;
	
	@Autowired
	private DocumentPublicValidationUtil documentPublicValidationUtil;
	
	@Autowired
	private EntityFetcher entityFetcher;
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public List<DocumentPublicDTO> findAllDocByPublic() {
		// 0. 取得 使用方法 的 使用者權限
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		
		// 1. 
		List<DocumentPublic> documentPublics = documentPublicRepository.findByUserPublic(userPublic);
		
		List<DocumentPublicDTO> documentPublicDTOs = documentPublics
														.stream()
														.map(documentPublic -> {
															DocumentPublicDTO documentPublicDTO = modelMapper.map(documentPublic, DocumentPublicDTO.class);
															return documentPublicDTO;
														}).toList();
		
		return documentPublicDTOs;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public List<DocumentPublicDTO> findAllDocByAdmin(DocumentPublicFindDTO documentPublicFindDTO) {
		// 0. 檢查完整性
		documentPublicValidationUtil.validatePublicFindByPublic(documentPublicFindDTO);

		// 1. 找出 民眾 並找出 他的 DocumentPublic
		UserPublic userPublic = entityFetcher.getUsersPublicByUserId(documentPublicFindDTO.getUserId());
		List<DocumentPublic> documentPublics = entityFetcher.getDocumentPublicListByUserPublic(userPublic);

		List<DocumentPublicDTO> documentPublicDTOs = documentPublics
														.stream()
														.map(documentPublic -> {
															DocumentPublicDTO documentPublicDTO = modelMapper.map(documentPublic, DocumentPublicDTO.class);
															return documentPublicDTO;
														}).toList();

		return documentPublicDTOs;
	}
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public List<DocumentPublicDTO> findAllDocByCase(DocumentPublicFindDTO documentPublicFindDTO) {
		// 0. 檢查完整性
		documentPublicValidationUtil.validatePublicFindByCase(documentPublicFindDTO);
		
		// 1. 從 Case 找尋 DocumentPublic
		Cases cases = entityFetcher.getCasesById(documentPublicFindDTO.getCaseId());
		
		Set<DocumentPublic> documentPublics = cases.getDocuments();
				
		List<DocumentPublicDTO> documentPublicDTOs = documentPublics
														.stream()
														.map(documentPublic -> {
															DocumentPublicDTO documentPublicDTO = modelMapper.map(documentPublic, DocumentPublicDTO.class);
															return documentPublicDTO;
														}).toList();

		return documentPublicDTOs;
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public DocumentPublicDTO createDocumentByPublic(DocumentPublicCreateDTO documentPublicCreateDTO, MultipartFile file) {
		// 0. 取得 使用方法 的 使用者權限
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		
		Long userId = userPublic.getUsers().getUserId();
		
		// 1. 檢查 傳入的檔案 是否存在 以及 是否缺乏必要資訊 並獲取 DocumentType
		DocumentType type = documentPublicValidationUtil.validateCreatePublicFields(documentPublicCreateDTO, file);
		
		// 2. 檔案儲存
		DocumentDTO documentDTO = documentUtil.upload(userId, EntityType.USER, file, false);
		
		// 3. 資料庫檔案的建立
		DocumentPublic documentPublic = new DocumentPublic();
		
		documentPublic.setUserPublic(userPublic);
		documentPublic.setFileName(documentDTO.getOrignalFileName());
		documentPublic.setStoragePath(documentDTO.getTargetLocation());
		documentPublic.setDocType(type);
		
		documentPublic = documentPublicRepository.save(documentPublic);
		
		// 4. Entity -> DTO
		return modelMapper.map(documentPublic, DocumentPublicDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public DocumentPublicDTO createDocumentByCase(DocumentPublicCreateDTO documentPublicCreateDTO, MultipartFile file) {
		// 0. 取得 使用方法 的 使用者權限
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		Long userId = userPublic.getUsers().getUserId();

		// 1. 檢查 傳入的檔案 是否存在 以及 是否缺乏必要資訊 並獲取 DocumentType
		DocumentType type = documentPublicValidationUtil.validateCreateCaseFields(documentPublicCreateDTO, file);
		
		Cases cases = entityFetcher.getCasesById(documentPublicCreateDTO.getCaseId());
		documentPublicValidationUtil.validateUserPublicAndCases(userPublic, cases);

		// 2. 檔案儲存
		DocumentDTO documentDTO = documentUtil.upload(userId, EntityType.USER, file, false);
				
		// 3. 資料庫檔案的建立
		DocumentPublic documentPublic = new DocumentPublic();
		
		documentPublic.setUserPublic(userPublic);
		documentPublic.setFileName(documentDTO.getOrignalFileName());
		documentPublic.setStoragePath(documentDTO.getTargetLocation());
		documentPublic.setDocType(type);

		// 關聯表 需要 互相加入關聯
		documentPublic.getCases().add(cases);
		cases.getDocuments().add(documentPublic);
		
		documentPublic = documentPublicRepository.save(documentPublic);

		// 4. Entity -> DTO
		return modelMapper.map(documentPublic, DocumentPublicDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public DocumentPublicDTO documentLinkCase(DocumentPublicLinkDTO documentPublicLinkDTO) {
		// 0. 取得 使用方法 的 使用者權限
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		
		// 1. 檢查 資料完整性
		documentPublicValidationUtil.validatePublicLink(documentPublicLinkDTO);
		
		DocumentPublic documentPublic = entityFetcher.getDocumentPublicById(documentPublicLinkDTO.getId());
		documentPublicValidationUtil.validateUserPublicAndDocumentPublic(userPublic, documentPublic);
		
		Cases cases = entityFetcher.getCasesById(documentPublicLinkDTO.getCaseId());
		documentPublicValidationUtil.validateUserPublicAndCases(userPublic, cases);
		
		documentPublicValidationUtil.validateCasesAndDocumentPublic(cases, documentPublic);
		
		if (documentPublic.getIsVerified() == false) {
			throw new FileStorageException("檔案錯誤：尚未通過驗證");
		}
		
		// 2. 建立關連
		documentPublic.getCases().add(cases);
		cases.getDocuments().add(documentPublic);
		
		documentPublic = documentPublicRepository.save(documentPublic);

		// 3. Entity -> DTO
		return modelMapper.map(documentPublic, DocumentPublicDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public DocumentPublicDTO verifyDocument(DocumentPublicVerifyDTO documentPublicVerifyDTO) {
		// 1. 檢查資料完整性
		documentPublicValidationUtil.validateDocumentVerify(documentPublicVerifyDTO);
		DocumentPublic documentPublic = entityFetcher.getDocumentPublicById(documentPublicVerifyDTO.getId());
		
		// 2. 審核動作 
		LocalDateTime now = LocalDateTime.now();
		documentPublic.setIsVerified(true);
		documentPublic.setVerificationDate(now);
		
		documentPublic = documentPublicRepository.save(documentPublic);
				
		// 3. Entity -> DTO
		return modelMapper.map(documentPublic, DocumentPublicDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public void deleteDocument(DocumentPublicDeleteDTO documentPublicDeleteDTO) {
		// 0. 取得 使用方法 的 使用者權限
		UserPublic userPublic = userSecurityUtil.getCurrentUserPublicEntity();
		
		// 1. 檢查完整性
		documentPublicValidationUtil.validateDocumentDelete(documentPublicDeleteDTO);
		
		DocumentPublic documentPublic = entityFetcher.getDocumentPublicById(documentPublicDeleteDTO.getId());
		
		documentPublicValidationUtil.validateUserPublicAndDocumentPublic(userPublic, documentPublic);
		
		// 2. 檢查是否還有關連的
		if (documentPublic.getCases().size() != 0) {
			throw new FileStorageException("檔案錯誤：被案件使用 無法刪除");
		}
		
		// 3. 目標檔案 刪除
		documentUtil.delete(documentPublic.getStoragePath());
		
		// 4. 執行軟刪除
		LocalDateTime now = LocalDateTime.now();
		documentPublic.setDeleteAt(now);
		documentPublic = documentPublicRepository.save(documentPublic);
	}

}
