package com.example.hiringProcess.Interview;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterviewRepository extends JpaRepository<Interview, Integer> {
    @Query("""
        SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END 
        FROM Interview i 
        JOIN JobAd ja ON ja.interview.id = i.id 
        JOIN ja.departments d 
        WHERE i.id = :interviewId AND d.organisation.id = :orgId
    """)
    boolean existsByIdAndOrganisationId(@Param("interviewId") Integer interviewId, @Param("orgId") Integer orgId);
}

