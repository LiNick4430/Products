package com.github.lianick.model.enums;

import lombok.Getter;

//用於 分類附件 的「細項」類型。
/**
 * <p>Admin 類型 (機構內部使用):
 * <ul>
 * <li>ORGANIZATION (機構文件)</li>
 * <li>ANNOUNCEMENT (公告文件)</li>
 * </ul>
 * <p>Public 類型 (使用者上傳):
 * <ul>
 * <li>CERTIFICATE_RESIDENCE (戶籍證明)</li>
 * <li>ID_PHOTO (證件照片)</li>
 * </ul>
 */
@Getter
public enum DocumentType {	

	// ADMIN 用
	ORGANIZATION("機構文件", DocumentScope.ADMIN),
	ANNOUNCEMENT("公告文件", DocumentScope.ADMIN),
	
	// PUBLIC 用
	CERTIFICATE_RESIDENCE("戶籍證明", DocumentScope.PUBLIC),
	ID_PHOTO("證件照片", DocumentScope.PUBLIC);
	
	
	private final String description;
    private final DocumentScope scope;
    
    DocumentType(String description, DocumentScope scope) {
    	this.description = description;
    	this.scope = scope;
    }
}
