package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.SendResponseException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {
    private final Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    private Update update;
    private TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskServiceImpl(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Override
    public String[] sendMessage(Update update, TelegramBot telegramBot) {
        init(update, telegramBot);
        String[] message = new String[2];
        if (update.message() != null) {
            String messageText = update.message().text();
            Long chatId = update.message().chat().id();
            switch (messageText) {
                case "/start" -> {
                    String hello = "Привет. Для сохранения задачи, наберите её в формате 'дд.мм.гггг чч:мм текст_задачи'. Для вывода информации введите '/info'";
                    send(hello);
                    message = new String[]{"START", hello};
                }
                case "/info" -> {
                    String info = "Telegram bot - напоминание о делах. Автор: Владислав Алиев, 2024г.";
                    send(info);
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

    @Override
    @Scheduled(cron = "10 0/1 * * * *")
    public void scheduling() {
        List<NotificationTask> notificationTaskList = notificationTaskRepository.
                findByDatetime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        notificationTaskList
                .stream()
                .map(NotificationTask::getMessage)
                .forEach(this::send);
    }

    /**
     * Initialisation NotificationTaskService
     *
     * @param update      Update from com.pengrad.telegrambot.model
     * @param telegramBot TelegramBot from com.pengrad.telegrambot
     */
    private void init(Update update, TelegramBot telegramBot) {
        this.update = update;
        this.telegramBot = telegramBot;
    }

    /**
     * Send message to chat
     *
     * @param m Message
     */
    private void send(String m) {
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), m);
        SendResponse response = telegramBot.execute(sendMessage);
        if (!response.isOk()) {
            throw new SendResponseException("SendResponseError: " + response.errorCode());
        }
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
                send("Задача сохранена!");
                return new String[]{"INFO", "Task saved."};
            } else {
                send("Заданная дата выполнения задачи должна быть позже текущей! Попробуйте снова!");
                return new String[]{"WARN", "Datetime warning!"};
            }
        } else {
            send("Неверная команда. Для сохранения задачи, наберите её в формате 'дд.мм.гггг чч:мм текст_задачи'. Для вывода информации введите '/info'");
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
