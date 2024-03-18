package pro.sky.telegrambot.service.helper;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import pro.sky.telegrambot.exception.SendResponseException;

public class Sender {
    private final Update update;
    private final TelegramBot telegramBot;

    public Sender(Update update, TelegramBot telegramBot) {
        this.update = update;
        this.telegramBot = telegramBot;
    }

    /**
     * Send message to chat
     *
     * @param m Message
     */
    public void send(String m) {
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), m);
        SendResponse response = telegramBot.execute(sendMessage);
        if (!response.isOk()) {
            throw new SendResponseException("SendResponseError: " + response.errorCode());
        }
    }
}
