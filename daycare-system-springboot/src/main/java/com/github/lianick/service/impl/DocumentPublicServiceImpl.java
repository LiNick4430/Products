package com.github.lianick.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.config.FileProperties;
import com.github.lianick.exception.CaseFailureException;
import com.github.lianick.exception.FileStorageException;
import com.github.lianick.exception.UserNoFoundException;
import com.github.lianick.exception.ValueMissException;
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
import com.github.lianick.repository.CasesRepository;
import com.github.lianick.repository.DocumentPublicRepository;
import com.github.lianick.repository.UserPublicRepository;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.service.DocumentPublicService;
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
	private FileProperties fileProperties;
	
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
		if (documentPublicCreateDTO.getType() == null || documentPublicCreateDTO.getType().isBlank()) {
			throw new ValueMissException("缺少特定資料(檔案類型)");
		}
		
		// 2. 檔案儲存
		// 組合路徑 (基本 + 帳號ID)
		String uploadDirWithUser = fileProperties.getUploadPath() + userId;
		Path uploadPath = Paths.get(uploadDirWithUser);
		Path targetLocation = null;
		
		// 建立 唯一的 檔案名稱
		String orignalFileName = file.getOriginalFilename();
		String storedFileName = UUID.randomUUID().toString() + "_" + orignalFileName;
		
		// I/O 區塊
		try {
			// 確認 目標路徑存在(如果不存在，則會自動創建)
			Files.createDirectories(uploadPath);
			
			// 組合最終目標
			targetLocation = uploadPath.resolve(storedFileName);
			
			// 儲存檔案
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new FileStorageException("檔案錯誤：儲存失敗，請檢查伺服器路徑權限或磁碟空間。", e);
		}
		
		// 3. 資料庫檔案的建立
		DocumentPublic documentPublic = new DocumentPublic();
		documentPublic.setUserPublic(userPublic);
		documentPublic.setFileName(orignalFileName);
		documentPublic.setStoragePath(targetLocation.toString());
		documentPublic.setDocType(documentPublicCreateDTO.getType());
		
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
		if (documentPublicCreateDTO.getType() == null || documentPublicCreateDTO.getType().isBlank() ||
				documentPublicCreateDTO.getCaseId() == null) {
			throw new ValueMissException("缺少特定資料(檔案類型, 案件ID)");
		}
		
		Cases cases = casesRepository.findById(documentPublicCreateDTO.getCaseId())
				.orElseThrow(() -> new CaseFailureException("案件不存在"));
		if (!cases.getChildInfo().getUserPublic().equals(userPublic)) {
			throw new AccessDeniedException("案件不存在");
		}

		// 2. 檔案儲存
		// 組合路徑 (基本 + 帳號ID)
		String uploadDirWithUser = fileProperties.getUploadPath() + userId;
		Path uploadPath = Paths.get(uploadDirWithUser);
		Path targetLocation = null;

		// 建立 唯一的 檔案名稱
		String orignalFileName = file.getOriginalFilename();
		String storedFileName = UUID.randomUUID().toString() + "_" + orignalFileName;

		// I/O 區塊
		try {
			// 確認 目標路徑存在(如果不存在，則會自動創建)
			Files.createDirectories(uploadPath);

			// 組合最終目標
			targetLocation = uploadPath.resolve(storedFileName);

			// 儲存檔案
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new FileStorageException("檔案錯誤：儲存失敗，請檢查伺服器路徑權限或磁碟空間。", e);
		}
				
		// 3. 資料庫檔案的建立
		DocumentPublic documentPublic = new DocumentPublic();
		documentPublic.setUserPublic(userPublic);
		documentPublic.getCases().add(cases);
		documentPublic.setFileName(orignalFileName);
		documentPublic.setStoragePath(targetLocation.toString());
		documentPublic.setDocType(documentPublicCreateDTO.getType());

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
			throw new AccessDeniedException("附件不存在");
		}
		
		// 2. 檢查是否還有關連的
		if (documentPublic.getCases().size() != 0) {
			throw new FileStorageException("檔案錯誤：被案件使用 無法刪除");
		}
		
		// 3. 執行軟刪除
		Path targetLocation = Paths.get(documentPublic.getStoragePath());
		LocalDateTime now = LocalDateTime.now();
		
		try {
			Files.delete(targetLocation);
		} catch (IOException e) {
			throw new FileStorageException("檔案錯誤：刪除失敗", e);
		}
		
		documentPublic.setDeleteAt(now);
		documentPublic = documentPublicRepository.save(documentPublic);
	}
}
