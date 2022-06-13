package ru.yandex.practicum.filmorate.exception;

public class ValidationException extends Exception {
    private final String parameter;

    public ValidationException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }

    public String getMessage() {
        return String.format("Incorrect parameter: %s", getParameter());
    }
}
