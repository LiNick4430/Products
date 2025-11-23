package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Regulations;
import java.util.List;
import java.util.Optional;

import com.github.lianick.model.eneity.Organization;



@Repository
public interface RegulationsRepository extends JpaRepository<Regulations, Long> {

	List<Regulations> findAllByOrganization(Organization organization);
	
	Optional<Regulations>  findByRegulationId(Long regulationId);
	
	@Query(value = 
			"SELECT * FROM regulations "
			+ "WHERE regulation_type = :type "
			+ "AND organization_id = :organizationId "
			+ "AND delete_at IS NULL "
			, nativeQuery = true)
	Optional<Regulations> findByOrganizationAndType(@Param("organizationId") Long organizationId, @Param("type") String type);
	
	@Query(value = 
			"SELECT * FROM regulations "
			+ "WHERE regulation_type = :type "
			+ "AND organization_id = :organizationId "
			+ "AND delete_at IS NOT NULL "
			, nativeQuery = true)
	Optional<Regulations> findByOrganizationAndTypeAndIsDelete(@Param("organizationId") Long organizationId, @Param("type") String type);
	
}
