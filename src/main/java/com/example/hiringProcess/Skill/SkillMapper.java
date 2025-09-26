package com.example.hiringProcess.Skill;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillDTO toDto(Skill s);
    List<SkillDTO> toDto(List<Skill> list);
}
