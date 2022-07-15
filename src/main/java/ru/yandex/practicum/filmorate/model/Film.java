package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;


@Data
@Builder
public class Film {
    private long id;
    private int rate;
    private Mpa mpa;
    private LinkedHashSet<Genre> genres;
    private Set<Long> likes = new HashSet<>();

    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    private Long duration;
}