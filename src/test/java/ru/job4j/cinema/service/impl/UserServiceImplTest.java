package ru.job4j.cinema.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenRegisterNewUserThenReturnUser() {
        User user = User.builder()
                .fullName("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        User savedUser = User.builder()
                .id(1)
                .fullName("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(Optional.of(savedUser));

        Optional<User> result = userService.register(user);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getFullName()).isEqualTo("Test User");

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(user);
    }

    @Test
    void whenRegisterExistingUserThenReturnEmpty() {
        User user = User.builder()
                .fullName("Test User")
                .email("existing@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        Optional<User> result = userService.register(user);

        assertThat(result).isEmpty();

        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenFindByEmailAndPasswordWithCorrectCredentialsThenReturnUser() {
        User user = User.builder()
                .id(1)
                .fullName("Test User")
                .email("test@example.com")
                .password("correctPassword")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmailAndPassword("test@example.com", "correctPassword");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void whenFindByEmailAndPasswordWithIncorrectPasswordThenReturnEmpty() {
        User user = User.builder()
                .id(1)
                .fullName("Test User")
                .email("test@example.com")
                .password("correctPassword")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmailAndPassword("test@example.com", "wrongPassword");

        assertThat(result).isEmpty();

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void whenFindByEmailAndPasswordWithNonExistingEmailThenReturnEmpty() {
        when(userRepository.findByEmail("nonexisting@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmailAndPassword("nonexisting@example.com", "anyPassword");

        assertThat(result).isEmpty();

        verify(userRepository).findByEmail("nonexisting@example.com");
    }

    @Test
    void whenFindByExistingIdThenReturnUser() {
        User user = User.builder()
                .id(1)
                .fullName("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getFullName()).isEqualTo("Test User");

        verify(userRepository).findById(1);
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(999);

        assertThat(result).isEmpty();

        verify(userRepository).findById(999);
    }

    @Test
    void whenFindByEmailAndPasswordWithNullPasswordInUserThenReturnEmpty() {
        User user = User.builder()
                .id(1)
                .fullName("Test User")
                .email("test@example.com")
                .password(null)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmailAndPassword("test@example.com", "anyPassword");

        assertThat(result).isEmpty();

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void whenFindByEmailAndPasswordWithNullInputPasswordThenReturnEmpty() {
        User user = User.builder()
                .id(1)
                .fullName("Test User")
                .email("test@example.com")
                .password("somePassword")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmailAndPassword("test@example.com", null);

        assertThat(result).isEmpty();

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void whenFindByEmailAndPasswordWithBothPasswordsNullThenReturnUser() {
        User user = User.builder()
                .id(1)
                .fullName("Test User")
                .email("test@example.com")
                .password(null)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmailAndPassword("test@example.com", null);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);

        verify(userRepository).findByEmail("test@example.com");
    }
}