package com.github.lianick.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class BaseModel {

	@Column(name = "create_date",nullable = false, updatable = false)
	private LocalDateTime createDate;		// 新增日期
	@Column(name = "update_date",nullable = false)
	private LocalDateTime updateDate;		// 最後更新日期
	
	// 「在 Entity 第一次被存進資料庫前」執行的方法。
	@PrePersist
	protected void onCreate() {
		this.createDate = LocalDateTime.now();
		this.updateDate = LocalDateTime.now();
	}
	
	// 「在 Entity 被更新（修改）前」執行的方法。
	@PreUpdate
	protected void onUpdate() {
	    this.updateDate = LocalDateTime.now();
	}
	
}
