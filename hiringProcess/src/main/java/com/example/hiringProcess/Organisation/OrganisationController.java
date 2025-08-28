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
    public List<Organisation> getAll() {
        return organisationService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organisation> getById(@PathVariable("id") Integer id) {
        return organisationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Organisation> create(@RequestBody Organisation organisation) {
        Organisation saved = organisationService.create(organisation);
        URI location = URI.create("/api/v1/organisations/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Organisation> update(
            @PathVariable("id") Integer id,
            @RequestBody Organisation updatedFields) {

        Organisation updated = organisationService.update(id, updatedFields);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        organisationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
