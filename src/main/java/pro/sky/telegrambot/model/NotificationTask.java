package pro.sky.telegrambot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime datetime;
    private Long chatId;
    private String message;

    public NotificationTask() {}

    public NotificationTask(Long id, LocalDateTime datetime, Long chatId, String message) {
        this.id = id;
        this.datetime = datetime;
        this.chatId = chatId;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(id, that.id) && Objects.equals(datetime, that.datetime) && Objects.equals(chatId, that.chatId) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, datetime, chatId, message);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", datetime=" + datetime +
                ", chatId=" + chatId +
                ", message='" + message + '\'' +
                '}';
    }
}
