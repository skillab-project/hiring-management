package com.example.hiringProcess.Organisation;

import com.example.hiringProcess.Department.Department;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Organisation {
    @Id
    @SequenceGenerator(
            name = "department_sequence",
            sequenceName = "department_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "department_sequence"
    )
    private int id;

    private String name;
    private String description;

    // Σχέση Organization με Department
    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Department> departments = new ArrayList<>();

    public Organisation(){}

    public Organisation(String name, String description) {
        this.name = name;
        this.description=description;
    }

    public void addDepartment(Department department) {
        departments.add(department);
        department.setOrganisation(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
