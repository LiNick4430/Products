package com.github.lianick.util.validate;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.exception.FileStorageException;
import com.github.lianick.exception.ValueMissException;
import com.github.lianick.model.dto.documentPublic.DocumentPublicCreateDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicDeleteDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicLinkDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicVerifyDTO;
import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.DocumentPublic;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.enums.DocumentScope;

/**
 * 負責處理 DocumentPublic 相關的 完整性 檢測
 * */
@Service
public class DocumentPublicValidationUtil {

	/**
	 * 處理 DocumentPublicCreateDTO 和 file 的完整性
	 */
	public void validateCreatePublicFields(DocumentPublicCreateDTO documentPublicCreateDTO, MultipartFile file) {
		if (file.isEmpty()) {
			throw new FileStorageException("檔案錯誤：上傳檔案不存在");
		}
		if (documentPublicCreateDTO.getDocType() == null) {
			throw new ValueMissException("缺少特定資料(附件類型)");
		}
		if (documentPublicCreateDTO.getDocType().getScope() != DocumentScope.PUBLIC) {
			throw new AccessDeniedException("錯誤的附件類型");
		}
	}
	
	/**
	 * 處理 DocumentPublicCreateDTO 和 file 的完整性
	 */
	public void validateCreateCaseFields(DocumentPublicCreateDTO documentPublicCreateDTO, MultipartFile file) {
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
