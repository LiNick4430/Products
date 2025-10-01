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
@Table(name = "announcements")	// 公告
public class Announcements extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "announcements_id")
	private Long announcementsId;	// 公告 ID
	
	@Column(name = "title", nullable = false)
	private String title;			// 公告 標題
	
	@Column(name = "content", columnDefinition = "TEXT", nullable = false)
	private String content;			// 公告 內容
	
	@Column(name = "publish_date", nullable = false)
	private LocalDateTime publishDate;	// 公告 實際發布時間
	
	@Column(name = "is_published", nullable = false)
	private Boolean isPublished = false;		// 預設為未發布 (草稿)
	
	@Column(name = "expiry_date ")
	private LocalDateTime expiryDate;	// 公告 到期日期 (可為 NULL)
	
	// 機構關聯 (多對一)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id", nullable = false)	// 誰發布的
	private Organization organization;
	
}
