package com.example.hiringProcess.InterviewReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewReportRepository extends JpaRepository<InterviewReport, Integer> {
    @Query("""
        SELECT CASE WHEN COUNT(ir) > 0 THEN true ELSE false END 
        FROM InterviewReport ir 
        JOIN ir.interview i 
        JOIN JobAd ja ON ja.interview.id = i.id 
        JOIN ja.departments d 
        WHERE ir.id = :reportId AND d.organisation.id = :orgId
    """)
    boolean existsByIdAndOrganisationId(@Param("reportId") Integer reportId, @Param("orgId") Integer orgId);
}
