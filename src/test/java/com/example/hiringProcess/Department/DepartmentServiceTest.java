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


//    // ===== READ =====
//    @Test
//    void getDepartments_shouldReturnAll() {
//        Department dept = new Department();
//        when(departmentRepository.findAll()).thenReturn(List.of(dept));
//
//        List<DepartmentNameDTO> result = departmentService.getDepartmentNamesByOrg(1);
//        assertEquals(1, result.size());
//        assertSame(dept, result.get(0));
//    }
//
//    @Test
//    void getDepartment_shouldReturnOptional() {
//        Department dept = new Department();
//        when(departmentRepository.findById(1)).thenReturn(Optional.of(dept));
//
//        Optional<Department> result = departmentService.getDepartmentByOrg(1,1);
//        assertTrue(result.isPresent());
//        assertSame(dept, result.get());
//    }
//
//    @Test
//    void getDepartmentNames_shouldMapToDTO() {
//        Department dept = new Department();
//        DepartmentNameDTO dto = new DepartmentNameDTO();
//        when(departmentRepository.findAll()).thenReturn(List.of(dept));
//        when(departmentMapper.toNameDTO(dept)).thenReturn(dto);
//
//        List<DepartmentNameDTO> result = departmentService.getDepartmentNamesByOrg(1);
//        assertEquals(1, result.size());
//        assertSame(dto, result.get(0));
//    }
//


}
