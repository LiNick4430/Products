package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.DocumentPublic;
import com.github.lianick.model.eneity.UserPublic;

import java.util.List;


@Repository
public interface DocumentPublicRepository extends JpaRepository<DocumentPublic, Long> {

	List<DocumentPublic> findByUserPublic(UserPublic userPublic);
	
	@Query(value = 
			"SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END "
			+ "FROM case_link_document_public "
			+ "WHERE case_id = :caseId "
			+ "AND public_doc_id = :documentPublicId "
			, nativeQuery = true)
	boolean existsByCaseIdAndDocumentPublicId(@Param("caseId") Long caseId, @Param("documentPublicId") Long documentPublicId);
	
}
