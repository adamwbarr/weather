package co.abarr.weather.time;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * A range of dates.
 * <p>
 * Created by adam on 01/12/2020.
 */
public final class DateRange extends AbstractList<LocalDate> implements Set<LocalDate> {
    private final List<LocalDate> dates;
    private final LocalDate end;

    private DateRange(List<LocalDate> dates, LocalDate end) {
        this.dates = dates;
        this.end = end;
    }

    /**
     * The start of the date range (inclusive).
     */
    public LocalDate start() {
        return get(0);
    }

    /**
     * Offsets the start of the date range.
     */
    public DateRange offsetStart(int offset) {
        return of(start().plusDays(offset), end());
    }

    /**
     * Updates the start of the date range.
     */
    public DateRange start(LocalDate start) {
        return of(start, end());
    }

    /**
     * The end of the date range (exclusive).
     */
    public LocalDate end() {
        return end;
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
        return of(start(), end);
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
        return dates.get(index);
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
        return dates.size();
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
            List<LocalDate> dates = new ArrayList<>(size);
            LocalDate date = start;
            do {
                dates.add(date);
                date = date.plusDays(1);
            } while (date.isBefore(end));
            return new DateRange(dates, date);
        }
    }
}
