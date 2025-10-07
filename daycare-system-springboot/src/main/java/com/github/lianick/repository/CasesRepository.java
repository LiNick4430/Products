package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Cases;

@Repository
public interface CasesRepository extends JpaRepository<Cases, Long> {

}
