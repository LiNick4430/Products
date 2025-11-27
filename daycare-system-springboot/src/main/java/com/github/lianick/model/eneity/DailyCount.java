package com.github.lianick.model.eneity;

import java.time.LocalDate;

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
@Table(name = "daily_count")
public class DailyCount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "daily_count_id")
	private Long id;
	
	@Column(name = "daily_count_name")
	private String name;
	
	@Column(name = "daily_count_current_date")
	private LocalDate currentDate;
	
	@Column(name = "daily_count_current_count")
	private Integer currentCount;
}
