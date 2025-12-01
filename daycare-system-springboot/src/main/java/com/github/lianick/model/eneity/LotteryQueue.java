package com.github.lianick.model.eneity;

import org.hibernate.annotations.SQLRestriction;

import com.github.lianick.model.enums.LotteryQueueStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lottery_queue",			// 候補/抽籤 序列
		uniqueConstraints = {
			@UniqueConstraint(
					name = "UK_case_organization_unique",
					columnNames = {"case_id", "organization_id"})	// 確保 (案件ID, 機構ID) 的組合是唯一的
		})
@SQLRestriction("delete_at IS NULL")
public class LotteryQueue extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lottery_queue_id")
	private Long lotteryQueueId;			// 序列ID
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "case_id", nullable = false)
	private Cases cases;					// 案件ID
	
	// 可以從 cases 反查 childInfo, 為了 方便查詢 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_id", nullable = false)
	private ChildInfo childInfo;			// 幼兒ID

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;		// 機構ID
	
	@Column(name = "lottery_queue_order", nullable = false)
	private Integer lotteryOrder;			// 抽籤 序位	只放入柱列 = -1
	
	@Column(name = "lottery_queue_alternate_number")
	private Integer alternateNumber;		// 候補 排序
	
	@Enumerated(EnumType.STRING)
	@Column(name = "lottery_queue_status", nullable = false)
	private LotteryQueueStatus status;		// 目前狀態
	
	@Version
	@Column(name = "version", nullable = false)
	private Long version;	// 樂觀鎖
}
