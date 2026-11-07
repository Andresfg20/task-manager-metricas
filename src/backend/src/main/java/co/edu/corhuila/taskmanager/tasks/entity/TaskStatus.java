package co.edu.corhuila.taskmanager.tasks.entity;

/**
 * Task statuses (BR-10). An enumeration, not an entity. "Overdue" is not a status:
 * it is derived when reading (BR-20).
 */
public enum TaskStatus {
    PENDING,
    COMPLETED
}
