package co.edu.corhuila.taskmanager.tasks.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.corhuila.taskmanager.tasks.entity.Task;
import co.edu.corhuila.taskmanager.tasks.entity.TaskStatus;

/**
 * Every query takes the owner id, so a user only ever reaches their own tasks (BR-30).
 */
public interface TaskRepository extends JpaRepository<Task, UUID> {

    Optional<Task> findByIdAndOwnerId(UUID id, UUID ownerId);

    /**
     * Own tasks, optionally filtered by status and/or overdue (BR-51), ordered by due
     * date with undated tasks last, then by creation (BR-50).
     */
    @Query(value = """
            select t from Task t
            where t.ownerId = :ownerId
              and (:status is null or t.status = :status)
              and (:overdueOnly = false
                   or (t.status = :pending and t.dueDate is not null and t.dueDate < :today))
            order by case when t.dueDate is null then 1 else 0 end, t.dueDate asc, t.createdAt asc
            """,
            countQuery = """
            select count(t) from Task t
            where t.ownerId = :ownerId
              and (:status is null or t.status = :status)
              and (:overdueOnly = false
                   or (t.status = :pending and t.dueDate is not null and t.dueDate < :today))
            """)
    Page<Task> search(@Param("ownerId") UUID ownerId,
                      @Param("status") TaskStatus status,
                      @Param("overdueOnly") boolean overdueOnly,
                      @Param("pending") TaskStatus pending,
                      @Param("today") LocalDate today,
                      Pageable pageable);

    /** Own dated tasks in an inclusive date range, for the Calendar View (BR-24). */
    List<Task> findByOwnerIdAndDueDateBetweenOrderByDueDateAscCreatedAtAsc(
            UUID ownerId, LocalDate from, LocalDate to);
}
