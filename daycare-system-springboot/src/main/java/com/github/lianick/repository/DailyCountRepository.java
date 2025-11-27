package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.DailyCount;

import jakarta.persistence.LockModeType;

import java.util.Optional;


@Repository
public interface DailyCountRepository extends JpaRepository<DailyCount, Long> {
	
	/**
     * 根據名稱查找計數器，並加上悲觀寫入鎖 (FOR UPDATE)
     * @param name 計數器名稱
     * @return 帶有鎖定的 DailyCount 實體
     */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<DailyCount> findByName(String name);
	
}
