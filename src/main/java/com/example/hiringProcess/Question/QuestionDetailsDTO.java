package com.example.hiringProcess.Question;

import com.example.hiringProcess.Skill.SkillDTO;
import java.util.List;

public record QuestionDetailsDTO(
        Integer id,
        String name,
        String description,
        List<SkillDTO> skills
) { }

