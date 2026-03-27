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

    @Test
    void getAll_returnsAllFromRepository() {
        List<Organisation> list = List.of(new Organisation(), new Organisation());
        when(organisationRepository.findAll()).thenReturn(list);

        List<Organisation> result = organisationService.getAll();

        assertSame(list, result);
        verify(organisationRepository).findAll();
    }

    @Test
    void getById_returnsOptionalFromRepository() {
        Organisation org = new Organisation();
        when(organisationRepository.findById(5)).thenReturn(Optional.of(org));

        Optional<Organisation> result = organisationService.getById(5);

        assertTrue(result.isPresent());
        assertSame(org, result.get());
        verify(organisationRepository).findById(5);
    }

    // -------------------------
    // create (σημαντικό: setId(0) + save)
    // -------------------------

    @Test
    void create_setsIdToZeroAndSaves() {
        Organisation org = new Organisation();
        org.setId(999); // simulate "existing id"

        when(organisationRepository.save(any(Organisation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Organisation saved = organisationService.create(org);

        assertEquals(0, org.getId(), "create() should force insert by setting id=0");
        verify(organisationRepository).save(org);
        assertSame(org, saved);
    }

    // -------------------------
    // update (partial update rules + exceptions)
    // -------------------------

    @Test
    void update_whenOrganisationNotFound_throwsIllegalStateException() {
        when(organisationRepository.findById(10)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> organisationService.update(10, new Organisation()));

        assertTrue(ex.getMessage().contains("10"));
        verify(organisationRepository).findById(10);
        verifyNoMoreInteractions(organisationRepository);
    }

    @Test
    void update_updatesNameAndDescription_whenProvidedAndNotEmpty() {
        Organisation existing = new Organisation();
        existing.setId(1);
        existing.setName("Old Name");
        existing.setDescription("Old Desc");

        Organisation patch = new Organisation();
        patch.setName("New Name");
        patch.setDescription("New Desc");

        when(organisationRepository.findById(1)).thenReturn(Optional.of(existing));

        Organisation result = organisationService.update(1, patch);

        assertSame(existing, result, "update() returns the managed existing entity");
        assertEquals("New Name", existing.getName());
        assertEquals("New Desc", existing.getDescription());

        verify(organisationRepository).findById(1);
        verify(organisationRepository, never()).save(any()); // update δεν καλεί save (transactional dirty checking)
    }

    @Test
    void update_doesNotOverwriteFields_whenNullOrEmpty() {
        Organisation existing = new Organisation();
        existing.setId(1);
        existing.setName("Keep Name");
        existing.setDescription("Keep Desc");

        Organisation patch = new Organisation();
        patch.setName("");          // empty -> ignore
        patch.setDescription(null); // null -> ignore

        when(organisationRepository.findById(1)).thenReturn(Optional.of(existing));

        Organisation result = organisationService.update(1, patch);

        assertSame(existing, result);
        assertEquals("Keep Name", existing.getName());
        assertEquals("Keep Desc", existing.getDescription());

        verify(organisationRepository).findById(1);
        verify(organisationRepository, never()).save(any());
    }

    // -------------------------
    // delete (find or throw + delete(existing))
    // -------------------------

    @Test
    void delete_whenOrganisationNotFound_throwsIllegalStateException() {
        when(organisationRepository.findById(99)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> organisationService.delete(99));

        assertTrue(ex.getMessage().contains("99"));
        verify(organisationRepository).findById(99);
        verify(organisationRepository, never()).delete(any());
    }

    @Test
    void delete_whenFound_deletesExisting() {
        Organisation existing = new Organisation();
        existing.setId(7);

        when(organisationRepository.findById(7)).thenReturn(Optional.of(existing));

        organisationService.delete(7);

        verify(organisationRepository).findById(7);
        verify(organisationRepository).delete(existing);
    }
}
