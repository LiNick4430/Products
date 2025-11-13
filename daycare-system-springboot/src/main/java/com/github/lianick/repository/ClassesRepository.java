package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.Organization;

import java.util.List;


@Repository
public interface ClassesRepository extends JpaRepository<Classes, Long> {

	List<Classes> findByOrganization(Organization organization);
	
	@Query(value = 
			"SELECT COUNT(case_id) "
			+ "FROM cases "
			+ "WHERE class_id = :classId "
			+ "AND case_status = 'PASSED' "
			+ "AND delete_at IS NULL"
			, nativeQuery = true)
	Long countActiveCasesByClassId(@Param("classId") Long classId);
}
