package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private HttpSession httpSession;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetRegistrationPageThenReturnRegistrationView() {
        String result = userController.getRegistrationPage();

        assertThat(result).isEqualTo("users/register");
    }

    @Test
    void whenRegisterNewUserThenRedirectToLogin() {
        User user = createTestUser();
        User savedUser = createTestUser();
        savedUser.setId(1);

        when(userService.register(user)).thenReturn(Optional.of(savedUser));

        String result = userController.register(user, model);

        assertThat(result).isEqualTo("redirect:/users/login");
        verify(userService).register(user);
    }

    @Test
    void whenRegisterExistingUserThenReturnRegisterViewWithMessage() {
        User user = createTestUser();

        when(userService.register(user)).thenReturn(Optional.empty());

        String result = userController.register(user, model);

        assertThat(result).isEqualTo("users/register");
        verify(userService).register(user);
        verify(model).addAttribute("message", "Пользователь с такой почтой уже существует");
    }

    @Test
    void whenGetLoginPageWithoutErrorMessageThenReturnLoginView() {
        when(httpSession.getAttribute("errormessage")).thenReturn(null);

        String result = userController.getLoginPage(httpSession, model);

        assertThat(result).isEqualTo("users/login");
        verify(httpSession).getAttribute("errormessage");
    }

    @Test
    void whenGetLoginPageWithErrorMessageThenAddToModelAndRemoveFromSession() {
        String errorMessage = "Test error message";
        when(httpSession.getAttribute("errormessage")).thenReturn(errorMessage);

        String result = userController.getLoginPage(httpSession, model);

        assertThat(result).isEqualTo("users/login");
        verify(httpSession).getAttribute("errormessage");
        verify(model).addAttribute("errormessage", errorMessage);
        verify(httpSession).removeAttribute("errormessage");
    }

    @Test
    void whenLoginUserWithValidCredentialsThenRedirectToHome() {
        User user = createTestUser();
        User authenticatedUser = createTestUser();
        authenticatedUser.setId(1);

        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.of(authenticatedUser));
        when(httpServletRequest.getSession()).thenReturn(httpSession);

        String result = userController.loginUser(user, httpServletRequest, model);

        assertThat(result).isEqualTo("redirect:/");
        verify(userService).findByEmailAndPassword(user.getEmail(), user.getPassword());
        verify(httpServletRequest).getSession();
        verify(httpSession).setAttribute("user", authenticatedUser);
    }

    @Test
    void whenLoginUserWithInvalidCredentialsThenReturnLoginViewWithError() {
        User user = createTestUser();

        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.empty());

        String result = userController.loginUser(user, httpServletRequest, model);

        assertThat(result).isEqualTo("users/login");
        verify(userService).findByEmailAndPassword(user.getEmail(), user.getPassword());
        verify(model).addAttribute("error", "Почта или пароль введены неверно");
    }

    @Test
    void whenLogoutThenInvalidateSessionAndRedirectToLogin() {
        String result = userController.logout(httpSession);

        assertThat(result).isEqualTo("redirect:/users/login");
        verify(httpSession).invalidate();
    }

    private User createTestUser() {
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        return user;
    }
}