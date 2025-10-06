package com.github.lianick.model.eneity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "public_document")				// 民眾用 附件
public class DocumentPublic extends BaseDocument{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "public_doc_id")
	private Long publicDocId;					// 附件 ID
	
	// 核心業務關聯 (至少一個不為 NULL)
	// 必須在 Service 層或資料庫 CHECK 約束中確保：
    // (case_id IS NOT NULL OR public_id IS NOT NULL)
    // 否則文件會成為無主數據。
	
	// 案件相關文件
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "case_id")
	private Cases cases;
	// 個人文件庫
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "public_id")
	private UserPublic userPublic;
	
	// 民眾 文件 專用
	
	@Column(name = "public_doc_is_verified" , nullable = false)
	private Boolean isVerified = false;			// 文件 是否 通過 後台審核
	
	@Column(name = "public_doc_verification_date")
	private LocalDateTime verificationDate;		// 審核通過時間
	
}
