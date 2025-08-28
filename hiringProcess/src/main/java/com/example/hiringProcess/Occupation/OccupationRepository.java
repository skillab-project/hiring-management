package com.example.hiringProcess.Occupation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OccupationRepository extends JpaRepository<Occupation, Integer> {
    @Query("select c from Occupation c where c.title = ?1")
    Optional<Occupation> findOccupationByTitle(String title);

    @Query("""
           select distinct o
           from Occupation o
           join o.jobAds ja
           join ja.departments d
           where d.id = :deptId
           """)
    List<Occupation> findAllByDepartmentIdViaJobAds(@Param("deptId") Integer deptId);
}
