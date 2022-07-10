package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    private String name;
    private Set<Long> friends = new HashSet<>();

    @NonNull
    private String email;
    @NonNull
    private String login;
    @NonNull
    private LocalDate birthday;
}