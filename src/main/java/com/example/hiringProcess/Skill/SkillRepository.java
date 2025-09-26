package com.example.hiringProcess.Skill;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
    Optional<Skill> findByTitle(String title);
    List<Skill> findByTitleIn(List<String> titles);
}

