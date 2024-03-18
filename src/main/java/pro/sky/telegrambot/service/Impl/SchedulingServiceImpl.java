package pro.sky.telegrambot.service.Impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.SchedulingService;
import pro.sky.telegrambot.service.helper.Sender;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SchedulingServiceImpl implements SchedulingService {
    private Sender sender;

    private final NotificationTaskRepository notificationTaskRepository;

    public SchedulingServiceImpl(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Override
    @Scheduled(cron = "10 0/1 * * * *")
    public void scheduling(Update update, TelegramBot telegramBot) {
        sender = new Sender(update, telegramBot);
        List<NotificationTask> notificationTaskList = notificationTaskRepository.
                findByDatetime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        notificationTaskList
                .stream()
                .map(NotificationTask::getMessage)
                .forEach(this::send);
    }

    private void send(String s) {
        sender.send(s);
    }
}
