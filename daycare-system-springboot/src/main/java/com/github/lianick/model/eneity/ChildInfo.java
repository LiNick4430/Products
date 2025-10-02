package com.github.lianick.model.eneity;

import java.time.LocalDate;

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
@Table(name = "child_info")						// 幼兒基本資料
public class ChildInfo extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "child_id")
	private Long childId;						// 幼兒 ID
	
	@Column(name = "child_name", nullable = false)
	private String name;						// 幼兒 姓名
	
	@Column(name = "child_national_id_no", nullable = false, unique = true)
	private String nationalIdNo; 				// 幼兒 身分證字號
	
	@Column(name = "child_birthdate", nullable = false)
	private LocalDate birthDate;				// 幼兒 生日
	
	@Column(name = "child_gender", nullable = false)
	private String gender;						// 幼兒 性別
	
	@ManyToOne
	@JoinColumn(name = "public_id", nullable = false)
	private UserPublic userPublic;				// 連接到 民眾 ID
}
