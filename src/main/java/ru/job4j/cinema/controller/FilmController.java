package ru.job4j.cinema.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;

@Controller
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final FilmSessionService filmSessionService;

    @GetMapping
    public String getAll(Model model) {
            model.addAttribute("films", filmService.findAll());
            return "films/list";
    }

    @GetMapping("/{id}/sessions")
    public String getFilmSessions(@PathVariable int id, Model model) {
        var filmOptional = filmService.findById(id);
        if (filmOptional.isEmpty()) {
            model.addAttribute("message", "Фильм не найден");
            return "errors/404";
        }
        
        var film = filmOptional.get();
        var sessions = filmSessionService.findByFilmId(id);
        
        model.addAttribute("film", film);
        model.addAttribute("sessions", sessions);
        return "films/sessions";
    }
}
