package co.abarr.weather.index;

import co.abarr.weather.time.DateRange;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

/**
 * A ordered list of contiguous dates and their associated index values.
 * <p>
 * Created by adam on 01/12/2020.
 */
public class IndexSeries extends AbstractList<IndexSeries.Entry> {
    private final LocalDate start;
    private final float[] values;

    private IndexSeries(LocalDate start, float[] values) {
        this.start = start;
        this.values = values;
    }

    /**
     * The entry at the supplied index.
     */
    @Override
    public Entry get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(index);
        } else {
            return Entry.of(start.plusDays(index), Index.of(values[index]));
        }
    }

    /**
     * The number of entries in the series.
     */
    @Override
    public int size() {
        return values.length;
    }

    /**
     * The mean of the series, if there is one.
     */
    public Optional<Index> mean() {
        if (isEmpty()) {
            return Optional.empty();
        } else {
            float sum = 0;
            for (float value : values) {
                sum += value;
            }
            return Optional.of(Index.of(sum / values.length));
        }
    }

    /**
     * A date and its associated index value.
     */
    public static class Entry {
        private final LocalDate date;
        private final Index index;

        private Entry(LocalDate date, Index index) {
            this.date = Objects.requireNonNull(date);
            this.index = Objects.requireNonNull(index);
        }

        /**
         * The date with which the temperature is associated.
         */
        public LocalDate date() {
            return date;
        }

        /**
         * The index on the date.
         */
        public Index index() {
            return index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return date.equals(entry.date) && index.equals(entry.index);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, index);
        }

        @Override
        public String toString() {
            return date + "=" + index;
        }

        /**
         * Creates a new entry.
         * <p>
         * An exception will be thrown if the date or index are null.
         */
        public static Entry of(LocalDate date, Index index) {
            return new Entry(date, index);
        }
    }

    /**
     * Creates a series containing no entries.
     */
    public static IndexSeries empty() {
        return of();
    }

    /**
     * Creates a series containing the supplied entries.
     * <p>
     * The dates of the supplied entries must be contiguous (ie there can be no
     * gaps), else an exception will be thrown.
     */
    public static IndexSeries of(Entry... entries) {
        return of(Arrays.asList(entries));
    }

    /**
     * Creates a series containing the supplied entries.
     * <p>
     * The dates of the supplied entries must be contiguous (ie there can be no
     * gaps), else an exception will be thrown.
     */
    public static IndexSeries of(Map<LocalDate, Index> map) {
        List<Entry> entries = new ArrayList<>(map.size());
        for (Map.Entry<LocalDate, Index> entry : map.entrySet()) {
            entries.add(Entry.of(entry.getKey(), entry.getValue()));
        }
        return of(entries);
    }

    /**
     * Creates a series from a factory function.
     * <p>
     * The resulting series will contain an entry for all dates in the range.
     */
    public static IndexSeries of(DateRange dates, Function<LocalDate, Index> factory) {
        float[] values = new float[dates.size()];
        int i = 0;
        for (LocalDate date : dates.all()) {
            values[i++] = factory.apply(date).floatValue();
        }
        return new IndexSeries(dates.start(), values);
    }

    /**
     * Creates a series containing the supplied entries.
     * <p>
     * The dates of the supplied entries must be contiguous (ie there can be no
     * gaps), else an exception will be thrown.
     */
    public static IndexSeries of(Iterable<? extends Entry> entries) {
        List<Entry> list = new ArrayList<>();
        entries.forEach(list::add);
        list.sort(Comparator.comparing(Entry::date));
        if (list.isEmpty()) {
            return new IndexSeries(null, new float[0]);
        } else {
            float[] values = new float[list.size()];
            LocalDate start = list.get(0).date;
            LocalDate expected = start;
            for (int i = 0; i < list.size(); i++) {
                Entry entry = list.get(i);
                if (!entry.date.equals(expected)) {
                    throw new IllegalArgumentException(String.format("Invalid dates: expected %s, found %s", expected, entry.date));
                }
                values[i] = entry.index.floatValue();
                expected = expected.plusDays(1);
            }
            return new IndexSeries(start, values);
        }
    }
}
