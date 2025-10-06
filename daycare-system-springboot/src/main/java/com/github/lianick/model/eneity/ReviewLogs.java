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
@Table(name = "review_logs")					// 審核紀錄
public class ReviewLogs extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_log_id")
	private Long reviewLogId;					// 審核 ID
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "case_id", nullable = false)
	private Cases cases;						// 審核 case ID
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	private UserAdmin userAdmin;				// 審核 人員
	
	@Column(name = "review_type", nullable = false)
	private String reviewType;					// 審核 動作
	
	@Column(name = "review_date", nullable = false)
	private LocalDateTime reviewDate;			// 審核 日期
	
	@Column(name = "review_message", columnDefinition = "TEXT")
    private String reviewMessage;                // 審核人員的意見/原因 (前台可顯示)
	
	@Column(name = "notify_date")				// 通知時間可能允許為 NULL (尚未通知)
	private LocalDateTime notifyDate;			// 通知時間
	
	@Column(name = "notify_type") 				// 通知方式可能允許為 NULL (尚未通知)
	private String notifyType;					// 通知方式 (例如：Email, 簡訊)
}
