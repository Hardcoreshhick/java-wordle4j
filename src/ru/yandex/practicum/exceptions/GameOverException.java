package ru.yandex.practicum.exceptions;

public class GameOverException extends RuntimeException {
    public GameOverException(String message) {
        super(message);
    }
}
