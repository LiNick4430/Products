package com.github.lianick.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Organization;
import com.github.lianick.model.eneity.UserAdmin;
import com.github.lianick.model.eneity.Users;
import java.util.List;


@Repository
public interface UserAdminRepository extends JpaRepository<UserAdmin, Long>{

	Optional<UserAdmin> findByUsers(Users users);
	
	List<UserAdmin> findByOrganization(Organization organization);
	
}
