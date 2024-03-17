package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationTaskServiceImplTest {
    @InjectMocks
    private NotificationTaskServiceImpl notificationTaskServiceImpl;

    @Test
    void sendMessage() {
        assertEquals(new String[2].length, notificationTaskServiceImpl.sendMessage(new Update(), new TelegramBot("")).length);
    }
}