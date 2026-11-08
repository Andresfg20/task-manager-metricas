package co.edu.corhuila.taskmanager.tasks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.corhuila.taskmanager.common.exception.BadRequestException;
import co.edu.corhuila.taskmanager.tasks.dto.TaskResponse;
import co.edu.corhuila.taskmanager.tasks.entity.Task;
import co.edu.corhuila.taskmanager.tasks.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskCalendarServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-02T15:00:00Z");
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 2);
    private static final UUID OWNER = UUID.randomUUID();

    @Mock
    private TaskRepository taskRepository;

    private TaskCalendarService calendarService;

    @BeforeEach
    void setUp() {
        calendarService = new TaskCalendarService(taskRepository, Clock.fixed(NOW, ZoneId.of("America/Bogota")));
    }

    @Test
    @DisplayName("TC-023: a calendar range over 42 days or reversed is rejected")
    void findCalendar_withInvalidRange_throwsInvalidRange() {
        assertThatThrownBy(() -> calendarService.findCalendar(OWNER, TODAY, TODAY.plusDays(42)))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> calendarService.findCalendar(OWNER, TODAY, TODAY.minusDays(1)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("BR-24: a 42-day range is accepted and returns the owner's dated tasks")
    void findCalendar_with42DayRange_returnsTasks() {
        LocalDate to = TODAY.plusDays(41);
        Task task = new Task(OWNER, "Exam", null, TODAY.plusDays(3));
        when(taskRepository.findByOwnerIdAndDueDateBetweenOrderByDueDateAscCreatedAtAsc(OWNER, TODAY, to))
                .thenReturn(List.of(task));

        List<TaskResponse> result = calendarService.findCalendar(OWNER, TODAY, to);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Exam");
    }

    @Test
    @DisplayName("BR-24: CalendarRange keeps the requested limits")
    void calendarRange_of_keepsLimits() {
        CalendarRange range = CalendarRange.of(TODAY, TODAY);
        assertThat(range.from()).isEqualTo(TODAY);
        assertThat(range.to()).isEqualTo(TODAY);
        assertThat(CalendarRange.MAX_DAYS).isEqualTo(42);
    }
}
