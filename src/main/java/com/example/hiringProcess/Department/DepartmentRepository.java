package com.example.hiringProcess.Department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByName(String name);

    // Find all departments for a specific organization
    List<Department> findByOrganisationId(Integer orgId);

    // Find a specific department only if it belongs to that organization (Security check)
    Optional<Department> findByIdAndOrganisationId(Integer id, Integer orgId);

    boolean existsByIdAndOrganisationId(Integer id, Integer orgId);

    Optional<Department> findByNameAndOrganisationId(String name, Integer orgId);



}

