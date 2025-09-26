package com.example.hiringProcess.Department;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository,
                             DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    public List<DepartmentNameDTO> getDepartmentNames() {
        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toNameDTO)
                .toList();
    }

    public Optional<Department> getDepartment(Integer departmentId) {
        return departmentRepository.findById(departmentId);
    }

    public Department addNewDepartment(Department department) {
        department.setId(0); // force insert
        return departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Integer departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalStateException("Department with id " + departmentId + " does not exist"));

        // Αποσύνδεση σχέσεων
        department.getJobAds().forEach(jobAd -> jobAd.getDepartments().remove(department));
        department.setOrganisation(null);

        departmentRepository.delete(department);
    }

    @Transactional
    public Department updateDepartment(Integer departmentId, Department updatedFields) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Department with id " + departmentId + " does not exist"));

        if (updatedFields.getName() != null &&
                !updatedFields.getName().isEmpty() &&
                !Objects.equals(department.getName(), updatedFields.getName())) {
            department.setName(updatedFields.getName());
        }

        if (updatedFields.getLocation() != null &&
                !updatedFields.getLocation().isEmpty() &&
                !Objects.equals(department.getLocation(), updatedFields.getLocation())) {
            department.setLocation(updatedFields.getLocation());
        }

        if (updatedFields.getDescription() != null &&
                !updatedFields.getDescription().isEmpty() &&
                !Objects.equals(department.getDescription(), updatedFields.getDescription())) {
            department.setDescription(updatedFields.getDescription());
        }

        return department;
    }
}
