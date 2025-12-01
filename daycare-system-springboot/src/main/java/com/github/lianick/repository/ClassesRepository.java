package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.Organization;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;


@Repository
public interface ClassesRepository extends JpaRepository<Classes, Long> {

	Optional<Classes> findByClassId(Long classId);
	
	List<Classes> findByOrganization(Organization organization);
	
	@Query(value = 
			"SELECT COUNT(case_id) "
			+ "FROM cases "
			+ "WHERE class_id = :classId "
			+ "AND case_status IN ('COMPLETED', 'ALLOCATED') "
			+ "AND delete_at IS NULL"
			, nativeQuery = true)
	Long countActiveCasesByClassId(@Param("classId") Long classId);
	
	/** 新增 悲觀鎖 的 搜尋 用於 更新狀態 用 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = 
			"SELECT * FROM classes "
			+ "WHERE class_id = :classId "
			+ "AND delete_at IS NULL "
			, nativeQuery = true)
	Optional<Classes> findByIdForUpdate(@Param("classId") Long classId);
}
