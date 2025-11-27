package com.github.lianick.model.eneity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

// 這個表本身是多對多關聯的中間表, 通常不繼承 BaseEntity, 方便 JPA 操作可設為獨立 Entity。
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "case_priority") 	
public class CasePriority {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "case_priority_id")
	private Long casePriorityId;			// 案件-優先條件 ID
	
	@ManyToOne
	@JoinColumn(name = "case_id", nullable = false)
	private Cases cases;					// 案件 ID
	
	@ManyToOne
	@JoinColumn(name = "priority_id", nullable = false)
	private Priority priority;				// 優先條件 ID
	
	@JoinColumn(name = "case_priority_hasProof")
	private Boolean hasProof = false;		// 該條件 是否有上傳文件證明
}
