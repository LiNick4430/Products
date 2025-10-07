package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.Users;

import java.util.Optional;


@Repository
public interface UserPublicRepository extends JpaRepository<UserPublic, Long>{

	Optional<UserPublic> findByUsers(Users users);
}
