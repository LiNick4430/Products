package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.UserPublic;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChildInfoRepository extends JpaRepository<ChildInfo, Long> {

	List<ChildInfo> findByUserPublic(UserPublic userPublic);
	
	Optional<ChildInfo> findByNationalIdNo(String nationalIdNo);
}
