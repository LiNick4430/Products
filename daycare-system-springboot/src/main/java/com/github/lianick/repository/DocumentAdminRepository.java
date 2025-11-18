package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Announcements;
import com.github.lianick.model.eneity.DocumentAdmin;
import com.github.lianick.model.eneity.Organization;

import java.util.List;
import java.util.Optional;


@Repository
public interface DocumentAdminRepository extends JpaRepository<DocumentAdmin, Long> {

	List<DocumentAdmin> findByOrganization(Organization organization);
	
	List<DocumentAdmin> findByAnnouncements(Announcements announcements);
	
	@Query(value = 
			"SELECT * FROM admin_document "
			+ "WHERE admin_doc_id = :id  AND organization_id = :organization_id "
			+ "AND admin_document.delete_at IS NULL"
			, nativeQuery = true)
	Optional<DocumentAdmin> findByIdAndOrganizationId(@Param("id") Long id, @Param("organization_id") Long organizationId);
	
	
	@Query(value = 
			"SELECT * FROM admin_document "
			+ "WHERE admin_doc_id = :id  AND announcement_id = :announcement_id "
			+ "AND admin_document.delete_at IS NULL"
			, nativeQuery = true)
	Optional<DocumentAdmin> findByIdAndAnnouncementId(@Param("id")Long id, @Param("announcement_id") Long announcementId);
}
