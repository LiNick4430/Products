package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.CasePriority;

@Repository
public interface CasePriorityRepository extends JpaRepository<CasePriority, Long>{

}
