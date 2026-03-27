package com.example.hiringProcess.Step;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END 
        FROM Step s 
        JOIN s.interview i 
        JOIN JobAd ja ON ja.interview.id = i.id 
        JOIN ja.departments d 
        WHERE s.id = :stepId AND d.organisation.id = :orgId
    """)
    boolean existsByIdAndOrganisationId(@Param("stepId") Integer stepId, @Param("orgId") Integer orgId);

    @Query("""
        SELECT DISTINCT s FROM Step s 
        JOIN s.interview i 
        JOIN JobAd ja ON ja.interview.id = i.id 
        JOIN ja.departments d 
        WHERE d.organisation.id = :orgId
    """)
    List<Step> findAllByOrganisationId(@Param("orgId") Integer orgId);

    @Query("""
    SELECT COUNT(s) FROM Step s 
    JOIN s.interview i 
    JOIN JobAd ja ON ja.interview.id = i.id 
    JOIN ja.departments d 
    WHERE s.id IN :ids AND d.organisation.id = :orgId
""")
    long countByIdInAndOrganisationId(@Param("ids") List<Integer> ids, @Param("orgId") Integer orgId);
}
