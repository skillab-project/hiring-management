// src/main/java/com/example/hiringProcess/Candidate/CandidateRepository.java
package com.example.hiringProcess.Candidate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    List<Candidate> findByJobAd_Id(Integer jobAdId);
}
