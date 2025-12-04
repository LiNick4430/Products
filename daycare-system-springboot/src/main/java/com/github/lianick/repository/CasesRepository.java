package com.github.lianick.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.ChildInfo;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.github.lianick.model.enums.CaseStatus;

import jakarta.persistence.LockModeType;



@Repository
public interface CasesRepository extends JpaRepository<Cases, Long> {
	
	@EntityGraph(attributePaths = {
	        "organizations",   
	        "classes",         
	        "childInfo"        
	    })
	Optional<Cases> findById(Long id);
	
	// 確保在查詢 Cases 時, 預載入 Set<CaseOrganization> Classes ChildInfo
	@EntityGraph(attributePaths = {
	        "organizations",   
	        "classes",         
	        "childInfo"        
	    })
	List<Cases> findByChildInfoIn(Collection<ChildInfo> childInfos);
	
	@EntityGraph(attributePaths = {
	        "organizations",   
	        "classes",         
	        "childInfo"        
	    })
	List<Cases> findByStatus(CaseStatus status);
	
	@Query(value = 
			"SELECT case_id FROM cases "
			+ "WHERE case_status = :status "
			+ "AND case_enrollment_deadline < :deadline "
			+ "AND delete_at IS NULL "	
			, nativeQuery = true)
	List<Long> findOverdueCaseIds(
	    @Param("status") String status,
	    @Param("deadline") LocalDateTime deadline);
	
	@EntityGraph(attributePaths = {
	        "organizations",   
	        "classes",         
	        "childInfo"        
	    })
	@Query("""
		    SELECT c FROM Cases c
		    JOIN c.organizations co
		    WHERE c.status = :status
		      AND co.organization.id = :organizationId
		      AND c.deleteAt IS NULL
		      AND co.deleteAt IS NULL
		    """)
	List<Cases> findByStatusAndOrganizationId(
            @Param("status") CaseStatus status,
            @Param("organizationId") Long organizationId);
	
	/** 新增 悲觀鎖 的 搜尋 用於 更新狀態 用 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@EntityGraph(attributePaths = {"organizations", "classes", "childInfo"})
	@Query("""
			SELECT c FROM Cases c 
			WHERE c.caseId = :caseId 
			AND c.deleteAt IS NULL 
			""")
    Optional<Cases> findByIdForUpdate(@Param("caseId") Long caseId);
}
