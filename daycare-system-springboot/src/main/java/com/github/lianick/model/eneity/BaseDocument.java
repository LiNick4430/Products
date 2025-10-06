package com.github.lianick.model.eneity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseDocument extends BaseEntity{

	@Column(name = "file_name", nullable = false)
	private String fileName;			// 附件 檔案名稱
	
	@Column(name = "storage_path", nullable = false)
	private String storagePath;			// 附件 存取路徑(地端/雲端)
	
	@Column(name = "document_type", nullable = false)
	private String docType;				// 附件類型 (戶籍證明/ 證件照片 / 機構資料 之類的)
	
}
