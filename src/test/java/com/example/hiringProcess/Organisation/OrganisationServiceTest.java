package com.example.hiringProcess.Organisation;

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
class OrganisationServiceTest {

    @Mock OrganisationRepository organisationRepository;

    @InjectMocks OrganisationService organisationService;

    // -------------------------
    // getAll / getById (χαμηλής αξίας αλλά οκ)
    // -------------------------

//    @Test
//    void getAll_returnsAllFromRepository() {
//        List<Organisation> list = List.of(new Organisation(), new Organisation());
//        when(organisationRepository.findAll()).thenReturn(list);
//
//        List<OrganisationResponseDTO> result = organisationService.getAll();
//
//        assertSame(list, result);
//        verify(organisationRepository).findAll();
//    }

    @Test
    void getById_returnsOptionalFromRepository() {
        Organisation org = new Organisation();
        when(organisationRepository.findById(5)).thenReturn(Optional.of(org));

        Optional<Organisation> result = organisationService.getById(5);

        assertTrue(result.isPresent());
        assertSame(org, result.get());
        verify(organisationRepository).findById(5);
    }


}
