package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

public interface SchedulingService {
    public void scheduling(Update update, TelegramBot telegramBot);
}
