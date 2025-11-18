package com.github.lianick.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.exception.CaseFailureException;
import com.github.lianick.exception.FileStorageException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.exception.ValueMissException;
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
import com.github.lianick.model.eneity.Users;
import com.github.lianick.model.enums.DocumentScope;
import com.github.lianick.repository.CasesRepository;
import com.github.lianick.repository.DocumentPublicRepository;
import com.github.lianick.repository.UserPublicRepository;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.service.DocumentPublicService;
import com.github.lianick.util.DocumentUtil;
import com.github.lianick.util.SecurityUtils;

@Service
@Transactional				// 確保 完整性 
public class DocumentPublicServiceImpl implements DocumentPublicService{

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UserPublicRepository userPublicRepository;
	
	@Autowired
	private DocumentPublicRepository documentPublicRepository;
	
	@Autowired
	private CasesRepository casesRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private DocumentUtil documentUtil;
	
	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public List<DocumentPublicDTO> findAllDocByPublic() {
		// 0. 取得 使用方法 的 使用者權限
		String currentUsername = SecurityUtils.getCurrentUsername();
		Users tableUser = usersRepository.findByAccount(currentUsername)
		        .orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
		
		UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
		
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
	public List<DocumentPublicDTO> findAllDocByCase(DocumentPublicFindDTO documentPublicFindDTO) {
		
		// 1. 從 Case 找尋 DocumentPublic
		Cases cases = casesRepository.findById(documentPublicFindDTO.getCaseId())
				.orElseThrow(() -> new CaseFailureException("案件不存在"));
		
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
		String currentUsername = SecurityUtils.getCurrentUsername();
		Users tableUser = usersRepository.findByAccount(currentUsername)
		        .orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
		
		UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
		
		Long userId = tableUser.getUserId();
		
		// 1. 檢查 傳入的檔案 是否存在 以及 是否缺乏必要資訊
		if (file.isEmpty()) {
			throw new FileStorageException("檔案錯誤：上傳檔案不存在");
		}
		if (documentPublicCreateDTO.getDocType() == null) {
			throw new ValueMissException("缺少特定資料(附件類型)");
		}
		if (documentPublicCreateDTO.getDocType().getScope() != DocumentScope.PUBLIC) {
			throw new AccessDeniedException("錯誤的附件類型");
		}
		
		// 2. 檔案儲存
		DocumentDTO documentDTO = documentUtil.upload(userId, file, false);
		
		// 3. 資料庫檔案的建立
		DocumentPublic documentPublic = new DocumentPublic();
		documentPublic.setUserPublic(userPublic);
		documentPublic.setFileName(documentDTO.getOrignalFileName());
		documentPublic.setStoragePath(documentDTO.getTargetLocation());
		documentPublic.setDocType(documentPublicCreateDTO.getDocType());
		
		documentPublic = documentPublicRepository.save(documentPublic);
		
		// 4. Entity -> DTO
		return modelMapper.map(documentPublic, DocumentPublicDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public DocumentPublicDTO createDocumentByCase(DocumentPublicCreateDTO documentPublicCreateDTO, MultipartFile file) {
		// 0. 取得 使用方法 的 使用者權限
		String currentUsername = SecurityUtils.getCurrentUsername();
		Users tableUser = usersRepository.findByAccount(currentUsername)
				.orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));

		UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));

		Long userId = tableUser.getUserId();

		// 1. 檢查 傳入的檔案 是否存在 以及 是否缺乏必要資訊
		if (file.isEmpty()) {
			throw new FileStorageException("檔案錯誤：上傳檔案不存在");
		}
		if (documentPublicCreateDTO.getDocType() == null ||
				documentPublicCreateDTO.getCaseId() == null) {
			throw new ValueMissException("缺少特定資料(附件類型, 案件ID)");
		}
		if (documentPublicCreateDTO.getDocType().getScope() != DocumentScope.PUBLIC) {
			throw new AccessDeniedException("錯誤的附件類型");
		}
		
