package com.github.lianick.model.eneity;

import org.hibernate.annotations.SQLRestriction;

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
@Table(name = "admin_document")				// 民眾用 附件
@SQLRestriction("delete_at IS NULL")
public class DocumentAdmin extends BaseDocument{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "admin_doc_id")
	private Long adminDocId;					// 附件 ID
	
	// 核心業務關聯 (只能有一個 有值)
	// 必須在 Service 層或資料庫 CHECK 約束中確保：
    // (announcement_id IS NOT NULL OR organization_id IS NOT NULL)
    // 否則文件會成為無主數據。
	
	// 公告用
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "announcement_id")
	private Announcements announcements;
	
	// 機構用
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	private Organization organization;
}
