package com.example.hiringProcess.Occupation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/occupations")
public class OccupationController {

    private final OccupationService occupationService;
    private final OccupationMapper occupationMapper;

    @Autowired
    public OccupationController(OccupationService occupationService,
                                OccupationMapper occupationMapper) {
        this.occupationService = occupationService;
        this.occupationMapper = occupationMapper;
    }

    @GetMapping
    public List<Occupation> getOccupations() {
        return occupationService.getOccupations();
    }

    @GetMapping("/names")
    public List<OccupationNameDTO> getOccupationNames() {
        return occupationService.getOccupations()
                .stream()
                .map(occupationMapper::toNameDTO)   // ✅ mapper εδώ
                .toList();
    }

    @GetMapping("/{occupationId}")
    public ResponseEntity<Occupation> getOccupation(@PathVariable Integer occupationId) {
        return occupationService.getOccupation(occupationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
