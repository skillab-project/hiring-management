package com.example.hiringProcess.Candidate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {

    long countByJobAd_IdAndStatusIgnoreCase(Integer jobAdId, String status);

    // Λίστα ΟΛΩΝ των υποψηφίων ως CandidateDTO (projection)
    @Query("""
        select new com.example.hiringProcess.Candidate.CandidateDTO(
            c.id,
            c.firstName,
            c.lastName,
            c.email,
            c.status,
            c.cvPath,
            c.cvOriginalName,
            c.interviewReport.id
        )
        from Candidate c
        order by c.id
    """)
    List<CandidateDTO> findAllListDtos();

    // Λίστα υποψηφίων ενός Job Ad ως CandidateDTO (projection)
    @Query("""
        select new com.example.hiringProcess.Candidate.CandidateDTO(
            c.id,
            c.firstName,
            c.lastName,
            c.email,
            c.status,
            c.cvPath,
            c.cvOriginalName,
            c.interviewReport.id
        )
        from Candidate c
        where c.jobAd.id = :jobAdId
        order by c.id
    """)
    List<CandidateDTO> findListDtosByJobAdId(@Param("jobAdId") Integer jobAdId);

    // Τελικές βαθμολογίες (όπως πριν)
    @Query("""
        SELECT new com.example.hiringProcess.Candidate.CandidateFinalScoreDTO(
            c.id,
            c.firstName,
            c.lastName,
            c.status,
            AVG(ss.score),
            SUM(CASE WHEN ss.score IS NOT NULL THEN 1 ELSE 0 END),
            COUNT(ss)
        )
        FROM Candidate c
        LEFT JOIN SkillScore ss ON ss.candidate = c
        WHERE c.jobAd.id = :jobAdId
        GROUP BY c.id, c.firstName, c.lastName, c.status
        ORDER BY
          CASE WHEN AVG(ss.score) IS NULL THEN 1 ELSE 0 END,
          AVG(ss.score) DESC
    """)
    List<CandidateFinalScoreDTO> findFinalScoresByJobAd(@Param("jobAdId") Integer jobAdId);
}
