package com.example.hiringProcess.Candidate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {

    //    boolean existsByIdAndOrganisationId(Integer id, Integer orgId);
    // 1. The "Exists" check for security
    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END 
            FROM Candidate c 
            JOIN c.jobAd ja 
            JOIN ja.departments d 
            WHERE c.id = :id AND d.organisation.id = :orgId
        """)
    boolean existsByIdAndOrganisationId(@Param("id") Integer id, @Param("orgId") Integer orgId);

    // 2. The "Find One" check (Useful for your GET /{id} endpoint)
    @Query("""
        SELECT c FROM Candidate c 
        JOIN c.jobAd ja 
        JOIN ja.departments d 
        WHERE c.id = :id AND d.organisation.id = :orgId
    """)
    Optional<Candidate> findByIdAndOrganisationId(@Param("id") Integer id, @Param("orgId") Integer orgId);


    long countByJobAd_IdAndStatusIgnoreCase(Integer jobAdId, String status);

//    // Λίστα ΟΛΩΝ των υποψηφίων ως CandidateDTO (projection)
//    @Query("""
//        select new com.example.hiringProcess.Candidate.CandidateDTO(
//            c.id,
//            c.firstName,
//            c.lastName,
//            c.email,
//            c.status,
//            c.cvPath,
//            c.cvOriginalName,
//            c.interviewReport.id
//        )
//        from Candidate c
//        order by c.id
//    """)
//    List<CandidateDTO> findAllListDtos();

    @Query("""
        select DISTINCT new com.example.hiringProcess.Candidate.CandidateDTO(
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
        join c.jobAd ja
        join ja.departments d
        where d.organisation.id = :orgId
        order by c.id
    """)
    List<CandidateDTO> findAllListDtosByOrgId(@Param("orgId") Integer orgId);

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
