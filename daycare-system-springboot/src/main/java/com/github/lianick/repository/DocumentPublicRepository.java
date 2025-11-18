package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.DocumentPublic;
import com.github.lianick.model.eneity.UserPublic;

import java.util.List;


@Repository
public interface DocumentPublicRepository extends JpaRepository<DocumentPublic, Long> {

	List<DocumentPublic> findByUserPublic(UserPublic userPublic);
}
