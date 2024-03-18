package pro.sky.telegrambot.exception;

public class SendResponseException extends RuntimeException {
    public SendResponseException(String message) {
        super(message);
    }
}
