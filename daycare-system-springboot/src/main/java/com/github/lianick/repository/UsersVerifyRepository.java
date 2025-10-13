package com.github.lianick.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.UserVerify;

@Repository
public interface UsersVerifyRepository extends JpaRepository<UserVerify, Long> {

	
}
