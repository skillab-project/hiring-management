package com.example.hiringProcess.Question;

public class QuestionListItemDTO {
    private Integer id;
    private String name;

    public QuestionListItemDTO() {}
    public QuestionListItemDTO(Integer id, String name) {
        this.id = id; this.name = name;
    }
    public Integer getId() { return id; }
    public String getName() { return name; }
}
