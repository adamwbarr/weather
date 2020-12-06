package co.abarr.weather.time;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.AbstractList;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;

/**
 * A range of dates.
 * <p>
 * Created by adam on 01/12/2020.
 */
public final class DateRange extends AbstractList<LocalDate> implements Set<LocalDate> {
    private final LocalDate start;
    private final int size;

    private DateRange(LocalDate start, int size) {
        this.start = Objects.requireNonNull(start);
        this.size = size;
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
        return of(start.plusDays(offset), end());
    }

    /**
     * Updates the start of the date range.
     */
    public DateRange start(LocalDate start) {
        return of(start, end());
    }

    /**
     * The end of the date range.
     */
    public LocalDate end() {
        return start.plusDays(size);
    }

    /**
     * Offsets the end of the date range.
     */
    public DateRange offsetEnd(int offset) {
        return end(end().plusDays(offset));
    }

    /**
     * Updates the end of the date range.
     */
    public DateRange end(LocalDate end) {
        return of(start, end);
    }

    /**
     * Whether this range contains the supplied object.
     */
    @Override
    public boolean contains(Object o) {
        if (o instanceof LocalDate) {
            LocalDate date = (LocalDate) o;
            return !start().isAfter(date) && end().isAfter(date);
        } else {
            return false;
        }
    }

    /**
     * Get a date in the range.
     */
    @Override
    public LocalDate get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(index);
        } else {
            return start.plusDays(index);
        }
    }

    @Override
    public Spliterator<LocalDate> spliterator() {
        return super.spliterator();
    }

    /**
     * The number of dates in the range.
     */
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRange range = (DateRange) o;
        return start.equals(range.start) && size == range.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, size);
    }

    @Override
    public String toString() {
        return String.format("[%s->%s]", start(), end());
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
        return of(start, start.plusMonths(1));
    }

    /**
     * Creates a new date range.
     * <p>
     * An exception will be thrown if the start or end are null, or the start
     * date is after the end date.
     */
    public static DateRange of(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException(String.format("Start (%s) is after end (%s)", start, end));
        } else {
            int size = (int) Math.max(1, ChronoUnit.DAYS.between(start, end));
            return new DateRange(start, size);
        }
    }
}
