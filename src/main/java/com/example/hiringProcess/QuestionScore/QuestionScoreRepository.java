package com.example.hiringProcess.QuestionScore;

import com.example.hiringProcess.Question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionScoreRepository extends JpaRepository<QuestionScore, Integer> {

    @Query("""
        SELECT CASE WHEN COUNT(qs) > 0 THEN true ELSE false END 
        FROM QuestionScore qs 
        JOIN qs.question q 
        JOIN q.step s 
        JOIN s.interview i 
        JOIN JobAd ja ON ja.interview.id = i.id 
        JOIN ja.departments d 
        WHERE qs.id = :id AND d.organisation.id = :orgId
    """)
    boolean existsByIdAndOrganisationId(@Param("id") Integer id, @Param("orgId") Integer orgId);

    @Query("""
        SELECT DISTINCT qs FROM QuestionScore qs 
        JOIN qs.question q 
        JOIN q.step s 
        JOIN s.interview i 
        JOIN JobAd ja ON ja.interview.id = i.id 
        JOIN ja.departments d 
        WHERE d.organisation.id = :orgId
    """)
    List<QuestionScore> findAllByOrganisationId(@Param("orgId") Integer orgId);
}
