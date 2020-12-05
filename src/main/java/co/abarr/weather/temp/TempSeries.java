package co.abarr.weather.temp;

import co.abarr.weather.time.DateRange;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
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
public class TempSeries extends AbstractList<TempSeries.Entry> implements TempUnits.Having<TempSeries> {
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
     * The temp for the supplied date.
     */
    public Optional<Temp> get(LocalDate date) {
        return temps.get(date);
    }

    /**
     * The number of entries in the series.
     */
    @Override
    public int size() {
        return temps.size();
    }

    /**
     * The units of all temperatures in this vector.
     */
    @Override
    public TempUnits units() {
        return temps.units();
    }

    /**
     * Converts all temperatures in this vector to the supplied units.
     */
    @Override
    public TempSeries to(TempUnits units) {
        if (units().equals(units)) {
            return this;
        } else {
            return new TempSeries(temps.to(units));
        }
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
     * Subtracts the supplied series from this one.
     * <p>
     * An exception will be thrown if the dates do not match.
     */
    public TempSeries minus(TempSeries o) {
        return new TempSeries(temps.minus(o.temps));
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
     * Calculates an index from this series.
     */
    public Temp apply(TempIndexer indexer) {
        return indexer.indexFor(this);
    }

    /**
     * Rounds all temperatures to some number of decimal places.
     */
    public TempSeries round(int places) {
        return new TempSeries(temps.round(places));
    }

    /**
     * Groups this series into one subseries per-year.
     */
    public Grouping<Year> groupByYear() {
        return groupBy(Year::from);
    }

    /**
     * Groups this series into one subseries per-month.
     */
    public Grouping<Month> groupByMonth() {
        return groupBy(Month::from);
    }

    private <G extends Comparable<G>> Grouping<G> groupBy(Function<LocalDate, G> grouper) {
        Map<G, List<Entry>> lists = new TreeMap<>();
        for (Entry entry : this) {
            G group = grouper.apply(entry.date());
            lists.computeIfAbsent(group, key -> new ArrayList<>()).add(entry);
        }
        Map<G, TempSeries> groups = new TreeMap<>();
        for (Map.Entry<G, List<Entry>> entry : lists.entrySet()) {
            groups.put(entry.getKey(), of(entry.getValue()));
        }
        return new Grouping<>(groups);
    }

    /**
     * The result of grouping a series into distinct subseries.
     */
    public static class Grouping<G> extends AbstractMap<G, TempSeries> {
        private final Map<G, TempSeries> groups;

        private Grouping(Map<G, TempSeries> groups) {
            this.groups = groups;
        }

        /**
         * The series for the supplied key, if there is one.
         */
        @Override
        public TempSeries get(Object key) {
            return groups.get(key);
        }

        /**
         * An iterator over the groups in the grouping.
         */
        @Override
        public Set<Entry<G, TempSeries>> entrySet() {
            return Collections.unmodifiableSet(groups.entrySet());
        }

        /**
         * Reduces each series to a single temperature.
         * <p>
         * Null temperatures will be excluded from the resulting vector.
         */
        public TempVector<G> reduce(Function<TempSeries, Temp> reducer) {
            return TempVector.of(groups, reducer);
        }
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
        return new TempSeries(TempVector.of(dates.all(), temp));
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
        return ofPossiblyUnsorted(TempVector.of(vector));
    }

    /**
     * Creates a series containing the supplied temperatures.
     */
    public static TempSeries of(Map<LocalDate, Temp> map) {
        return ofPossiblyUnsorted(TempVector.of(map));
    }

    private static TempSeries ofPossiblyUnsorted(TempVector<LocalDate> vector) {
        return new TempSeries(vector.sortKeys());
    }
}
