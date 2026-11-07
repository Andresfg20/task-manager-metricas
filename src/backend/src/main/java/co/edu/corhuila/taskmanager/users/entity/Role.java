package co.edu.corhuila.taskmanager.users.entity;

/**
 * User roles. An enumeration, not an entity: no CRUD and no table of its own.
 * ADMIN exists only to meet the course guide's role-based authorization
 * requirement; it is not a functional need of the MVP (ADR-005).
 */
public enum Role {
    ADMIN,
    USER
}
