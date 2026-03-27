package com.example.hiringProcess.Department;

import com.example.hiringProcess.Occupation.OccupationNameDTO;
import com.example.hiringProcess.Occupation.OccupationService;
import com.example.hiringProcess.Organisation.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final OccupationService occupationService;
    private final OrganisationService organizationService;

    @Autowired
    public DepartmentController(DepartmentService departmentService,
                                OccupationService occupationService, OrganisationService organizationService) {
        this.organizationService = organizationService;
        this.departmentService = departmentService;
        this.occupationService = occupationService;
    }

    @GetMapping("/names") //OK
    public List<DepartmentNameDTO> getDepartmentNames(@RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return departmentService.getDepartmentNamesByOrg(orgId);
    }

    @GetMapping("/{id}") //OK
    public ResponseEntity<Department> getDepartment( @PathVariable("id") Integer id, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return departmentService.getDepartmentByOrg(orgId, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/occupations") //OK
    public ResponseEntity<List<OccupationNameDTO>> getOccupationNamesByDepartment( @PathVariable("id") Integer id, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        // 1. Security Check: Does this department belong to this organization?
        if (!departmentService.existsByOrg(id, orgId)) {
            return ResponseEntity.notFound().build();
        }

        List<OccupationNameDTO> occupations = occupationService.getOccupationNamesByDepartment(id);
        return ResponseEntity.ok(occupations);
    }
}
