package com.example.hiringProcess.StepResults;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepResultsRepository extends JpaRepository<StepResults, Integer> {
}
