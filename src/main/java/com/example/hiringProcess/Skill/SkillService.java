package com.example.hiringProcess.Skill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<Skill> getSkills() {
        return skillRepository.findAll();
    }

    public Optional<Skill> getSkill(Integer skillId) {
        return skillRepository.findById(skillId);
    }

    public void addNewSkill(Skill skill) {
        skillRepository.save(skill);
    }

    public void deleteSkill(Integer skillId) {
        boolean exists = skillRepository.existsById(skillId);
        if (!exists) {
            throw new IllegalStateException("Skill with id " + skillId + " does not exist");
        }
        skillRepository.deleteById(skillId);
    }

    @Transactional
    public void updateSkill(Integer skillId, Skill updatedSkill) {
        Skill existingSkill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalStateException(
                        "Skill with id " + skillId + " does not exist"));

        // Title
        if (updatedSkill.getTitle() != null) {
            existingSkill.setTitle(updatedSkill.getTitle());
        }

        // Score
        // Χρησιμοποίησε wrapper (Double) στο DTO/Request αν θες να ξεχωρίζεις "δεν εστάλη" vs "0.0"
        existingSkill.setScore(updatedSkill.getScore());

        // EscoId
        if (updatedSkill.getEscoId() != null) {
            existingSkill.setEscoId(updatedSkill.getEscoId());
        }

        // Σχέσεις (προσοχή: εδώ κάνεις πλήρη αντικατάσταση)
        if (updatedSkill.getQuestions() != null) {
            existingSkill.setQuestions(updatedSkill.getQuestions());
        }

    }

}
