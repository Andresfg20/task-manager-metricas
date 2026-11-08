package co.edu.corhuila.taskmanager.users.controller;

import static co.edu.corhuila.taskmanager.ApiTestSupport.bearer;
import static co.edu.corhuila.taskmanager.ApiTestSupport.loginAndGetToken;
import static co.edu.corhuila.taskmanager.ApiTestSupport.registerAndGetToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("TC-025 BR-33 BR-63: the seeded ADMIN lists users without password hashes")
    void listUsers_asAdmin_returnsPageWithoutPasswordHash() throws Exception {
        String adminToken = loginAndGetToken(mockMvc, "admin@test.local", "Admin12345");

        mockMvc.perform(get("/api/v1/users").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").exists())
                .andExpect(jsonPath("$.content[0].role").exists())
                .andExpect(jsonPath("$.content[0].passwordHash").doesNotExist())
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    @DisplayName("TC-026 BR-33: a USER gets 403 on the user list")
    void listUsers_asUser_returns403() throws Exception {
        String userToken = registerAndGetToken(mockMvc);

        mockMvc.perform(get("/api/v1/users").header("Authorization", bearer(userToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("BR-32: the user list needs a token")
    void listUsers_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }
}
