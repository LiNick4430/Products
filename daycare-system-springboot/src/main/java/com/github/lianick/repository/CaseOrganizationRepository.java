package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.CaseOrganization;

@Repository
public interface CaseOrganizationRepository extends JpaRepository<CaseOrganization, Long>{

}
