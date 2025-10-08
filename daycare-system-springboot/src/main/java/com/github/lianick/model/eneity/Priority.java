package com.github.lianick.model.eneity;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "priority")			// case 用 優先條件
@SQLRestriction("delete_at IS NULL")
public class Priority extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "priority_id")
	private Long priorityId;			// 優先條件 ID
	
	@Column(name = "priority_name", nullable = false)
	private String name;				// 優先條件 名稱
	
	@Column(name = "priority_is_active", nullable = false)
	private Boolean isActive = true;	// 是否啟用(預設 啟用)
}
