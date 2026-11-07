package co.edu.corhuila.taskmanager.users.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Creates the single ADMIN account at startup from ADMIN_EMAIL / ADMIN_PASSWORD
 * (ADR-005, BR-63). No endpoint can create or promote an ADMIN.
 */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserService userService;
    private final String adminEmail;
    private final String adminPassword;

    public AdminSeeder(UserService userService,
                       @Value("${app.admin.email:}") String adminEmail,
                       @Value("${app.admin.password:}") String adminPassword) {
        this.userService = userService;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.info("Admin seed skipped: ADMIN_EMAIL or ADMIN_PASSWORD is empty");
            return;
        }
        if (userService.createAdminIfMissing(adminEmail, adminPassword)) {
            log.info("Admin account created");
        }
    }
}
