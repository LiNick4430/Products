package com.github.lianick.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Cases;
import com.github.lianick.model.eneity.ChildInfo;

import java.util.Collection;
import java.util.List;


@Repository
public interface CasesRepository extends JpaRepository<Cases, Long> {
	
	// 確保在查詢 Cases 時, 預載入 Set<CaseOrganization> Classes ChildInfo
	@EntityGraph(attributePaths = {
	        "organizations",   
	        "classes",         
	        "childInfo"        
	    })
	List<Cases> findByChildInfoIn(Collection<ChildInfo> childInfos);
}
