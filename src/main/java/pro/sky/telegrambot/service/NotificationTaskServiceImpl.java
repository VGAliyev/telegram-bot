package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.SendResponseException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {
    private final Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskServiceImpl(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Override
    public void sendMessage(Update update, TelegramBot telegramBot, Logger logger) {
        if (update.message() != null) {
            String messageText = update.message().text();
            Long chatId = update.message().chat().id();
            switch (messageText) {
                case "/start" ->
                        send(update, telegramBot, logger, "Привет. Для сохранения задачи, наберите её в формате 'дд.мм.гггг чч:мм текст_задачи'. Для вывода информации введите '/info'");
                case "/info" ->
                        send(update, telegramBot, logger, "Telegram bot - напоминание о делах. Автор: Владислав Алиев, 2024г.");
                default -> {
                    Matcher matcher = pattern.matcher(messageText);
                    if (matcher.matches()) {
                        String date = matcher.group(1);
                        String message = matcher.group(3);
                        LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                        if (LocalDateTime.now().isBefore(localDateTime)) {
                            notificationTaskRepository.save(
                                    new NotificationTask(
                                            localDateTime,
                                            chatId,
                                            message));
                            send(update, telegramBot, logger, "Задача сохранена!");
                            logger.info("Task saved!");
                        } else {
                            send(update, telegramBot, logger, "Заданная дата выполнения задачи должна быть позже текущей! Попробуйте снова!");
                        }
                    } else {
                        send(update, telegramBot, logger, "Неверная команда. Для сохранения задачи, наберите её в формате 'дд.мм.гггг чч:мм текст_задачи'. Для вывода информации введите '/info'");
                    }
                }
            }
        } else {
            logger.warn("Message is null!");
        }
    }

    private void send(Update update, TelegramBot telegramBot, Logger logger, String s) {
            SendMessage sendMessage = new SendMessage(update.message().chat().id(), s);
            SendResponse response = telegramBot.execute(sendMessage);
            if (!response.isOk()) {
                logger.error("Telegram bot response error: {}", response.errorCode());
                throw new SendResponseException("Error: " + response.errorCode());
            } else {
                logger.info("Response OK!");
            }
    }
}
