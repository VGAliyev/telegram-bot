package pro.sky.telegrambot.service.Impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.SendResponseException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationTaskService;
import pro.sky.telegrambot.service.helper.Sender;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {
    private final Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    private final NotificationTaskRepository notificationTaskRepository;

    private Sender sender;

    public NotificationTaskServiceImpl(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Override
    public String[] sendMessage(Update update, TelegramBot telegramBot) {
        sender = new Sender(update, telegramBot);
        String[] message = new String[2];
        if (update.message() != null) {
            String messageText = update.message().text();
            Long chatId = update.message().chat().id();
            switch (messageText) {
                case "/start" -> {
                    String hello = "Привет. Для сохранения задачи, наберите её в формате 'дд.мм.гггг чч:мм текст_задачи'. Для вывода информации введите '/info'";
                    sender.send(hello);
                    message = new String[]{"START", hello};
                }
                case "/info" -> {
                    String info = "Telegram bot - напоминание о делах. Автор: Владислав Алиев, 2024г.";
                    sender.send(info);
                    message = new String[]{"ABOUT_BOT", info};
                }
                default -> {
                    message = matchesMessage(messageText, chatId);
                }
            }
        } else {
            message = new String[]{"WARN", "Message is null!"};
        }
        return message;
    }

    /**
     * Marches message
     *
     * @param messageText Message text
     * @param chatId      Chat id
     */
    private String[] matchesMessage(String messageText, Long chatId) {
        Matcher matcher = pattern.matcher(messageText);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String message = matcher.group(3);
            LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            if (LocalDateTime.now().isBefore(localDateTime)) {
                saveTask(chatId, localDateTime, message);
                sender.send("Задача сохранена!");
                return new String[]{"INFO", "Task saved."};
            } else {
                sender.send("Заданная дата выполнения задачи должна быть позже текущей! Попробуйте снова!");
                return new String[]{"WARN", "Datetime warning!"};
            }
        } else {
            sender.send("Неверная команда. Для сохранения задачи, наберите её в формате 'дд.мм.гггг чч:мм текст_задачи'. Для вывода информации введите '/info'");
            return new String[]{"WARN", "Command is not available!"};
        }
    }

    /**
     * Save task to DB
     *
     * @param chatId        Chat id
     * @param localDateTime Date and time task
     * @param message       Message
     */
    private void saveTask(Long chatId, LocalDateTime localDateTime, String message) {
        notificationTaskRepository.save(
                new NotificationTask(
                        localDateTime,
                        chatId,
                        message));
    }
}
