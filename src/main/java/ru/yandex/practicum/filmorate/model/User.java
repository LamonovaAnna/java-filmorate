package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {
    private long id;
    private String name;

    @NonNull
    private String email;
    @NonNull
    private String login;
    @NonNull
    private LocalDate birthday;
}