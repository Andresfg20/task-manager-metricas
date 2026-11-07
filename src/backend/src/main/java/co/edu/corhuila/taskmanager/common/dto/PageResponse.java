package co.edu.corhuila.taskmanager.common.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

/**
 * Paginated response body (07-api/rest-conventions.md, BR-52).
 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    public static <E, T> PageResponse<T> from(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(page.getContent().stream().map(mapper).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    /** Keeps page at 0 or more and size between 1 and 100. */
    public static int safePage(int page) {
        return Math.max(page, 0);
    }

    public static int safeSize(int size) {
        return Math.min(Math.max(size, 1), MAX_SIZE);
    }
}
