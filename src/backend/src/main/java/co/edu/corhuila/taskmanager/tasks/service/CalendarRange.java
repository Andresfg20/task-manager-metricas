package co.edu.corhuila.taskmanager.tasks.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import co.edu.corhuila.taskmanager.common.exception.BadRequestException;

/**
 * Inclusive date range requested by the Calendar View (BR-24).
 * A valid range is created only through {@link #of(LocalDate, LocalDate)}.
 */
public record CalendarRange(LocalDate from, LocalDate to) {

    /** A month grid of 6 weeks: the largest range the Calendar View requests. */
    static final int MAX_DAYS = 42;

    public static CalendarRange of(LocalDate from, LocalDate to) {
        if (to.isBefore(from) || ChronoUnit.DAYS.between(from, to) + 1 > MAX_DAYS) {
            throw new BadRequestException("INVALID_DATE_RANGE",
                    "'to' must not be before 'from', and the range must cover at most 42 days.");
        }
        return new CalendarRange(from, to);
    }
}
