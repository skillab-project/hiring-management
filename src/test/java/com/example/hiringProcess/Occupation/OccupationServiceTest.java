package com.example.hiringProcess.Occupation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OccupationServiceTest {

    @Mock OccupationRepository occupationRepository;
    @Mock OccupationMapper occupationMapper;

    @InjectMocks OccupationService occupationService;

    @Test
    void getOccupations_returnsAllFromRepository() {
        List<Occupation> list = List.of(new Occupation(), new Occupation());
        when(occupationRepository.findAll()).thenReturn(list);

        List<Occupation> result = occupationService.getOccupations();

        assertSame(list, result);
        verify(occupationRepository).findAll();
        verifyNoInteractions(occupationMapper);
    }

    @Test
    void getOccupation_returnsOptionalFromRepository() {
        Occupation occ = new Occupation();
        when(occupationRepository.findById(7)).thenReturn(Optional.of(occ));

        Optional<Occupation> result = occupationService.getOccupation(7);

        assertTrue(result.isPresent());
        assertSame(occ, result.get());
        verify(occupationRepository).findById(7);
        verifyNoInteractions(occupationMapper);
    }

    @Test
    void addNewOccupation_savesToRepository() {
        Occupation occ = new Occupation();

        occupationService.addNewOccupation(occ);

        verify(occupationRepository).save(occ);
        verifyNoInteractions(occupationMapper);
    }

    @Test
    void deleteOccupation_deletesById() {
        occupationService.deleteOccupation(10);

        verify(occupationRepository).deleteById(10);
        verifyNoInteractions(occupationMapper);
    }

    @Test
    void updateOccupation_setsIdAndSaves() {
        Occupation updated = new Occupation();

        occupationService.updateOccupation(55, updated);

        assertEquals(55, updated.getId());
        verify(occupationRepository).save(updated);
        verifyNoInteractions(occupationMapper);
    }

    @Test
    void getOccupationNamesByDepartment_mapsUsingMapper() {
        Integer deptId = 3;

        Occupation o1 = new Occupation();
        Occupation o2 = new Occupation();
        when(occupationRepository.findAllByDepartmentIdViaJobAds(deptId)).thenReturn(List.of(o1, o2));

        OccupationNameDTO dto1 = mock(OccupationNameDTO.class);
        OccupationNameDTO dto2 = mock(OccupationNameDTO.class);
        when(occupationMapper.toNameDTO(o1)).thenReturn(dto1);
        when(occupationMapper.toNameDTO(o2)).thenReturn(dto2);

        List<OccupationNameDTO> result = occupationService.getOccupationNamesByDepartment(deptId);

        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));

        verify(occupationRepository).findAllByDepartmentIdViaJobAds(deptId);
        verify(occupationMapper).toNameDTO(o1);
        verify(occupationMapper).toNameDTO(o2);
    }

    @Test
    void getOccupationNamesByDepartment_whenNoOccupations_returnsEmptyList() {
        Integer deptId = 999;
        when(occupationRepository.findAllByDepartmentIdViaJobAds(deptId)).thenReturn(List.of());

        List<OccupationNameDTO> result = occupationService.getOccupationNamesByDepartment(deptId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(occupationRepository).findAllByDepartmentIdViaJobAds(deptId);
        verifyNoInteractions(occupationMapper);
    }
}
