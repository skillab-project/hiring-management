// src/main/java/com/example/hiringProcess/Candidate/CandidateMapper.java
package com.example.hiringProcess.Candidate;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR // αν θες αυστηρότητα
)
public interface CandidateMapper {

    CandidateMapper INSTANCE = Mappers.getMapper(CandidateMapper.class);

    // ========= Entity -> DTOs =========

    // Entity -> List DTO (ρητά mappings όπως ζήτησες)
    @Mapping(source = "id",        target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName",  target = "lastName")
    @Mapping(source = "email",     target = "email")
    @Mapping(source = "status",    target = "status")
    @Mapping(source = "cvPath",    target = "cvPath")
    CandidateDTO toListDto(Candidate candidate);

    // Entity -> Comment DTO (διαφορετικό όνομα πεδίου)
    @Mapping(source = "id",       target = "candidateId")
    @Mapping(source = "comments", target = "comments")
    CandidateCommentDTO toCommentDto(Candidate candidate);

    // ========= DTO -> Entity (update μόνο comments) =========
    // Δεν δημιουργούμε νέο Candidate, απλώς ενημερώνουμε το υπάρχον.
    // Αγνοούμε ΟΛΑ τα άλλα πεδία (άρα και το id), για να μην γίνει write στο id.
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "comments", target = "comments")
    void updateCommentsFromDto(CandidateCommentDTO dto, @MappingTarget Candidate candidate);
}
