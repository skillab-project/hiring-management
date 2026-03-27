package com.example.hiringProcess.Organisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/organisations")
public class OrganisationController {

    private final OrganisationService organisationService;

    @Autowired
    public OrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    @GetMapping
    public List<OrganisationResponseDTO> getAll() {
        return organisationService.getAll();
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Organisation> getById(@PathVariable("id") Integer id) {
//        return organisationService.getById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
}