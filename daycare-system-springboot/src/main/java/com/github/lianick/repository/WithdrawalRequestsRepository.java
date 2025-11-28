package com.github.lianick.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.WithdrawalRequests;
import com.github.lianick.model.enums.WithdrawalRequestStatus;


@Repository
public interface WithdrawalRequestsRepository extends JpaRepository<WithdrawalRequests, Long> {

	@Query(value = 
			"SELECT * FROM withdrawal_requests "
			+ "WHERE case_id = :caseId "
			+ "AND withdrawal_request_audit_status IN (:statuses) "
			+ "AND delete_at IS NULL "
			, nativeQuery = true)
	List<WithdrawalRequests> findByCaseIdAndStatusIn(
										@Param("caseId") Long caseId,
										@Param("statuses") List<WithdrawalRequestStatus> statuses);
}
