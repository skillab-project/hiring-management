package com.example.hiringProcess.StepScore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepScoreRepository extends JpaRepository<StepScore, Integer> {

}
