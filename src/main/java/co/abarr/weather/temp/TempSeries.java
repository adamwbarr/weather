package co.abarr.weather.temp;

import co.abarr.weather.time.DateRange;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A date keyed and ordered list of temperatures.
 * <p>
 * A series is guaranteed to contain at most one entry per date, and all
 * temperatures in the series are guaranteed to have the same units.
 * <p>
 * Created by adam on 03/12/2020.
 */
public class TempSeries extends AbstractList<TempSeries.Entry> {
    private final TempVector<LocalDate> temps;

    private TempSeries(TempVector<LocalDate> temps) {
        this.temps = temps;
    }

    /**
     * The entry at the supplied index.
     */
    @Override
    public Entry get(int index) {
        return new Entry(temps.get(index));
    }

    /**
     * The number of entries in the series.
     */
    @Override
    public int size() {
        return temps.size();
    }

    /**
     * Groups this series into sub-series by function.
     */
    public <G> Map<G, TempSeries> groupBy(Function<LocalDate, G> grouper) {
        Map<G, TempVector<LocalDate>> groups = temps.groupBy(grouper);
        Map<G, TempSeries> series = new HashMap<>(groups.size());
        for (Map.Entry<G, TempVector<LocalDate>> entry : groups.entrySet()) {
            series.put(entry.getKey(), of(entry.getValue()));
        }
        return series;
    }

    /**
     * Transforms the entries in this series.
     * <p>
     * The resulting series will contain for the same dates as this one.
     */
    public TempSeries map(BiFunction<LocalDate, Temp, Temp> transform) {
        return new TempSeries(temps.map(transform));
    }

    /**
     * The sum of the series.
     */
    public Temp sum() {
        return temps.sum();
    }

    /**
     * The mean of the series, if there is one.
     */
    public Optional<Temp> mean() {
       return temps.mean();
    }

    /**
     * The quadratic variation of the series.
     * <p>
     * No result will be returned if there are fewer than two entries in the series.
     */
    public Optional<Temp> qvar() {
       return temps.qvar();
    }

    /**
     * Creates a new entry.
     * <p>
     * An exception will be thrown if the date or temperature are null.
     */
    public static Entry entry(LocalDate date, Temp temp) {
        return new Entry(TempVector.entry(date, temp));
    }

    /**
     * A date and its associated temperature.
     */
    public static final class Entry {
        private final TempVector.Entry<LocalDate> entry;

        private Entry(TempVector.Entry<LocalDate> entry) {
            this.entry = Objects.requireNonNull(entry);
        }

        /**
         * The date with which the temperature is associated.
         */
        public LocalDate date() {
            return entry.key();
        }

        /**
         * The temperature associated with the date.
         */
        public Temp temp() {
            return entry.temp();
        }

        /**
         * Updates the temperature for the date.
         */
        public Entry temp(Temp temp) {
            return new Entry(entry.temp(temp));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return this.entry.equals(entry.entry);
        }

        @Override
        public int hashCode() {
            return entry.hashCode();
        }

        @Override
        public String toString() {
            return entry.toString();
        }
    }

    /**
     * Creates a series containing no entries.
     */
    public static TempSeries empty() {
        return of();
    }

    /**
     * Creates a series from a factory function.
     */
    public static TempSeries of(DateRange dates, Function<LocalDate, Temp> temp) {
        return of(TempVector.of(dates.all(), temp));
    }

    /**
     * Creates a series containing the supplied entries.
     */
    public static TempSeries of(Entry... entries) {
        return of(Arrays.asList(entries));
    }

    /**
     * Creates a series containing the supplied entries.
     */
    public static TempSeries of(Iterable<? extends Entry> entries) {
        List<TempVector.Entry<LocalDate>> vector = new ArrayList<>();
        for (Entry entry : entries) {
            vector.add(entry.entry);
        }
        return of(TempVector.of(vector));
    }

    /**
     * Creates a series containing the supplied temperatures.
     */
    public static TempSeries of(Map<LocalDate, Temp> map) {
        return of(TempVector.of(map));
    }

    private static TempSeries of(TempVector<LocalDate> vector) {
        return new TempSeries(vector.sortKeys());
    }
}
