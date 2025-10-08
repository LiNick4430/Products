package com.github.lianick.model.eneity;

import org.hibernate.annotations.SQLRestriction;

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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "regulations")			// 規範
@SQLRestriction("delete_at IS NULL")
public class Regulations extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "regulation_id")
	private Long regulationId;			// 規範 ID
	
	@Column(name = "regulation_type", nullable = false, unique = true)
	private String type;				// 規範 類型
	
	@Column(name = "regulation_content", columnDefinition = "TEXT", nullable = false)
	private String content;				// 規範 內容
	
	@ManyToOne
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;	// 規範 機構
}
