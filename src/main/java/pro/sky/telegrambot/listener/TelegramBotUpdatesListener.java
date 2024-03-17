package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private NotificationTaskService notificationTaskService;

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here
            String[] message = notificationTaskService.sendMessage(update, telegramBot);
            log(message);
            notificationTaskService.scheduling();
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void log(String[] message) {
        switch (message[0]) {
            case "INFO": logger.info(message[1]);
                break;
            case "WARN": logger.warn(message[1]);
                break;
            case "START":
            case "ABOUT_BOT": logger.info("Bot message: '{}'", message[1]);
            default: logger.warn("Message is null.");
        }
    }

}
