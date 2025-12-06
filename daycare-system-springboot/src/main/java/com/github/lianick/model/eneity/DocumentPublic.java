package com.github.lianick.model.eneity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
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
@SQLRestriction("delete_at IS NULL")
public class DocumentPublic extends BaseDocument{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "public_doc_id")
	private Long publicDocId;					// 附件 ID
	
	// 案件相關文件 多對多關連
	@ManyToMany(mappedBy = "documents")
	private Set<Cases> cases = new HashSet<Cases>();
	
	// 個人文件庫
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "public_id", nullable = false)
	private UserPublic userPublic;
	
	// 民眾 文件 專用
	@Column(name = "public_doc_is_verified" , nullable = false)
	private Boolean isVerified = false;			// 文件 是否 通過 後台審核
	
	@Column(name = "public_doc_verification_date")
	private LocalDateTime verificationDate;		// 審核通過時間
	
}
