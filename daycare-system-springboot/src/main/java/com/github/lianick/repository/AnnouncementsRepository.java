package com.github.lianick.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.Announcements;


@Repository
public interface AnnouncementsRepository extends JpaRepository<Announcements, Long>{

	Optional<Announcements> findByTitle(String title);
	
}
