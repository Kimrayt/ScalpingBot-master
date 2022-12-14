package com.whiskels.telegram.bot.handler;

import com.vdurmont.emoji.EmojiParser;
import com.whiskels.telegram.bot.State;
import com.whiskels.telegram.model.Question;
import com.whiskels.telegram.model.Trivia;
import com.whiskels.telegram.model.User;
import com.whiskels.telegram.repository.JpaQuestionRepository;
import com.whiskels.telegram.repository.JpaUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static com.whiskels.telegram.util.TelegramUtil.createMessageTemplate;

@Slf4j
@Component
public class QuizHandler implements Handler {
    // Supported CallBackQueries are stored as constants
    public static final String QUIZ_CORRECT = "/quiz_correct";
    public static final String QUIZ_INCORRECT = "/quiz_incorrect";
    public static final String QUIZ_START = "/quiz_start";

    public static final String LEARNING_TRIVIA = "/learning_trivia";

    public static final String CONTACT_MANAGER = "/contact_manager";
    public  int currentQuestion = 0;
    boolean changeStates = true;
    // Answer options
    private ArrayList<String> OPTIONS = new ArrayList<>(Arrays.asList("Далее", "Далее", "Далее", "Далее"));

    private final JpaUserRepository userRepository;

    public QuizHandler(JpaUserRepository userRepository, JpaQuestionRepository questionRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) throws SQLException {
        if(user.getBotState() == State.PLAYING_QUIZ){
        if (message.startsWith(QUIZ_CORRECT)) {
            // action performed on callback with correct answer
            return correctAnswer(user, message);
        } else if (message.startsWith(QUIZ_INCORRECT)) {
            // action performed on callback with incorrect answer
            return incorrectAnswer(user);
        } else {
            return startNewQuiz(user);
        }
        }
        else if (user.getBotState() == State.LEARNING_TRIVIA){
            return startNewTrivia(user);
        }
        else {
            return managerContact(user);
        }
        }

    private List<PartialBotApiMethod<? extends Serializable>> correctAnswer(User user, String message) throws SQLException {
        log.info("correct");

        // Incrementing user score
        final int currentScore = user.getScore() + 1;
        user.setScore(currentScore);
        userRepository.save(user);

        // Returning next question
        return nextQuestion(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> incorrectAnswer(User user) {
        final int currentScore = user.getScore();
        // Changing high score if needed
        if (user.getHighScore() < currentScore) {
            user.setHighScore(currentScore);
        }
        // Updating user status
        user.setScore(0);
        user.setBotState(State.NONE);
        userRepository.save(user);

        // Creating "Try again?" button
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Попробовать ещё раз?", QUIZ_START));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));
        SendMessage sm = createMessageTemplate(user);
        sm.setText(String.format("Неверно\\!%nВы заработали *%d* баллов\\!", currentScore));
        sm.setReplyMarkup(inlineKeyboardMarkup);
        return List.of(sm);
    }

    private List<PartialBotApiMethod<? extends Serializable>> startNewQuiz(User user) throws SQLException {
        user.setBotState(State.PLAYING_QUIZ);
        userRepository.save(user);

        return nextQuestion(user);
    }
    private List<PartialBotApiMethod<? extends Serializable>> startNewTrivia(User user) throws SQLException {
        user.setBotState(State.LEARNING_TRIVIA);
        userRepository.save(user);

        return nextQuestion(user);
    }
    private List<PartialBotApiMethod<? extends Serializable>> managerContact(User user) throws SQLException {
        user.setBotState(State.CONTACT_MANAGER);
        userRepository.save(user);

        return nextQuestion(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> nextQuestion(User user) throws SQLException {

        if (user.getBotState() == State.PLAYING_QUIZ & changeStates){
            currentQuestion = 1;
            changeStates = false;
        }
        else if (user.getBotState() == State.CONTACT_MANAGER){
            currentQuestion = 12;
        }
        else if (user.getBotState() == State.LEARNING_TRIVIA){
            currentQuestion = 1;
        }
        Connection con= null;
        try {
            con = DriverManager.getConnection("jdbc:postgresql://dpg-cecevi94reb9mgflf0h0-a.frankfurt-postgres.render.com:5432/scalpingdb","xcriticalmarketingtgadmin","4Lyc84CFBa1LxZANZ4z7JgfJu7LR68OV");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert con != null;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("SELECT * FROM scalping_quiz WHERE id>? LIMIT 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            assert pstmt != null;
            pstmt.setInt(1, currentQuestion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet rs = null;
        try {
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            assert rs != null;
            rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Question question = new Question();
        List<String> options;
        try {
            currentQuestion = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        if (currentQuestion <=6) {
            System.out.println(currentQuestion);
            try {
                question.setId(rs.getInt(1));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                question.setQuestion(rs.getString(2));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                question.setCorrectAnswer(rs.getString(3));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            options = new ArrayList<>(List.of(question.getCorrectAnswer()));

            sb.append(EmojiParser.parseToUnicode(question.getQuestion()));
        }
        else {
            System.out.println(currentQuestion);
            OPTIONS.set(0, "A");
            OPTIONS.set(1, "B");
            OPTIONS.set(2, "C");
            OPTIONS.set(3, "D");
            question.setId(rs.getInt(1));
            question.setQuestion(rs.getString(2));
            question.setCorrectAnswer(rs.getString(3));
            question.setOptionOne(rs.getString(4));
            question.setOptionTwo(rs.getString(5));
            question.setOptionThree(rs.getString(6));
            options = new ArrayList<>(List.of(question.getCorrectAnswer(), question.getOptionOne(), question.getOptionTwo(), question.getOptionThree()));
            sb.append(question.getQuestion());
            System.out.println(sb);
        }

        System.out.println(sb);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowTwo = new ArrayList<>();

        for (int i = 0; i < options.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();

            String callbackData = options.get(i).equalsIgnoreCase(question.getCorrectAnswer()) ? QUIZ_CORRECT : QUIZ_INCORRECT;

            button.setText(OPTIONS.get(i));
            button.setCallbackData(String.format("%s %d", callbackData, question.getId()));

            if (i < 2) {
                inlineKeyboardButtonsRowOne.add(button);
            } else {
                inlineKeyboardButtonsRowTwo.add(button);
            }
            if (currentQuestion >6) {
            sb.append(OPTIONS.get(i)).append("\\. ").append(options.get(i))
                    .append("\n");
            }
        }

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne, inlineKeyboardButtonsRowTwo));
        SendMessage sm = createMessageTemplate(user);
        sm.setText(sb.toString());
        sm.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sm);
    }

    @Override
    public State operatedBotState() {
        return null;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(LEARNING_TRIVIA,QUIZ_START ,CONTACT_MANAGER , QUIZ_CORRECT, QUIZ_INCORRECT);
    }
}