		Cases cases = casesRepository.findById(documentPublicCreateDTO.getCaseId())
				.orElseThrow(() -> new CaseFailureException("案件不存在"));
		if (!cases.getChildInfo().getUserPublic().equals(userPublic)) {
			throw new AccessDeniedException("案件不存在");
		}

		// 2. 檔案儲存
		DocumentDTO documentDTO = documentUtil.upload(userId, file, false);
				
		// 3. 資料庫檔案的建立
		DocumentPublic documentPublic = new DocumentPublic();
		documentPublic.setUserPublic(userPublic);
		documentPublic.getCases().add(cases);
		documentPublic.setFileName(documentDTO.getOrignalFileName());
		documentPublic.setStoragePath(documentDTO.getTargetLocation());
		documentPublic.setDocType(documentPublicCreateDTO.getDocType());

		documentPublic = documentPublicRepository.save(documentPublic);

		// 4. Entity -> DTO
		return modelMapper.map(documentPublic, DocumentPublicDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_PUBLIC')")
	public DocumentPublicDTO documentLinkCase(DocumentPublicLinkDTO documentPublicLinkDTO) {
		// 0. 取得 使用方法 的 使用者權限
		String currentUsername = SecurityUtils.getCurrentUsername();
		Users tableUser = usersRepository.findByAccount(currentUsername)
		        .orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
		
		UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
		
		// 1. 檢查 資料完整性
		if (documentPublicLinkDTO.getId() == null || documentPublicLinkDTO.getCaseId() == null) {
			throw new ValueMissException("缺少特定資料(附件ID, 案件ID)");
		}
		
		DocumentPublic documentPublic = documentPublicRepository.findById(documentPublicLinkDTO.getId())
				.orElseThrow(() -> new FileStorageException("附件不存在"));
		if (!documentPublic.getUserPublic().equals(userPublic)) {
			throw new AccessDeniedException("附件不存在");
		}
		
		Cases cases = casesRepository.findById(documentPublicLinkDTO.getCaseId())
				.orElseThrow(() -> new CaseFailureException("案件不存在"));
		if (!cases.getChildInfo().getUserPublic().equals(userPublic)) {
			throw new AccessDeniedException("案件不存在");
		}
		
		if (documentPublic.getIsVerified() == false) {
			throw new FileStorageException("檔案錯誤：尚未通過驗證");
		}
		
		
		// 2. 建立關連
		documentPublic.getCases().add(cases);
		documentPublic = documentPublicRepository.save(documentPublic);

		// 3. Entity -> DTO
		return modelMapper.map(documentPublic, DocumentPublicDTO.class);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_STAFF')")
	public DocumentPublicDTO verifyDocument(DocumentPublicVerifyDTO documentPublicVerifyDTO) {
		// 1. 檢查資料完整性
		if (documentPublicVerifyDTO.getId() == null) {
			throw new ValueMissException("缺少特定資料(附件ID)");
		}
		
		DocumentPublic documentPublic = documentPublicRepository.findById(documentPublicVerifyDTO.getId())
				.orElseThrow(() -> new FileStorageException("附件不存在"));
		
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
		String currentUsername = SecurityUtils.getCurrentUsername();
		Users tableUser = usersRepository.findByAccount(currentUsername)
		        .orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
		UserPublic userPublic = userPublicRepository.findByUsers(tableUser)
				.orElseThrow(() -> new UserNoFoundException("帳號不存在或已被刪除"));
		
		// 1. 檢查完整性
		if (documentPublicDeleteDTO.getId() == null ) {
			throw new ValueMissException("缺少特定資料(附件ID)");
		}
		
		DocumentPublic documentPublic = documentPublicRepository.findById(documentPublicDeleteDTO.getId())
				.orElseThrow(() -> new FileStorageException("附件不存在"));
		if (!documentPublic.getUserPublic().equals(userPublic)) {
			throw new AccessDeniedException("您無權限刪除此附件，或附件不存在於您的帳號下。");
		}
		
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
