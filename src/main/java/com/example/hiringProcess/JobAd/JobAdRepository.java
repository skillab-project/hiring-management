package com.example.hiringProcess.JobAd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobAdRepository extends JpaRepository<JobAd, Integer> {


    // Finds JobAds -> looking into Departments -> looking into Organisation -> by ID
    List<JobAd> findAllByDepartments_Organisation_Id(Integer orgId);

    // Security check: Find a specific JobAd only if it belongs to this Org
    Optional<JobAd> findByIdAndDepartments_Organisation_Id(Integer id, Integer orgId);

//    boolean existsByIdAndOrganisationId(Integer id, Integer orgId);

    @Query("SELECT ja FROM JobAd ja JOIN ja.departments d WHERE d.organisation.id = :orgId")
    List<JobAd> findAllByOrganisationId(@Param("orgId") Integer orgId);

    @Query("SELECT ja FROM JobAd ja JOIN ja.departments d WHERE ja.id = :id AND d.organisation.id = :orgId")
    Optional<JobAd> findByIdAndOrganisationId(@Param("id") Integer id, @Param("orgId") Integer orgId);

    @Query("SELECT CASE WHEN COUNT(ja) > 0 THEN true ELSE false END FROM JobAd ja JOIN ja.departments d WHERE ja.id = :id AND d.organisation.id = :orgId")
    boolean existsByIdAndOrganisationId(@Param("id") Integer id, @Param("orgId") Integer orgId);
}


