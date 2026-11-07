package co.edu.corhuila.taskmanager.users.service;

import java.util.Locale;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.corhuila.taskmanager.common.dto.PageResponse;
import co.edu.corhuila.taskmanager.common.exception.ConflictException;
import co.edu.corhuila.taskmanager.common.exception.InvalidCredentialsException;
import co.edu.corhuila.taskmanager.users.dto.UserResponse;
import co.edu.corhuila.taskmanager.users.entity.Role;
import co.edu.corhuila.taskmanager.users.entity.User;
import co.edu.corhuila.taskmanager.users.repository.UserRepository;

/**
 * Owns the User entity: registration, credential checks and the ADMIN listing.
 * Other modules use this service, never the repository (AB-01, AB-02).
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Creates a USER account (BR-60, BR-61, BR-62). */
    @Transactional
    public UserResponse register(String email, String rawPassword) {
        return UserResponse.from(create(email, rawPassword, Role.USER));
    }

    /** Returns the user when email and password match; otherwise a generic error (BR-61). */
    @Transactional(readOnly = true)
    public UserResponse authenticate(String email, String rawPassword) {
        return userRepository.findByEmail(normalizeEmail(email))
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
                .map(UserResponse::from)
                .orElseThrow(InvalidCredentialsException::new);
    }

    /** Lists users, oldest first. Called only from the ADMIN endpoint (BR-33). */
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> findAll(int page, int size) {
        var pageable = PageRequest.of(PageResponse.safePage(page), PageResponse.safeSize(size),
                Sort.by("createdAt").ascending());
        return PageResponse.from(userRepository.findAll(pageable), UserResponse::from);
    }

    /** Creates the seeded ADMIN if its email is not registered yet (BR-63). */
    @Transactional
    public boolean createAdminIfMissing(String email, String rawPassword) {
        if (userRepository.existsByEmail(normalizeEmail(email))) {
            return false;
        }
        create(email, rawPassword, Role.ADMIN);
        return true;
    }

    private User create(String email, String rawPassword, Role role) {
        String normalizedEmail = normalizeEmail(email);
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("EMAIL_ALREADY_REGISTERED", "This email is already registered.");
        }
        return userRepository.save(new User(normalizedEmail, passwordEncoder.encode(rawPassword), role));
    }

    static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
