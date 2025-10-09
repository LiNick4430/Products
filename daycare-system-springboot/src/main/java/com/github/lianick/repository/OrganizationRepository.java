package com.github.lianick.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

	// 使用 名字有 name 的來 搜尋
	@Query(value = "SELECT * FROM organization "
			+ "WHERE organization_name LIKE :name AND delete_at IS NULL"
			,nativeQuery = true)
	List<Organization> findByName(@Param("name") String name);
	
	// 使用 地址有 address 的來 搜尋
	@Query(value = "SELECT * FROM organization "
			+ "WHERE organization_address LIKE :address AND delete_at IS NULL"
			,nativeQuery = true)
	List<Organization> findByAddress(@Param("address") String address);
	
}
