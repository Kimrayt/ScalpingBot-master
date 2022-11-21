package com.whiskels.telegram.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "scalping_quiz")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Question extends AbstractBaseEntity {

    @Column(name = "id", nullable = false)
    @NotBlank
    private long id;
    @Column(name = "question", nullable = false)
    @NotBlank
    private String question;

    @Column(name = "answer_correct", nullable = false)
    @NotBlank
    private String correctAnswer;

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    @Column(name = "option2")
    @NotBlank
    private String optionOne;

    @Column(name = "option1")
    @NotBlank
    private String optionTwo;

    @Column(name = "option3")
    @NotBlank
    private String optionThree;

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
//                ", optionOne='" + optionOne + '\'' +
//                ", optionTwo='" + optionTwo + '\'' +
//                ", optionThree='" + optionThree + '\'' +
                '}';
    }
}
