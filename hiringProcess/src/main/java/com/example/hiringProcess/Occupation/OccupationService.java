package com.example.hiringProcess.Occupation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OccupationService {

    private final OccupationRepository occupationRepository;
    private final OccupationMapper occupationMapper;

    @Autowired
    public OccupationService(OccupationRepository occupationRepository,
                             OccupationMapper occupationMapper) {
        this.occupationRepository = occupationRepository;
        this.occupationMapper = occupationMapper;
    }

    public List<Occupation> getOccupations() {
        return occupationRepository.findAll();
    }

    public Optional<Occupation> getOccupation(Integer occupationId) {
        return occupationRepository.findById(occupationId);
    }

    public void addNewOccupation(Occupation occupation) {
        occupationRepository.save(occupation);
    }

    public void deleteOccupation(Integer occupationId) {
        occupationRepository.deleteById(occupationId);
    }

    public void updateOccupation(Integer occupationId, Occupation updatedOccupation) {
        updatedOccupation.setId(occupationId);
        occupationRepository.save(updatedOccupation);
    }

    public List<OccupationNameDTO> getOccupationNamesByDepartment(Integer deptId) {
        return occupationRepository.findAllByDepartmentIdViaJobAds(deptId)
                .stream()
                .map(occupationMapper::toNameDTO)   // ✅ χρήση του mapper
                .collect(Collectors.toList());
    }
}
