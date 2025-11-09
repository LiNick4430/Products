package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Classes;
import com.github.lianick.model.eneity.Organization;

import java.util.List;


@Repository
public interface ClassesRepository extends JpaRepository<Classes, Long> {

	List<Classes> findByOrganization(Organization organization);
	
}
