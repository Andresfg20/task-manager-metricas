package co.edu.corhuila.taskmanager.tasks.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.corhuila.taskmanager.tasks.dto.TaskResponse;
import co.edu.corhuila.taskmanager.tasks.repository.TaskRepository;

/**
 * Calendar View query (RF-08). Extracted from TaskService to lower its WMC and coupling:
 * this class only reads the owner's dated tasks inside a validated range.
 */
@Service
public class TaskCalendarService {

    private final TaskRepository taskRepository;
    private final Clock clock;

    public TaskCalendarService(TaskRepository taskRepository, Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findCalendar(UUID ownerId, LocalDate from, LocalDate to) {
        CalendarRange range = CalendarRange.of(from, to);
        LocalDate today = LocalDate.now(clock);
        return taskRepository
                .findByOwnerIdAndDueDateBetweenOrderByDueDateAscCreatedAtAsc(ownerId, range.from(), range.to())
                .stream()
                .map(task -> TaskResponse.from(task, today))
                .toList();
    }
}
