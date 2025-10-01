package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.User_Admin;

@Repository
public interface UserAdminRepository extends JpaRepository<User_Admin, Long>{

}
