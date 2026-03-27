package com.example.hiringProcess.Organisation;

import com.example.hiringProcess.Department.Department;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizations")
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizations_seq")
    @SequenceGenerator(name = "organizations_seq", sequenceName = "organizations_seq", allocationSize = 1)
    private Integer id;

    private String name;

    private String description; // Specific to Hiring

    private String location;    // Added to match Employee/DB structure

    // Note: mappedBy must match the variable name in your Department entity
    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Department> departments = new ArrayList<>();

    public Organisation() {}

    public Organisation(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Standard Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<Department> getDepartments() { return departments; }
    public void setDepartments(List<Department> departments) { this.departments = departments; }

    public void addDepartment(Department department) {
        departments.add(department);
        department.setOrganisation(this);
    }
}