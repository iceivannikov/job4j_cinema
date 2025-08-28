package ru.job4j.cinema.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.service.FilmSessionService;

@Controller
@RequestMapping("/sessions")
@AllArgsConstructor
public class FilmSessionController {

    private final FilmSessionService filmSessionService;

    @GetMapping
    public String getAll(Model model) {
            model.addAttribute("sessions", filmSessionService.findAll());
            return "sessions/list";
        }
    }
