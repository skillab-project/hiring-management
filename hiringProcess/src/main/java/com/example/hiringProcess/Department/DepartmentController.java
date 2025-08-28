package com.example.hiringProcess.Department;

import com.example.hiringProcess.Occupation.OccupationNameDTO;
import com.example.hiringProcess.Occupation.OccupationService;
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

    @Autowired
    public DepartmentController(DepartmentService departmentService,
                                OccupationService occupationService) {
        this.departmentService = departmentService;
        this.occupationService = occupationService;
    }

    @GetMapping("/names")
    public List<DepartmentNameDTO> getDepartmentNames() {
        return departmentService.getDepartmentNames();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartment(@PathVariable("id") Integer id) {
        return departmentService.getDepartment(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Department> addNewDepartment(@RequestBody Department department) {
        Department saved = departmentService.addNewDepartment(department);
        URI location = URI.create("/api/v1/departments/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable("id") Integer id,
                                                       @RequestBody Department updatedFields) {
        Department updated = departmentService.updateDepartment(id, updatedFields);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable("id") Integer id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/occupations")
    public List<OccupationNameDTO> getOccupationNamesByDepartment(@PathVariable("id") Integer id) {
        return occupationService.getOccupationNamesByDepartment(id);
    }
}
