package ru.job4j.cinema.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/tickets")
@AllArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final FilmSessionService filmSessionService;

    @GetMapping("/buy/{sessionId}")
    public String getBuyTicketPage(@PathVariable int sessionId, Model model) {
        var sessionDetail = filmSessionService.findById(sessionId);
        if (sessionDetail == null) {
            model.addAttribute("message", "Сеанс не найден");
            return "errors/404";
        }
        
        model.addAttribute("sessionDetail", sessionDetail);
        return "tickets/buy";
    }

    @PostMapping("/buy")
    public String buyTicket(@ModelAttribute Ticket ticket, 
                           HttpSession httpSession, 
                           Model model) {
        
        var user = (ru.job4j.cinema.model.User) httpSession.getAttribute("user");
        if (user == null) {
            model.addAttribute("message", "Необходимо авторизоваться для покупки билетов");
            return "redirect:/users/login";
        }
        
        ticket.setUserId(user.getId());
        Optional<Ticket> savedTicket = ticketService.buyTicket(ticket);
        
        if (savedTicket.isEmpty()) {
            model.addAttribute("message", "Не удалось приобрести билет. Возможно, место уже занято.");
            return "tickets/error";
        }
        
        model.addAttribute("ticket", savedTicket.get());
        return "tickets/success";
    }
}
