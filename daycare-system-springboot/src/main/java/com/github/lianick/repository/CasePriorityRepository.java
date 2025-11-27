package com.github.lianick.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.CasePriority;


@Repository
public interface CasePriorityRepository extends JpaRepository<CasePriority, Long>{

	@Query(value = 
			"SELECT * FROM case_priority "
			+ "WHERE case_id = :caseId "
			+ "AND priority_id = :priorityId "
			+ "AND delete_at IS NULL "
			, nativeQuery = true)
	Optional<CasePriority> findbyCaseAndPriority(@Param("caseId") Long caseId, @Param("priorityId") Long priorityId);
}
