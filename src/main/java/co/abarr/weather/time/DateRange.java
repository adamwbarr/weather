package co.abarr.weather.time;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by adam on 01/12/2020.
 */
public final class DateRange {
    private final LocalDate start;
    private final LocalDate end;

    private DateRange(LocalDate start, LocalDate end) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid date range: " + this);
        }
    }

    /**
     * The start of the date range.
     */
    public LocalDate start() {
        return start;
    }

    /**
     * Offsets the start of the date range.
     */
    public DateRange offsetStart(int offset) {
        return new DateRange(start.plusDays(offset), end);
    }

    /**
     * Updates the start of the date range.
     */
    public DateRange start(LocalDate start) {
        return new DateRange(start, end);
    }

    /**
     * The end of the date range.
     */
    public LocalDate end() {
        return end;
    }

    /**
     * Offsets the end of the date range.
     */
    public DateRange offsetEnd(int offset) {
        return new DateRange(start, end.plusDays(offset));
    }

    /**
     * Updates the end of the date range.
     */
    public DateRange end(LocalDate end) {
        return new DateRange(start, end);
    }

    /**
     * The number of dates in the range.
     */
    public int size() {
        return all().size();
    }

    /**
     * All dates in the range from start (inclusive) to end (exclusive).
     */
    public List<LocalDate> all() {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate date = start;
        do {
            dates.add(date);
            date = date.plusDays(1);
        } while (date.isBefore(end));
        return dates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRange dateRange = (DateRange) o;
        return start.equals(dateRange.start) && end.equals(dateRange.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return String.format("[%s->%s]", start, end);
    }

    /**
     * Creates a new date range spanning a calendar year.
     */
    public static DateRange year(int year) {
        return of(LocalDate.of(year, 1, 1), LocalDate.of(year + 1, 1, 1));
    }

    /**
     * Creates a new date range spanning a calendar month.
     */
    public static DateRange yearMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        return of(start, start.withDayOfMonth(start.lengthOfMonth()));
    }

    /**
     * Creates a new date range.
     * <p>
     * An exception will be thrown if the start or end are null, or the start
     * date is after the end date.
     */
    public static DateRange of(LocalDate start, LocalDate end) {
        return new DateRange(start, end);
    }
}
