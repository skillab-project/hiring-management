package com.example.hiringProcess.Department;

import com.example.hiringProcess.Organisation.Organisation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentService departmentService;

    // ===== CREATE =====
    @Test
    void addNewDepartment_shouldSetIdZeroAndCallSave() {
        Department dept = new Department();
        dept.setId(5); // id αρχικά

        Department saved = new Department();
        when(departmentRepository.save(any())).thenReturn(saved);

        Department result = departmentService.addNewDepartment(dept);

        assertSame(saved, result);
        assertEquals(0, dept.getId()); // id έγινε 0 πριν το save
        verify(departmentRepository).save(dept);
    }

    // ===== READ =====
    @Test
    void getDepartments_shouldReturnAll() {
        Department dept = new Department();
        when(departmentRepository.findAll()).thenReturn(List.of(dept));

        List<Department> result = departmentService.getDepartments();
        assertEquals(1, result.size());
        assertSame(dept, result.get(0));
    }

    @Test
    void getDepartment_shouldReturnOptional() {
        Department dept = new Department();
        when(departmentRepository.findById(1)).thenReturn(Optional.of(dept));

        Optional<Department> result = departmentService.getDepartment(1);
        assertTrue(result.isPresent());
        assertSame(dept, result.get());
    }

    @Test
    void getDepartmentNames_shouldMapToDTO() {
        Department dept = new Department();
        DepartmentNameDTO dto = new DepartmentNameDTO();
        when(departmentRepository.findAll()).thenReturn(List.of(dept));
        when(departmentMapper.toNameDTO(dept)).thenReturn(dto);

        List<DepartmentNameDTO> result = departmentService.getDepartmentNames();
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
    }

    // ===== UPDATE =====
    @Test
    void updateDepartment_shouldUpdateFields() {
        Department existing = new Department();
        existing.setName("Old");
        existing.setLocation("OldLoc");
        existing.setDescription("OldDesc");

        Department updated = new Department();
        updated.setName("New");
        updated.setLocation("NewLoc");
        updated.setDescription("NewDesc");

        when(departmentRepository.findById(1)).thenReturn(Optional.of(existing));

        Department result = departmentService.updateDepartment(1, updated);

        assertEquals("New", result.getName());
        assertEquals("NewLoc", result.getLocation());
        assertEquals("NewDesc", result.getDescription());
    }

    @Test
    void updateDepartment_shouldThrowIfNotFound() {
        when(departmentRepository.findById(99)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalStateException.class, () ->
                departmentService.updateDepartment(99, new Department())
        );

        assertTrue(ex.getMessage().contains("does not exist"));
    }

    // ===== DELETE =====
    @Test
    void deleteDepartment_shouldCallDeleteAndDisconnectRelations() {
        Department dept = new Department();
        dept.setJobAds(new java.util.HashSet<>()); // ✅ mutable Set
        dept.setOrganisation(mock(Organisation.class));

        when(departmentRepository.findById(1)).thenReturn(Optional.of(dept));

        departmentService.deleteDepartment(1);

        verify(departmentRepository).delete(dept);
        assertNull(dept.getOrganisation());
        assertTrue(dept.getJobAds().isEmpty()); // προαιρετικό check
    }


    @Test
    void deleteDepartment_shouldThrowIfNotFound() {
        when(departmentRepository.findById(99)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalStateException.class, () ->
                departmentService.deleteDepartment(99)
        );

        assertTrue(ex.getMessage().contains("does not exist"));
    }
}
