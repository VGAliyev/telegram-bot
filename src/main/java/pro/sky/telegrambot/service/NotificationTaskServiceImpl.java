package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.SendResponseException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {
    private final Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    private Update update;
    private TelegramBot telegramBot;
    private Logger logger;

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskServiceImpl(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Override
    public void sendMessage(Update update, TelegramBot telegramBot, Logger logger) {
        init(update, telegramBot, logger);
        if (update.message() != null) {
            String messageText = update.message().text();
            Long chatId = update.message().chat().id();
            switch (messageText) {
                case "/start" ->
                        send("Привет. Для сохранения задачи, наберите её в формате 'дд.мм.гггг чч:мм текст_задачи'. Для вывода информации введите '/info'");
                case "/info" ->
                        send("Telegram bot - напоминание о делах. Автор: Владислав Алиев, 2024г.");
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
                            send("Задача сохранена!");
                            logger.info("Task saved!");
                        } else {
                            send("Заданная дата выполнения задачи должна быть позже текущей! Попробуйте снова!");
                        }
                    } else {
                        send("Неверная команда. Для сохранения задачи, наберите её в формате 'дд.мм.гггг чч:мм текст_задачи'. Для вывода информации введите '/info'");
                    }
                }
            }
        } else {
            logger.warn("Message is null!");
        }
    }

    @Override
    @Scheduled(cron = "0 0/1 * * * *")
    public void scheduling() {
        notificationTaskRepository.findByDatetime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .stream()
                .map(NotificationTask::getMessage)
                .forEach(this::send);
    }

    public void init(Update update, TelegramBot telegramBot, Logger logger) {
        this.update = update;
        this.telegramBot = telegramBot;
        this.logger = logger;
    }

    private void send(String s) {
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
