package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;

public interface NotificationTaskService {
    public void sendMessage(Update update, TelegramBot telegramBot, Logger logger);
    public void scheduling();
}
