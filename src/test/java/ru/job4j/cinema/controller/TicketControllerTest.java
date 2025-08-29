package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.dto.FilmSessionDetailDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @Mock
    private FilmSessionService filmSessionService;

    @Mock
    private Model model;

    @Mock
    private HttpSession httpSession;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private TicketController ticketController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetBuyTicketPageWithValidSessionThenReturnBuyView() {
        int sessionId = 1;
        FilmSessionDetailDto sessionDetail = createTestFilmSessionDetailDto();

        when(filmSessionService.findById(sessionId)).thenReturn(sessionDetail);

        String result = ticketController.getBuyTicketPage(sessionId, model);

        assertThat(result).isEqualTo("tickets/buy");
        verify(filmSessionService).findById(sessionId);
        verify(model).addAttribute("sessionDetail", sessionDetail);
    }

    @Test
    void whenGetBuyTicketPageWithInvalidSessionThenReturn404() {
        int sessionId = 999;

        when(filmSessionService.findById(sessionId)).thenReturn(null);

        String result = ticketController.getBuyTicketPage(sessionId, model);

        assertThat(result).isEqualTo("errors/404");
        verify(filmSessionService).findById(sessionId);
        verify(model).addAttribute("message", "Сеанс не найден");
    }

    @Test
    void whenBuyTicketWithoutUserThenRedirectToLogin() {
        Ticket ticket = createTestTicket();

        when(httpSession.getAttribute("user")).thenReturn(null);

        String result = ticketController.buyTicket(ticket, httpSession, redirectAttributes, model);

        assertThat(result).isEqualTo("redirect:/users/login");
        verify(httpSession).getAttribute("user");
        verify(redirectAttributes).addFlashAttribute("errormessage", "Необходимо авторизоваться для покупки билетов");
        verify(ticketService, never()).buyTicket(any());
    }

    @Test
    void whenBuyTicketWithUserWithoutIdThenRedirectToLogin() {
        Ticket ticket = createTestTicket();
        User user = new User();
        user.setId(null);

        when(httpSession.getAttribute("user")).thenReturn(user);

        String result = ticketController.buyTicket(ticket, httpSession, redirectAttributes, model);

        assertThat(result).isEqualTo("redirect:/users/login");
        verify(httpSession).getAttribute("user");
        verify(redirectAttributes).addFlashAttribute("errormessage", "Необходимо авторизоваться для покупки билетов");
        verify(ticketService, never()).buyTicket(any());
    }

    @Test
    void whenBuyTicketSuccessfullyThenReturnSuccessView() {
        Ticket ticket = createTestTicket();
        User user = createTestUser();
        Ticket savedTicket = createTestTicket();
        savedTicket.setId(1);
        savedTicket.setUserId(user.getId());

        when(httpSession.getAttribute("user")).thenReturn(user);
        when(ticketService.buyTicket(ticket)).thenReturn(Optional.of(savedTicket));

        String result = ticketController.buyTicket(ticket, httpSession, redirectAttributes, model);

        assertThat(result).isEqualTo("tickets/success");
        assertThat(ticket.getUserId()).isEqualTo(user.getId());
        verify(httpSession).getAttribute("user");
        verify(ticketService).buyTicket(ticket);
        verify(model).addAttribute("ticket", savedTicket);
    }

    @Test
    void whenBuyTicketFailsThenReturnErrorView() {
        Ticket ticket = createTestTicket();
        User user = createTestUser();

        when(httpSession.getAttribute("user")).thenReturn(user);
        when(ticketService.buyTicket(ticket)).thenReturn(Optional.empty());

        String result = ticketController.buyTicket(ticket, httpSession, redirectAttributes, model);

        assertThat(result).isEqualTo("tickets/error");
        assertThat(ticket.getUserId()).isEqualTo(user.getId());
        verify(httpSession).getAttribute("user");
        verify(ticketService).buyTicket(ticket);
        verify(model).addAttribute("message", "Не удалось приобрести билет. Возможно, место уже занято.");
    }

    @Test
    void whenBuyTicketThenSetUserIdFromSession() {
        Ticket ticket = createTestTicket();
        ticket.setUserId(null);
        User user = createTestUser();
        Ticket savedTicket = createTestTicket();
        savedTicket.setId(1);
        savedTicket.setUserId(user.getId());

        when(httpSession.getAttribute("user")).thenReturn(user);
        when(ticketService.buyTicket(ticket)).thenReturn(Optional.of(savedTicket));

        ticketController.buyTicket(ticket, httpSession, redirectAttributes, model);

        assertThat(ticket.getUserId()).isEqualTo(user.getId());
        verify(ticketService).buyTicket(ticket);
    }

    private Ticket createTestTicket() {
        Ticket ticket = new Ticket();
        ticket.setSessionId(1);
        ticket.setRowNumber(5);
        ticket.setPlaceNumber(10);
        return ticket;
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        return user;
    }

    private FilmSessionDetailDto createTestFilmSessionDetailDto() {
        return new FilmSessionDetailDto(
                1,
                LocalDateTime.of(2023, 10, 1, 18, 0),
                LocalDateTime.of(2023, 10, 1, 20, 0),
                500,
                "Test Film",
                "Description",
                2023,
                18,
                120,
                "Action",
                "/files/1",
                "Hall 1",
                "Main hall",
                10,
                15
        );
    }
}