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
@Table(name = "scalping_trivia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trivia extends AbstractBaseEntity {
    @Column(name = "question", nullable = false)
    @NotBlank
    private String question;

    @Column(name = "answer_correct", nullable = false)
    @NotBlank
    private String correctAnswer;

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                '}';
    }
}
