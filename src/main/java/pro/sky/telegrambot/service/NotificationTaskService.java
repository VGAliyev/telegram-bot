package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;

public interface NotificationTaskService {
    // получаем сообщение от пользователя
    // матчим его, если в нем /start, то приветствуем и даем инструкцию о формате задания задач
    // если в нем задача, то матчим её и сохраняем в репозиторий
    // если текст неверного формата, то сообщаем, что неверно и снова даем инструкцию
    // если /info то выдаем информацию о телеграм боте и авторе
    public void sendMessage(Update update, TelegramBot telegramBot, Logger logger);
}
