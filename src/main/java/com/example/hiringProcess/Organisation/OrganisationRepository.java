package com.example.hiringProcess.Organisation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Integer> {

    @Query("SELECT o FROM Organisation o LEFT JOIN FETCH o.departments WHERE o.id = :id")
    Organisation findByIdWithDepartments(@Param("id") Integer id);

}
