package com.github.lianick.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Announcements;


@Repository
public interface AnnouncementsRepository extends JpaRepository<Announcements, Long>{

	@Query(value = 
			"SELECT * FROM announcements "
			+ "WHERE announcement_is_published = true "
			+ "AND (announcement_expiry_date IS NULL OR announcement_expiry_date > :now) "
			+ "AND delete_at IS NULL"
			, nativeQuery = true)
	List<Announcements> findAllActive(@Param("now") LocalDateTime now);
	
	@Query(value = 
			"SELECT * FROM announcements "
			+ "WHERE announcement_id = :id "
			+ "AND announcement_is_published = true "
			+ "AND (announcement_expiry_date IS NULL OR announcement_expiry_date > :now) "
			+ "AND delete_at IS NULL"
			, nativeQuery = true)
	Optional<Announcements> findAllById(@Param("id") Long id, @Param("now") LocalDateTime now);
	
	@Query(value = 
			"SELECT * FROM announcements "
			+ "WHERE announcement_is_published = false "
			+ "AND delete_at IS NULL"
			, nativeQuery = true)
	List<Announcements> findAllByNoPublish();
	
	@Query(value = 
			"SELECT * FROM announcements "
			+ "WHERE announcement_is_published = false "
			+ "AND organization_id = :organizationId "
			+ "AND delete_at IS NULL"
			, nativeQuery = true)
	List<Announcements> findAllByNoPublishAndOrganizationId(@Param("organizationId") Long organizationId);
	
	@Query(value = 
			"SELECT * FROM announcements "
			+ "WHERE announcement_id = :id "
			+ "AND announcement_is_published = false "
			+ "AND delete_at IS NULL"
			, nativeQuery = true)
	Optional<Announcements> findByIdAndNoPublish(@Param("id") Long id);
	
	@Query(value = 
			"SELECT * FROM announcements "
			+ "WHERE announcement_id = :id "
			+ "AND organization_id = :organizationId "
			+ "AND announcement_is_published = false "
			+ "AND delete_at IS NULL"
			, nativeQuery = true)
	Optional<Announcements> findByIdAndOrganizationId(@Param("id") Long id, @Param("organizationId") Long organizationId);
}
