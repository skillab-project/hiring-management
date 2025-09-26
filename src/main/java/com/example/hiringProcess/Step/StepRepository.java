package com.example.hiringProcess.Step;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StepRepository extends JpaRepository<Step, Integer> {

    List<Step> findByInterviewIdOrderByPositionAsc(int interviewId);

    @Query("""
       select coalesce(max(s.position), -1)
       from Step s
       where s.interview.id = :interviewId
    """)
    int findMaxPositionByInterviewId(int interviewId);

    Optional<Step> findByInterviewIdAndPosition(int interviewId, int position);
}
