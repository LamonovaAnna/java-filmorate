package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaDaoService;

import java.util.List;

@RestController
public class MpaController {
    private final MpaDaoService mpaService;

    @Autowired
    public MpaController(MpaDaoService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpa() {
        return mpaService.getMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa findMpaById(@PathVariable long id) {
        return mpaService.findMpaById(id);
    }
}
