package com.github.lianick.util.validate;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.exception.EnumNotFoundException;
import com.github.lianick.exception.FileStorageException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.documentPublic.DocumentPublicCreateDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicDeleteDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicLinkDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicVerifyDTO;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.DocumentPublic;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.enums.document.DocumentScope;
import com.github.lianick.model.enums.document.DocumentType;

/**
 * 負責處理 DocumentPublic 相關的 完整性 檢測
 * */
@Service
public class DocumentPublicValidationUtil {

	/**
	 * 私人方法 處理 DocumentPublicCreateDTO 和 file 的完整性 並回傳 DocumentType
	 * */
	private DocumentType validateCreateFields(DocumentPublicCreateDTO documentPublicCreateDTO, MultipartFile file, Boolean isCaseUsed) {
		if (file.isEmpty()) {
			throw new FileStorageException("檔案錯誤：上傳檔案不存在");
		}
		
		if (isCaseUsed) {
			if (documentPublicCreateDTO.getDocType() == null || documentPublicCreateDTO.getDocType().isBlank() || 
					documentPublicCreateDTO.getCaseId() == null) {
				throw new ValueMissException("缺少特定資料(附件類型, 案件ID)");
			}
			
		} else {
			if (documentPublicCreateDTO.getDocType() == null || documentPublicCreateDTO.getDocType().isBlank()) {
				throw new ValueMissException("缺少特定資料(附件類型)");
			}
		}
		
		DocumentType documentType = null;
		
		try {
			documentType = DocumentType.valueOf(documentPublicCreateDTO.getDocType());
		} catch (IllegalArgumentException e) {
			throw new EnumNotFoundException("附件類型 錯誤");
		}
		
		if (documentType.getScope() != DocumentScope.PUBLIC) {
			throw new AccessDeniedException("錯誤的附件類型，此接口僅支援公共範圍的附件。");
		}
		
		return documentType;
	}
	
	/**
	 * Public 處理 DocumentPublicCreateDTO 和 file 的完整性 並回傳 DocumentType
	 */
	public DocumentType validateCreatePublicFields(DocumentPublicCreateDTO documentPublicCreateDTO, MultipartFile file) {
		return validateCreateFields(documentPublicCreateDTO, file, false);
	}
	
	/**
	 * Case 處理 DocumentPublicCreateDTO 和 file 的完整性 並回傳 DocumentType
	 */
	public DocumentType validateCreateCaseFields(DocumentPublicCreateDTO documentPublicCreateDTO, MultipartFile file) {
		return validateCreateFields(documentPublicCreateDTO, file, true);
	}
	
	/**
	 * 處理 DocumentPublicLinkDTO 的完整性
	 * */
	public void validatePublicLink(DocumentPublicLinkDTO documentPublicLinkDTO) {
		if (documentPublicLinkDTO.getId() == null || documentPublicLinkDTO.getCaseId() == null) {
			throw new ValueMissException("缺少特定資料(附件ID, 案件ID)");
		}
	}
	
	/**
	 * 處理 DocumentPublicVerifyDTO 的完整性
	 * */
	public void validateDocumentVerify(DocumentPublicVerifyDTO documentPublicVerifyDTO) {
		if (documentPublicVerifyDTO.getId() == null) {
			throw new ValueMissException("缺少特定資料(附件ID)");
		}
	}
	
	/**
	 * 處理 DocumentPublicVerifyDTO 的完整性
	 * */
	public void validateDocumentDelete(DocumentPublicDeleteDTO documentPublicDeleteDTO) {
		if (documentPublicDeleteDTO.getId() == null ) {
			throw new ValueMissException("缺少特定資料(附件ID)");
		}
	}
	
	/**
	 * 處理 UserPublic 和 Cases 的 關係
	 */
	public void validateUserPublicAndCases(UserPublic userPublic, Cases cases) {
		if (!cases.getChildInfo().getUserPublic().equals(userPublic)) {
			throw new AccessDeniedException("查無案件");
		}
	}
	
	/**
	 * 處理 UserPublic 和 DocumentPublic 的 關係
	 */
	public void validateUserPublicAndDocumentPublic(UserPublic userPublic, DocumentPublic documentPublic) {
		if (!documentPublic.getUserPublic().equals(userPublic)) {
			throw new AccessDeniedException("查無附件");
		}
	}
}
