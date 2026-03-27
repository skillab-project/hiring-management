package com.example.hiringProcess.Organisation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Integer> {

    Optional<Organisation> findByName(String name);

    @Query("SELECT o.name FROM Organisation o WHERE o.id = :id")
    Optional<String> findNameById(@Param("id") Integer id);
}