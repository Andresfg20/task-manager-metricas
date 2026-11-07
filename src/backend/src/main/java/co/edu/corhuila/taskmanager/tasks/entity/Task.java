package co.edu.corhuila.taskmanager.tasks.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * An activity a user records to follow it up (02-domain/entities-and-rules.md).
 * The owner is stored as an id, not as a User object, so this module does not
 * depend on the users module.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id", nullable = false, updatable = false)
    private UUID ownerId;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 10)
    private TaskStatus status;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Task() {
        // required by JPA
    }

    /** Every new task starts as PENDING and keeps its owner forever (BR-01, BR-05, BR-06). */
    public Task(UUID ownerId, String title, String description, LocalDate dueDate) {
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = TaskStatus.PENDING;
    }

    /** Edits title, description and due date; the status never changes here (BR-15). */
    public void updateDetails(String title, String description, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    /** PENDING to COMPLETED, storing completedAt; no change if already completed (BR-11 to BR-13). */
    public void complete(Instant now) {
        if (status == TaskStatus.COMPLETED) {
            return;
        }
        this.status = TaskStatus.COMPLETED;
        this.completedAt = now;
    }

    /** COMPLETED to PENDING, clearing completedAt; no change if already pending (BR-11, BR-12, BR-14). */
    public void reopen() {
        if (status == TaskStatus.PENDING) {
            return;
        }
        this.status = TaskStatus.PENDING;
        this.completedAt = null;
    }

    /** Changes only the due date (BR-23). */
    public void reschedule(LocalDate newDueDate) {
        this.dueDate = newDueDate;
    }

    /** A PENDING task whose due date is before today (BR-20, BR-21). */
    public boolean isOverdue(LocalDate today) {
        return status == TaskStatus.PENDING && dueDate != null && dueDate.isBefore(today);
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
