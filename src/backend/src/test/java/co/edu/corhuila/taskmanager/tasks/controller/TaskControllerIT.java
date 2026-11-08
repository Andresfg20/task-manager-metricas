package co.edu.corhuila.taskmanager.tasks.controller;

import static co.edu.corhuila.taskmanager.ApiTestSupport.bearer;
import static co.edu.corhuila.taskmanager.ApiTestSupport.registerAndGetToken;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerIT {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("America/Bogota"));

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        token = registerAndGetToken(mockMvc);
    }

    private String createTask(String authToken, String title, LocalDate dueDate) throws Exception {
        String due = dueDate == null ? "null" : "\"" + dueDate + "\"";
        String body = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", bearer(authToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + title + "\",\"dueDate\":" + due + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    private void complete(String id) throws Exception {
        mockMvc.perform(patch("/api/v1/tasks/" + id + "/status")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TC-009 BR-32: listing tasks without a token returns 401")
    void listTasks_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("RF-04 BR-05: a created task is PENDING and has a Location header")
    void create_withValidData_returns201Pending() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Submit report\",\"status\":\"COMPLETED\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.overdue").value(false));
    }

    @Test
    @DisplayName("TC-011 BR-02: a blank title returns 400")
    void create_withBlankTitle_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0].field").value("title"));
    }

    @Test
    @DisplayName("TC-012 BR-30 BR-31: another user's task answers 404 on every route")
    void getTask_ofAnotherUser_returns404() throws Exception {
        String otherToken = registerAndGetToken(mockMvc);
        String othersTask = createTask(otherToken, "Private", null);

        mockMvc.perform(get("/api/v1/tasks/" + othersTask).header("Authorization", bearer(token)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"));
        mockMvc.perform(put("/api/v1/tasks/" + othersTask)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Hijacked\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/v1/tasks/" + othersTask).header("Authorization", bearer(token)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/tasks/" + othersTask).header("Authorization", bearer(otherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Private"));
    }

    @Test
    @DisplayName("TC-014 BR-40: a deleted task is gone")
    void delete_ownTask_returns204ThenNotFound() throws Exception {
        String id = createTask(token, "Temporary", TODAY);

        mockMvc.perform(delete("/api/v1/tasks/" + id).header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/tasks/" + id).header("Authorization", bearer(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("RF-08 RF-09 BR-13 BR-14: complete then reopen through the API")
    void changeStatus_completeThenReopen() throws Exception {
        String id = createTask(token, "Toggle me", null);

        mockMvc.perform(patch("/api/v1/tasks/" + id + "/status")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.completedAt").exists());
        mockMvc.perform(patch("/api/v1/tasks/" + id + "/status")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"PENDING\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.completedAt").doesNotExist());
    }

    @Test
    @DisplayName("TC-018 BR-50 BR-51: filter by status; undated tasks last")
    void listTasks_filtersByStatusAndOrdersUndatedLast() throws Exception {
        createTask(token, "Undated", null);
        createTask(token, "Later", TODAY.plusDays(5));
        createTask(token, "Sooner", TODAY.plusDays(1));
        complete(createTask(token, "Finished", TODAY.plusDays(2)));

        mockMvc.perform(get("/api/v1/tasks").param("status", "PENDING").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].title").value("Sooner"))
                .andExpect(jsonPath("$.content[1].title").value("Later"))
                .andExpect(jsonPath("$.content[2].title").value("Undated"));
        mockMvc.perform(get("/api/v1/tasks").param("status", "COMPLETED").header("Authorization", bearer(token)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Finished"));
    }

    @Test
    @DisplayName("TC-019 BR-52: lists are paginated")
    void listTasks_paginates() throws Exception {
        createTask(token, "One", null);
        createTask(token, "Two", null);
        createTask(token, "Three", null);

        mockMvc.perform(get("/api/v1/tasks").param("size", "2").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    @DisplayName("TC-021 BR-21 BR-51: the overdue filter returns only pending past tasks")
    void listTasks_withOverdueFilter_returnsOnlyOverdue() throws Exception {
        createTask(token, "Late", TODAY.minusDays(3));
        complete(createTask(token, "Late but done", TODAY.minusDays(3)));
        createTask(token, "Future", TODAY.plusDays(3));

        mockMvc.perform(get("/api/v1/tasks").param("overdue", "true").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Late"))
                .andExpect(jsonPath("$.content[0].overdue").value(true));
    }

    @Test
    @DisplayName("TC-022 BR-24: the calendar returns only dated tasks in range")
    void calendar_returnsOnlyDatedTasksInRange() throws Exception {
        createTask(token, "In range", TODAY.plusDays(2));
        createTask(token, "Out of range", TODAY.plusDays(60));
        createTask(token, "Undated", null);

        mockMvc.perform(get("/api/v1/tasks/calendar")
                        .param("from", TODAY.toString())
                        .param("to", TODAY.plusDays(6).toString())
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("In range"));
        mockMvc.perform(get("/api/v1/tasks/calendar")
                        .param("from", TODAY.toString())
                        .param("to", TODAY.minusDays(1).toString())
                        .header("Authorization", bearer(token)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_DATE_RANGE"));
    }

    @Test
    @DisplayName("RF-06 RF-17 BR-15 BR-23: edit and reschedule keep the status")
    void updateAndReschedule_keepStatus() throws Exception {
        String id = createTask(token, "Draft", TODAY);
        complete(id);

        mockMvc.perform(put("/api/v1/tasks/" + id)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Final\",\"description\":\"Reviewed\",\"dueDate\":\"" + TODAY + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Final"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
        mockMvc.perform(patch("/api/v1/tasks/" + id + "/due-date")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dueDate\":\"" + TODAY.plusDays(4) + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dueDate").value(TODAY.plusDays(4).toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Malformed enum values return 400 MALFORMED_REQUEST")
    void changeStatus_withUnknownStatus_returns400() throws Exception {
        String id = createTask(token, "Task", null);

        mockMvc.perform(patch("/api/v1/tasks/" + id + "/status")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"OVERDUE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }
}
