package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.ReviewLogs;

@Repository
public interface ReviewLogsRepository extends JpaRepository<ReviewLogs, Long> {

	
	@Query(value = 
			"SELECT COUNT(*) FROM review_logs "
			+ "WHERE case_id = :caseId "
			+ "AND review_from_status = :from "
			+ "AND review_type = :type "
			+ "AND delete_at IS NULL "
			, nativeQuery = true)
	Long countByCaseIdAndFromAndType(
			@Param("caseId") Long caseId,
			@Param("from") String from, 
			@Param("type") String type);
}
