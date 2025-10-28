package com.github.lianick.repository;

import java.util.List;
import java.util.Optional;

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
	List<Organization> findByNameLike(@Param("name") String name);
	
	// 使用 地址有 address 的來 搜尋
	@Query(value = "SELECT * FROM organization "
			+ "WHERE organization_address LIKE :address AND delete_at IS NULL"
			,nativeQuery = true)
	List<Organization> findByAddressLike(@Param("address") String address);
	
	// 使用 名字 和 地址有 name, address 的來 搜尋
	@Query(value = "SELECT * FROM organization "
			+ "WHERE organization_name LIKE :name AND "
			+ "organization_address LIKE :address AND "
			+ "delete_at IS NULL"
			,nativeQuery = true)
	List<Organization> findByNameAndAddressLike(@Param("name") String name, @Param("address") String address);
	
	Optional<Organization> findById(Long id);
	
	Optional<Organization> findByName(String name);
	
	Optional<Organization> findByPhoneNumber(String phoneNumber);
	
	Optional<Organization> findByEmail(String email);

	// 檢查是否有「非指定 ID」的機構 正在使用 這個名稱/電話/信箱
	@Query(value = "SELECT * FROM organization "
	        + "WHERE organization_name = :name AND organization_id != :id AND delete_at IS NULL"
	        , nativeQuery = true)
	Optional<Organization> findByNameAndIdNot(@Param("name") String name, @Param("id") Long id);
	@Query(value = "SELECT * FROM organization "
	        + "WHERE organization_phone = :phone AND organization_id != :id AND delete_at IS NULL"
	        , nativeQuery = true)
	Optional<Organization> findByPhoneNumberAndIdNot(@Param("phone") String phone, @Param("id") Long id);
	@Query(value = "SELECT * FROM organization "
	        + "WHERE organization_email = :email AND organization_id != :id AND delete_at IS NULL"
	        , nativeQuery = true)
	Optional<Organization> findByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
}
