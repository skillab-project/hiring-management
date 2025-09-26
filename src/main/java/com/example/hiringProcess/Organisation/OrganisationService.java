package com.example.hiringProcess.Organisation;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganisationService {

    private final OrganisationRepository organisationRepository;

    @Autowired
    public OrganisationService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    public List<Organisation> getAll() {
        return organisationRepository.findAll();
    }

    public Optional<Organisation> getById(Integer id) {
        return organisationRepository.findById(id);
    }

    public Organisation create(Organisation organisation) {
        organisation.setId(0); // force insert
        return organisationRepository.save(organisation);
    }

    @Transactional
    public Organisation update(Integer id, Organisation updatedFields) {
        Organisation existing = organisationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Organisation with id " + id + " does not exist"));

        if (updatedFields.getName() != null && !updatedFields.getName().isEmpty()) {
            existing.setName(updatedFields.getName());
        }

        if (updatedFields.getDescription() != null && !updatedFields.getDescription().isEmpty()) {
            existing.setDescription(updatedFields.getDescription());
        }

        return existing;
    }

    @Transactional
    public void delete(Integer id) {
        Organisation existing = organisationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Organisation with id " + id + " does not exist"));

        organisationRepository.delete(existing);
    }

}
