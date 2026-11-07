package co.edu.corhuila.taskmanager.tasks.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.corhuila.taskmanager.common.dto.PageResponse;
import co.edu.corhuila.taskmanager.common.security.CurrentUser;
import co.edu.corhuila.taskmanager.tasks.dto.TaskDueDateRequest;
import co.edu.corhuila.taskmanager.tasks.dto.TaskRequest;
import co.edu.corhuila.taskmanager.tasks.dto.TaskResponse;
import co.edu.corhuila.taskmanager.tasks.dto.TaskStatusRequest;
import co.edu.corhuila.taskmanager.tasks.entity.TaskStatus;
import co.edu.corhuila.taskmanager.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Task endpoints (07-api/endpoints.md). Holds no business rules: it validates input,
 * passes the authenticated owner id and delegates to TaskService.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService taskService;
    private final CurrentUser currentUser;

    public TaskController(TaskService taskService, CurrentUser currentUser) {
        this.taskService = taskService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "List own tasks, filtered by status or overdue, sorted by due date")
    public PageResponse<TaskResponse> list(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "false") boolean overdue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return taskService.findPage(currentUser.id(), status, overdue, page, size);
    }

    @GetMapping("/calendar")
    @Operation(summary = "Own dated tasks between two dates (Calendar View)")
    public List<TaskResponse> calendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return taskService.findCalendar(currentUser.id(), from, to);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one own task")
    public TaskResponse get(@PathVariable UUID id) {
        return taskService.findById(currentUser.id(), id);
    }

    @PostMapping
    @Operation(summary = "Create a task (always PENDING)")
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        TaskResponse created = taskService.create(currentUser.id(), request);
        return ResponseEntity.created(URI.create("/api/v1/tasks/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit title, description and due date")
    public TaskResponse update(@PathVariable UUID id, @Valid @RequestBody TaskRequest request) {
        return taskService.update(currentUser.id(), id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Complete or reopen a task")
    public TaskResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody TaskStatusRequest request) {
        return taskService.changeStatus(currentUser.id(), id, request.status());
    }

    @PatchMapping("/{id}/due-date")
    @Operation(summary = "Reschedule a task (changes only the due date)")
    public TaskResponse reschedule(@PathVariable UUID id, @Valid @RequestBody TaskDueDateRequest request) {
        return taskService.reschedule(currentUser.id(), id, request.dueDate());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task permanently")
    public void delete(@PathVariable UUID id) {
        taskService.delete(currentUser.id(), id);
    }
}
