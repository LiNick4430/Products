package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.LotteryQueue;

@Repository
public interface LotteryQueueRepository extends JpaRepository<LotteryQueue, Long> {

	@Query(value = 
			"SELECT EXISTS( "
			+ "SELECT 1 FROM lottery_queue "
			+ "WHERE case_id = :caseId "
			+ "AND organization_id = :organizationId "
			+ "AND delete_at IS NULL "
			+ ")"
			, nativeQuery = true)
	boolean existsByCaseAndOrg(
			@Param("caseId") Long caseId,
			@Param("organizationId") Long organizationId);
	
}
