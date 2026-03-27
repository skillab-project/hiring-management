package com.example.hiringProcess.Department;

import com.example.hiringProcess.JobAd.JobAd;
import com.example.hiringProcess.Organisation.Organisation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "departments") // Matches Employee plural naming
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "departments_seq")
    @SequenceGenerator(name = "departments_seq", sequenceName = "departments_seq", allocationSize = 1)
    private Integer id;

    private String name;
    private String location;
    private String description;

    // Relationship with JobAd (Hiring specific)
    @ManyToMany(mappedBy = "departments")
    @JsonIgnore
    private Set<JobAd> jobAds;

    // Relationship with Organisation
    @ManyToOne
    @JsonIgnore
    // Changed "organisation_id" to "organization_id" to match Employee truth
    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    private Organisation organisation;

    public Department(){}

    public Department(String name, String location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }

    // Getters and Setters ...
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Organisation getOrganisation() { return organisation; }
    public void setOrganisation(Organisation organisation) { this.organisation = organisation; }

    public Set<JobAd> getJobAds() { return jobAds; }
    public void setJobAds(Set<JobAd> jobAds) { this.jobAds = jobAds; }
}