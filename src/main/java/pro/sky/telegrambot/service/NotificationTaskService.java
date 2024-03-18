package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

public interface NotificationTaskService {
    public String[] sendMessage(Update update, TelegramBot telegramBot);
    public void scheduling();
}
