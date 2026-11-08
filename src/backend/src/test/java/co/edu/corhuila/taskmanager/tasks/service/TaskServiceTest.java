package co.edu.corhuila.taskmanager.tasks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.corhuila.taskmanager.common.exception.NotFoundException;
import co.edu.corhuila.taskmanager.tasks.dto.TaskRequest;
import co.edu.corhuila.taskmanager.tasks.dto.TaskResponse;
import co.edu.corhuila.taskmanager.tasks.entity.Task;
import co.edu.corhuila.taskmanager.tasks.entity.TaskStatus;
import co.edu.corhuila.taskmanager.tasks.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-02T15:00:00Z");
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 2);
    private static final UUID OWNER = UUID.randomUUID();
    private static final UUID TASK_ID = UUID.randomUUID();

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneId.of("America/Bogota"));
        taskService = new TaskService(taskRepository, clock);
    }

    private void saveReturnsArgument() {
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Task givenOwnTask(Task task) {
        when(taskRepository.findByIdAndOwnerId(TASK_ID, OWNER)).thenReturn(Optional.of(task));
        return task;
    }

    @Test
    @DisplayName("TC-010 BR-02 BR-05: title is trimmed and a new task is PENDING")
    void create_trimsTitleAndStartsPending() {
        saveReturnsArgument();

        TaskResponse response = taskService.create(OWNER, new TaskRequest("  Submit report  ", "  ", null));

        assertThat(response.title()).isEqualTo("Submit report");
        assertThat(response.description()).isNull();
        assertThat(response.status()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.completedAt()).isNull();
    }

    @Test
    @DisplayName("TC-013 BR-15: editing a COMPLETED task keeps its status")
    void update_onCompletedTask_keepsStatus() {
        Task task = givenOwnTask(new Task(OWNER, "Old", null, null));
        task.complete(NOW);
        saveReturnsArgument();

        TaskResponse response = taskService.update(OWNER, TASK_ID,
                new TaskRequest("New", "Details", TODAY.plusDays(3)));

        assertThat(response.title()).isEqualTo("New");
        assertThat(response.description()).isEqualTo("Details");
        assertThat(response.dueDate()).isEqualTo(TODAY.plusDays(3));
        assertThat(response.status()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    @DisplayName("TC-015 BR-11 BR-13: completing stores completedAt")
    void changeStatus_toCompleted_setsCompletedAt() {
        givenOwnTask(new Task(OWNER, "Task", null, null));
        saveReturnsArgument();

        TaskResponse response = taskService.changeStatus(OWNER, TASK_ID, TaskStatus.COMPLETED);

        assertThat(response.status()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(response.completedAt()).isEqualTo(NOW);
    }

    @Test
    @DisplayName("TC-016 BR-12: completing a completed task changes nothing")
    void changeStatus_whenAlreadyCompleted_keepsCompletedAt() {
        Task task = givenOwnTask(new Task(OWNER, "Task", null, null));
        Instant firstCompletion = NOW.minusSeconds(3600);
        task.complete(firstCompletion);
        saveReturnsArgument();

        TaskResponse response = taskService.changeStatus(OWNER, TASK_ID, TaskStatus.COMPLETED);

        assertThat(response.status()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(response.completedAt()).isEqualTo(firstCompletion);
    }

    @Test
    @DisplayName("TC-017 BR-14: reopening clears completedAt")
    void changeStatus_toPending_clearsCompletedAt() {
        Task task = givenOwnTask(new Task(OWNER, "Task", null, null));
        task.complete(NOW);
        saveReturnsArgument();

        TaskResponse response = taskService.changeStatus(OWNER, TASK_ID, TaskStatus.PENDING);

        assertThat(response.status()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.completedAt()).isNull();
    }

    @Test
    @DisplayName("TC-020 BR-20 BR-21: only PENDING tasks dated before today are overdue")
    void toResponse_marksOnlyPendingPastTasksOverdue() {
        Task pendingPast = new Task(OWNER, "Late", null, TODAY.minusDays(1));
        Task pendingToday = new Task(OWNER, "Today", null, TODAY);
        Task completedPast = new Task(OWNER, "Done", null, TODAY.minusDays(1));
        completedPast.complete(NOW);
        Task undated = new Task(OWNER, "Someday", null, null);

        assertThat(TaskResponse.from(pendingPast, TODAY).overdue()).isTrue();
        assertThat(TaskResponse.from(pendingToday, TODAY).overdue()).isFalse();
        assertThat(TaskResponse.from(completedPast, TODAY).overdue()).isFalse();
        assertThat(TaskResponse.from(undated, TODAY).overdue()).isFalse();
    }

    @Test
    @DisplayName("TC-024 BR-23: rescheduling changes only the due date")
    void reschedule_changesOnlyDueDate() {
        Task task = givenOwnTask(new Task(OWNER, "Task", "Notes", TODAY));
        task.complete(NOW);
        saveReturnsArgument();

        TaskResponse response = taskService.reschedule(OWNER, TASK_ID, TODAY.plusDays(7));

        assertThat(response.dueDate()).isEqualTo(TODAY.plusDays(7));
        assertThat(response.status()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(response.title()).isEqualTo("Task");
        assertThat(response.description()).isEqualTo("Notes");
    }

    @Test
    @DisplayName("BR-31: a task of another owner is not found")
    void findById_ofAnotherOwner_throwsNotFound() {
        when(taskRepository.findByIdAndOwnerId(TASK_ID, OWNER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(OWNER, TASK_ID))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("BR-40: delete removes the own task")
    void delete_ownTask_deletesIt() {
        Task task = givenOwnTask(new Task(OWNER, "Task", null, null));

        taskService.delete(OWNER, TASK_ID);

        verify(taskRepository).delete(task);
    }
}
