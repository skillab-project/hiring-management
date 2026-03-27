package com.example.hiringProcess.Organisation;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class OrganisationService {

    private final OrganisationRepository organisationRepository;

    @Autowired
    public OrganisationService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    /**
     * Translates an Organization Name (from Header) into an ID.
     */
    public Integer getIdByName(String orgName) {
        return organisationRepository.findByName(orgName)
                .map(Organisation::getId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Organization '" + orgName + "' not found"
                ));
    }

    /**
     * Returns the name of an organization given its ID.
     */
    public String getOrganizationNameById(Integer id) {
        return organisationRepository.findNameById(id)
                .orElseThrow(() -> new RuntimeException("Organization with ID " + id + " not found"));
    }


    public List<OrganisationResponseDTO> getAll() {
//        return organisationRepository.findAll();
        return organisationRepository.findAll()
                .stream()
                .map(org -> new OrganisationResponseDTO(org.getName(), org.getDescription()))
                .toList();
    }


    public Optional<Organisation> getById(Integer id) {
        return organisationRepository.findById(id);
    }

}