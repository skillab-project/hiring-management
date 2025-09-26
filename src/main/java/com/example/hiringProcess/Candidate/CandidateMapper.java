package com.example.hiringProcess.Candidate;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CandidateMapper {

    CandidateMapper INSTANCE = Mappers.getMapper(CandidateMapper.class);

    // Entity -> List DTO
    @Mapping(source = "id",        target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName",  target = "lastName")
    @Mapping(source = "email",     target = "email")
    @Mapping(source = "status",    target = "status")
    @Mapping(source = "cvPath",    target = "cvPath")
    @Mapping(source = "cvOriginalName",  target = "cvOriginalName")
    @Mapping(source = "interviewReport.id", target = "interviewReportId")
    CandidateDTO toListDto(Candidate candidate);

    // Entity -> Comment DTO
    @Mapping(source = "id",       target = "candidateId")
    @Mapping(source = "comments", target = "comments")
    CandidateCommentDTO toCommentDto(Candidate candidate);

    // DTO -> Entity (update μόνο comments)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "comments", target = "comments")
    void updateCommentsFromDto(CandidateCommentDTO dto, @MappingTarget Candidate candidate);

    // DTO -> Entity (update μόνο status)
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "status", target = "status")
    void updateStatusFromDto(CandidateStatusDTO dto, @MappingTarget Candidate candidate);
}
