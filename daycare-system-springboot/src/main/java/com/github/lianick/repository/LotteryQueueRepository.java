package com.github.lianick.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.LotteryQueue;
import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.enums.LotteryQueueStatus;


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
	
	@Query(value = 
			"SELECT * FROM lottery_queue "
			+ "WHERE case_id = :caseId "
			+ "AND organization_id = :organizationId "
			+ "AND delete_at IS NULL "
			+ ")"
			, nativeQuery = true)
	Optional<LotteryQueue> findByCaseIdAndOrganizationId(
			@Param("caseId") Long caseId,
			@Param("organizationId") Long organizationId);
	
	@EntityGraph(attributePaths = {
	        "organization",   
	        "cases",         
	        "childInfo"        
	    })
	@Query("""
		    SELECT l FROM LotteryQueue l
		    WHERE l.status = :status
		      AND l.organization = :organization
		      AND l.deleteAt IS NULL
		    """)
	List<LotteryQueue> findByOrganizationAndStatus(
			@Param("organization") Organization organization,
			@Param("status") LotteryQueueStatus status
			);
	
	@EntityGraph(attributePaths = {
	        "organization",   
	        "cases",         
	        "childInfo"        
	    })
	@Query("""
		    SELECT l FROM LotteryQueue l
		    WHERE l.status = :status
		      AND l.deleteAt IS NULL
		    """)
	List<LotteryQueue> findByStatus(
			@Param("status") LotteryQueueStatus status
			);
}
