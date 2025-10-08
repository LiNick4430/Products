package com.github.lianick.model.eneity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

	@Column(name = "create_date",nullable = false, updatable = false)
	private LocalDateTime createDate;		// 新增日期
	
	@Column(name = "update_date",nullable = false)
	private LocalDateTime updateDate;		// 最後更新日期
	
	// @SQLRestriction("delete_at IS NULL") 繼承的 Entity 需要添加這個 
	@Column(name = "delete_at")
	private LocalDateTime deleteAt;				// 被刪除的時間
	
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

/*
 * Service 層的實作責任
	這個 BaseEntity，你現在需要在 Service 層和 Repository 層實現邏輯刪除的邏輯：
	查詢過濾： 所有對核心 Entity (如 Cases、Users) 的查詢，預設必須加上 WHERE delete_at IS NULL 的條件。
	執行刪除： 處理刪除操作時，不是調用 repository.delete(entity)，而是：
		設置 entity.setDeleteAt(LocalDateTime.now()) (或 Instant.now())
		調用 repository.save(entity) 進行更新。
 * */
