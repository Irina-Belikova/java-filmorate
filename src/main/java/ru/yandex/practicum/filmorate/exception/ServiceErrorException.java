package ru.yandex.practicum.filmorate.exception;

public class ServiceErrorException extends RuntimeException {
    public ServiceErrorException(String message) {
        super(message);
    }
}
