package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.DocumentAdmin;

@Repository
public interface DocumentAdminRepository extends JpaRepository<DocumentAdmin, Long> {

}
