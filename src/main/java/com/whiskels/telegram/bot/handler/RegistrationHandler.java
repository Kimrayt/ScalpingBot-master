package com.whiskels.telegram.bot.handler;

import com.whiskels.telegram.bot.State;
import com.whiskels.telegram.model.User;
import com.whiskels.telegram.repository.JpaUserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.List;

import static com.whiskels.telegram.bot.handler.QuizHandler.*;
import static com.whiskels.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static com.whiskels.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class RegistrationHandler implements Handler {
    // Supported CallBackQueries are stored as constants
    public static final String NAME_ACCEPT = "/enter_name_accept";
    public static final String NAME_CHANGE = "/enter_name";
    public static final String NAME_CHANGE_CANCEL = "/enter_name_cancel";

    private final JpaUserRepository userRepository;

    public RegistrationHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        // Checking type of input message
        if (message.equalsIgnoreCase(NAME_ACCEPT) || message.equalsIgnoreCase(NAME_CHANGE_CANCEL)) {
            return accept(user);
        } else if (message.equalsIgnoreCase(NAME_CHANGE)) {
            return changeName(user);
        }
        return checkName(user, message);

    }

    private List<PartialBotApiMethod<? extends Serializable>> accept(User user) {
        // If user accepted the change - update bot state and save user
        user.setBotState(State.NONE);
        userRepository.save(user);

        // Creating button to start new game
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("?????????????? ??????????????????", LEARNING_TRIVIA));
        List<InlineKeyboardButton> inlineKeyboardButtonsRowTwo = List.of(createInlineKeyboardButton("???????????? ??????????????????", CONTACT_MANAGER));
        List<InlineKeyboardButton> inlineKeyboardButtonsRowThree = List.of(createInlineKeyboardButton("???????????? ????????", QUIZ_START));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne, inlineKeyboardButtonsRowTwo, inlineKeyboardButtonsRowThree));

        SendMessage sm = createMessageTemplate(user);
        sm.setText(String.format(
                "???????? ?????? ?????????????????? ??????: %s", user.getName()));
        sm.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sm);
    }

    private List<PartialBotApiMethod<? extends Serializable>> checkName(User user, String message) {
        // When we check user name we store it in database immediately
        // refactoring idea: temporal storage
        user.setName(message);
        userRepository.save(user);

        // Creating button to accept changes
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("??????????????", NAME_ACCEPT));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sm = createMessageTemplate(user);
        sm.setText(String.format("???? ??????????: %s%n ???????? ?????? ?????????????????? \\- ?????????????? ????????????", user.getName()));
        sm.setReplyMarkup(inlineKeyboardMarkup);
        return List.of(sm);
    }

    private List<PartialBotApiMethod<? extends Serializable>> changeName(User user) {
        // When name change request is received - bot state changes
        user.setBotState(State.ENTER_NAME);
        userRepository.save(user);

        // Cancel button creation
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("????????????", NAME_CHANGE_CANCEL));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sm = createMessageTemplate(user);
        sm.setText(String.format("???????? ?????????????? ??????: %s%n ?????????????? ?????????? ?????? ?????? ?????????????? ????????????, ?????????? ????????????????????", user.getName()));
        sm.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sm);
    }

    @Override
    public State operatedBotState() {
        return State.ENTER_NAME;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(NAME_ACCEPT, NAME_CHANGE, NAME_CHANGE_CANCEL);
    }
}
