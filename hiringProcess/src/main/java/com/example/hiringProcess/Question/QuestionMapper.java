package com.example.hiringProcess.Question;

import com.example.hiringProcess.Skill.Skill;
import com.example.hiringProcess.Skill.SkillDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionMapper {

    // single item
    public QuestionLiteDTO toLite(Question q) {
        if (q == null) return null;
        // entity.title -> DTO.name
        return new QuestionLiteDTO(q.getId(), q.getTitle());
    }

    // list overload (το θέλει το service)
    public List<QuestionLiteDTO> toLite(List<Question> list) {
        List<QuestionLiteDTO> out = new ArrayList<>();
        if (list == null) return out;
        for (Question q : list) out.add(toLite(q));
        return out;
    }

    // details (record με List<SkillDTO>)
    public QuestionDetailsDTO toDetails(Question q) {
        if (q == null) return null;
        List<SkillDTO> skills = new ArrayList<>();
        if (q.getSkills() != null) {
            for (Skill s : q.getSkills()) {
                if (s != null) skills.add(new SkillDTO(s.getId(), s.getTitle()));
            }
        }
        return new QuestionDetailsDTO(
                q.getId(),
                q.getTitle(),          // -> name
                q.getDescription(),
                skills
        );
    }
}
