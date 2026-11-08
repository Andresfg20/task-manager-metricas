package co.edu.corhuila.taskmanager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

/**
 * Helpers for module-integration tests: every test registers its own users through
 * the API, so tests never depend on each other (11-quality/testing-strategy.md).
 */
public final class ApiTestSupport {

    public static final String PASSWORD = "Secret123";

    private ApiTestSupport() {
    }

    public static String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@test.local";
    }

    public static String credentials(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }

    /** Registers a new USER and returns its bearer token. */
    public static String registerAndGetToken(MockMvc mockMvc) throws Exception {
        String body = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials(uniqueEmail(), PASSWORD)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    /** Logs in and returns the bearer token. */
    public static String loginAndGetToken(MockMvc mockMvc, String email, String password) throws Exception {
        String body = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials(email, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    public static String bearer(String token) {
        return "Bearer " + token;
    }
}
