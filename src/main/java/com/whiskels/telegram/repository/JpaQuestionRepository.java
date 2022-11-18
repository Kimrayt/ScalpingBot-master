package com.whiskels.telegram.repository;

import com.whiskels.telegram.model.Question;
import com.whiskels.telegram.model.Trivia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import static com.whiskels.telegram.bot.handler.QuizHandler.currentQuestion;

public interface JpaQuestionRepository extends JpaRepository<Question, Integer> {

}
