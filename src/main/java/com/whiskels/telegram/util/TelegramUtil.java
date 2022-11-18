package com.whiskels.telegram.util;

import com.whiskels.telegram.model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class TelegramUtil {
    public static SendMessage createMessageTemplate(User user) {
        return createMessageTemplate(String.valueOf(user.getChatId()));
    }

    // Creating template of SendMessage with enabled Markdown
    public static SendMessage createMessageTemplate(String chatId) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.enableMarkdownV2(true);
        return sm;

    }

    // Creating button
    public static InlineKeyboardButton createInlineKeyboardButton(String text, String command) {
        InlineKeyboardButton ikb = new InlineKeyboardButton();
        ikb.setText(text);
        ikb.setCallbackData(command);
        return ikb;

    }
}
