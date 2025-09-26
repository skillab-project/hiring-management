package com.example.hiringProcess.Question;

import com.example.hiringProcess.Skill.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    // Skills ενός step (όπως έχεις)
    @Query("""
           select distinct s
           from Question q
           join q.skills s
           where q.step.id = :stepId
           """)
    List<Skill> findDistinctSkillsByStepId(@Param("stepId") Integer stepId);

    // Όλες οι ερωτήσεις ενός step (χωρίς σειρά)
    List<Question> findByStep_Id(Integer stepId);

    // Όλες οι ερωτήσεις ενός step με σειρά
    List<Question> findByStep_IdOrderByPositionAsc(Integer stepId);

    // Μαζική φόρτωση για reorder
    List<Question> findByIdIn(List<Integer> ids);

    // Πλήθος ερωτήσεων ενός step
    long countByStep_Id(Integer stepId);

    /* ---------- ΝΕΑ: Aggregations για τα assessments ---------- */

    /** Πλήθος ερωτήσεων ανά step για όλο το interview */
    @Query("""
        select q.step.id as stepId, count(q) as cnt
        from Question q
        where q.step.interview.id = :interviewId
        group by q.step.id
    """)
    List<Object[]> countQuestionsPerStep(@Param("interviewId") int interviewId);

    /** Πλήθος skills ανά question (για όλο το interview) */
    @Query("""
        select q.id as questionId, count(distinct s.id) as cnt
        from Question q
        left join q.skills s
        where q.step.interview.id = :interviewId
        group by q.id
    """)
    List<Object[]> countSkillsPerQuestion(@Param("interviewId") int interviewId);

    @Query("""
       select q.id as questionId, count(distinct s.id) as cnt
       from Question q
       left join q.skills s
       where q.id in :qids
       group by q.id
     """)
    List<Object[]> countSkillsForQuestions(@Param("qids") Collection<Integer> qids);

    @Query("""
       select q.step.id as stepId, count(q) as cnt
       from Question q
       where q.step.id in :stepIds
       group by q.step.id
    """)
    List<Object[]> countQuestionsByStepIds(@Param("stepIds") Collection<Integer> stepIds);

    @Query("""
       select q.step.id as stepId, q.id as questionId
       from Question q
       where q.step.id in :stepIds
    """)
    List<Object[]> listQuestionIdsByStepIds(@Param("stepIds") Collection<Integer> stepIds);


    List<Question> findByStepIdOrderByPositionAsc(Integer stepId);

    List<Question> findByStep_Interview_Id(Integer interviewId);


}
