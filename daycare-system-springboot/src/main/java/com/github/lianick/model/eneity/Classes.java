package com.github.lianick.model.eneity;

import java.util.Set;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "classes")					// 班級
@SQLRestriction("delete_at IS NULL")
public class Classes extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "class_id")
	private Long classId;					// 班級 ID
	
	@Column(name = "class_name", nullable = false)
	private String name;					// 班級名稱
	
	@Column(name = "class_max_capacity", nullable = false)
	private Integer maxCapacity;			// 班級 最大人數
	
	@Column(name = "class_current_count", nullable = false)
	private Integer currentCount = 0;		// 班級 目前人數(預設 0)
	
	@Column(name = "class_age_min_months", nullable = false)
	private Integer ageMinMonths;			// 最小公托年齡
	
	@Column(name = "class_age_max_months", nullable = false)
	private Integer ageMaxMonths;			// 最大公托年齡
	
	@Column(name = "class_service_start_month", nullable = false)
	private Integer serviceStartMonth;		// 服務起始月份
	
	@Column(name = "class_service_end_month", nullable = false)
	private Integer serviceEndMonth;		// 服務結束月份
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;		// 對應的 機構ID
	
	@OneToMany(mappedBy = "classes")
	private Set<Cases> cases;				// 旗下關連的 CASE(CHILD)
}
