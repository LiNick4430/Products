package com.github.lianick.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.CaseOrganization;

import jakarta.persistence.LockModeType;

@Repository
public interface CaseOrganizationRepository extends JpaRepository<CaseOrganization, Long>{

	@Query(value = 
			"SELECT * FROM case_organization "
			+ "WHERE case_id = :caseId "
			+ "AND organization_id = :organizationId "
			+ "AND delete_at IS NULL "
			, nativeQuery = true)
	Optional<CaseOrganization> findByCasesAndOrganization(@Param("caseId") Long caseId, @Param("organizationId") Long organizationId);
	
	/** 新增 悲觀鎖 的 搜尋 用於 更新狀態 用 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = 
			"SELECT * FROM case_organization "
			+ "WHERE case_id = :caseId "
			+ "AND organization_id = :organizationId "
			+ "AND delete_at IS NULL "
			, nativeQuery = true)
	Optional<CaseOrganization> findByCasesAndOrganizationForUpdate(@Param("caseId") Long caseId, @Param("organizationId") Long organizationId);
	
	@Query(value = 
			"SELECT * FROM case_organization "
			+ "WHERE case_id = :caseId "
			+ "AND case_organization_preference_order = :preferenceOrder "
			+ "AND delete_at IS NULL "
			, nativeQuery = true)
	Optional<CaseOrganization> findByCasesAndPreferenceOrder(@Param("caseId") Long caseId, @Param("preferenceOrder") String preferenceOrder);
	
}
