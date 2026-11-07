package co.edu.corhuila.taskmanager.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.corhuila.taskmanager.common.exception.ConflictException;
import co.edu.corhuila.taskmanager.common.exception.InvalidCredentialsException;
import co.edu.corhuila.taskmanager.users.dto.UserResponse;
import co.edu.corhuila.taskmanager.users.entity.Role;
import co.edu.corhuila.taskmanager.users.entity.User;
import co.edu.corhuila.taskmanager.users.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("TC-001 BR-60 BR-61 BR-62: email stored lowercase, password hashed, role USER")
    void register_withMixedCaseEmail_savesLowercaseUserRole() {
        when(userRepository.existsByEmail("camila@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.register("  Camila@Example.com ", "Secret123");

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getEmail()).isEqualTo("camila@example.com");
        assertThat(saved.getValue().getPasswordHash()).isNotEqualTo("Secret123");
        assertThat(passwordEncoder.matches("Secret123", saved.getValue().getPasswordHash())).isTrue();
        assertThat(response.role()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("TC-002 BR-60: a registered email cannot register again")
    void register_whenEmailExists_throwsConflict() {
        when(userRepository.existsByEmail("camila@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("camila@example.com", "Secret123"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already registered");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-003 BR-61: a wrong password gives the generic credentials error")
    void authenticate_withWrongPassword_throwsInvalidCredentials() {
        User user = new User("camila@example.com", passwordEncoder.encode("Secret123"), Role.USER);
        when(userRepository.findByEmail("camila@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.authenticate("camila@example.com", "Wrong1234"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("BR-61: an unknown email gives the same generic credentials error")
    void authenticate_withUnknownEmail_throwsInvalidCredentials() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.authenticate("nobody@example.com", "Secret123"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("BR-63: the admin is created once, with role ADMIN")
    void createAdminIfMissing_createsAdminOnlyOnce() {
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false, false, true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(userService.createAdminIfMissing("admin@example.com", "Admin12345")).isTrue();
        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getRole()).isEqualTo(Role.ADMIN);

        assertThat(userService.createAdminIfMissing("admin@example.com", "Admin12345")).isFalse();
    }
}
