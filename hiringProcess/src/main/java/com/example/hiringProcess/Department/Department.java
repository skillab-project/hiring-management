package com.example.hiringProcess.Department;

import com.example.hiringProcess.JobAd.JobAd;
import com.example.hiringProcess.Organisation.Organisation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Set;


@Entity
@Table(name = "department")
public class Department {
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
    private int id;  // ΤΟ ID ΠΡΕΠΕΙ ΝΑ ΔΗΛΩΘΕΙ ΠΡΩΤΟ!

    private String name;
    private String location;
    private String description;

    // Σχέση Department με JobAd
    @ManyToMany(mappedBy = "departments")
    @JsonIgnore
    private Set<JobAd> jobAds;

    // Σχέση Department με Organisation
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "organisation_id", referencedColumnName = "id")
    private Organisation organisation;

    public Department(){}

    public Department(String name, String location, String description) {
        this.name = name;
        this.location=location;
        this.description=description;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Set<JobAd> getJobAds() {
        return jobAds;
    }

    public void setJobAds(Set<JobAd> jobAds) {
        this.jobAds = jobAds;
    }
}
