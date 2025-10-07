package com.github.lianick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.lianick.model.eneity.LotteryQueue;

@Repository
public interface LotteryQueueRepository extends JpaRepository<LotteryQueue, Long> {

}
