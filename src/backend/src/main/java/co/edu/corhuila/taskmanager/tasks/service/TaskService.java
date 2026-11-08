package co.edu.corhuila.taskmanager.tasks.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.corhuila.taskmanager.common.dto.PageResponse;
import co.edu.corhuila.taskmanager.common.exception.NotFoundException;
import co.edu.corhuila.taskmanager.tasks.dto.TaskRequest;
import co.edu.corhuila.taskmanager.tasks.dto.TaskResponse;
import co.edu.corhuila.taskmanager.tasks.entity.Task;
import co.edu.corhuila.taskmanager.tasks.entity.TaskStatus;
import co.edu.corhuila.taskmanager.tasks.repository.TaskRepository;

/**
 * All task rules live here (AB-06). Every method receives the authenticated owner id
 * and only touches that owner's tasks (BR-30, BR-31).
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final Clock clock;

    public TaskService(TaskRepository taskRepository, Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    @Transactional
    public TaskResponse create(UUID ownerId, TaskRequest request) {
        Task task = new Task(ownerId, request.title().trim(), normalizeDescription(request.description()),
                request.dueDate());
        return toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(UUID ownerId, UUID taskId) {
        return toResponse(getOwnTask(ownerId, taskId));
    }

    @Transactional
    public TaskResponse update(UUID ownerId, UUID taskId, TaskRequest request) {
        Task task = getOwnTask(ownerId, taskId);
        task.updateDetails(request.title().trim(), normalizeDescription(request.description()),
                request.dueDate());
        return toResponse(taskRepository.save(task));
    }

    /** Complete or reopen; setting the current status again changes nothing (BR-11 to BR-14). */
    @Transactional
    public TaskResponse changeStatus(UUID ownerId, UUID taskId, TaskStatus newStatus) {
        Task task = getOwnTask(ownerId, taskId);
        if (newStatus == TaskStatus.COMPLETED) {
            task.complete(clock.instant());
        } else {
            task.reopen();
        }
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse reschedule(UUID ownerId, UUID taskId, LocalDate newDueDate) {
        Task task = getOwnTask(ownerId, taskId);
        task.reschedule(newDueDate);
        return toResponse(taskRepository.save(task));
    }

    /** Permanent delete; there is no trash (BR-40). */
    @Transactional
    public void delete(UUID ownerId, UUID taskId) {
        taskRepository.delete(getOwnTask(ownerId, taskId));
    }

    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> findPage(UUID ownerId, TaskStatus status, boolean overdueOnly,
            int page, int size) {
        LocalDate today = today();
        var pageable = PageRequest.of(PageResponse.safePage(page), PageResponse.safeSize(size));
        var result = taskRepository.search(ownerId, status, overdueOnly, TaskStatus.PENDING, today, pageable);
        return PageResponse.from(result, task -> TaskResponse.from(task, today));
    }

    private Task getOwnTask(UUID ownerId, UUID taskId) {
        return taskRepository.findByIdAndOwnerId(taskId, ownerId)
                .orElseThrow(() -> new NotFoundException("TASK_NOT_FOUND", "The task does not exist."));
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.from(task, today());
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }

    private static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}
