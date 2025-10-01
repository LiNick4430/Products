package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.UserAdmin;

@Repository
public interface UserAdminRepository extends JpaRepository<UserAdmin, Long>{

}
