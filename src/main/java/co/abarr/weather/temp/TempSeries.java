package co.abarr.weather.temp;

import co.abarr.weather.index.Index;
import co.abarr.weather.index.IndexSeries;
import co.abarr.weather.time.DateRange;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

/**
 * A ordered list of contiguous dates and their associated temperatures.
 * <p>
 * Created by adam on 01/12/2020.
 */
public final class TempSeries extends AbstractList<TempSeries.Entry> {
    private final IndexSeries temps;

    private TempSeries(IndexSeries temps) {
        this.temps = temps;
    }

    /**
     * The entry at the supplied index.
     */
    @Override
    public Entry get(int index) {
        IndexSeries.Entry entry = temps.get(index);
        return Entry.of(entry.date(), Temp.kelvin(entry.index().floatValue()));
    }

    /**
     * The number of entries in the series.
     */
    @Override
    public int size() {
        return temps.size();
    }

    /**
     * The mean of the series, if there is one.
     */
    public Optional<Temp> mean() {
        return temps.mean().map(index -> Temp.kelvin(index.floatValue()));
    }

    /**
     * Calculates a HDD (heating-degree-day) index from this series.
     * <p>
     * For each entry the HDD is the difference between that day's temp and the
     * supplied reference temp, capped at zero, in units of the reference temp.
     */
    public IndexSeries hdd(Temp reference) {
        return null;
    }

    /**
     * A date and its associated temperature.
     */
    public static class Entry {
        private final LocalDate date;
        private final Temp temp;

        private Entry(LocalDate date, Temp temp) {
            this.date = Objects.requireNonNull(date);
            this.temp = Objects.requireNonNull(temp);
        }

        /**
         * The date with which the temperature is associated.
         */
        public LocalDate date() {
            return date;
        }

        /**
         * The temperature on the date.
         */
        public Temp temp() {
            return temp;
        }

        /**
         * Converts the temperature to the supplied units.
         */
        public Entry to(TempUnits units) {
            return of(date, temp.to(units));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return date.equals(entry.date) && temp.equals(entry.temp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, temp);
        }

        @Override
        public String toString() {
            return date + "=" + temp;
        }

        /**
         * Creates a new entry.
         * <p>
         * An exception will be thrown if the date or temperature are null.
         */
        public static Entry of(LocalDate date, Temp temp) {
            return new Entry(date, temp);
        }
    }

    /**
     * Creates a series containing no entries.
     */
    public static TempSeries empty() {
        return of();
    }

    /**
     * Creates a series containing the supplied entries.
     * <p>
     * The dates of the supplied entries must be contiguous (ie there can be no
     * gaps), else an exception will be thrown.
     */
    public static TempSeries of(Map<LocalDate, Temp> map) {
        List<Entry> entries = new ArrayList<>(map.size());
        for (Map.Entry<LocalDate, Temp> entry : map.entrySet()) {
            entries.add(Entry.of(entry.getKey(), entry.getValue()));
        }
        return of(entries);
    }

    /**
     * Creates a series containing the supplied dates.
     */
    public static TempSeries of(DateRange dates, Function<LocalDate, Temp> temp) {
        List<Entry> entries = new ArrayList<>();
        for (LocalDate date : dates.all()) {
            entries.add(Entry.of(date, temp.apply(date)));
        }
        return of(entries);
    }

    /**
     * Creates a series containing the supplied entries.
     * <p>
     * The dates of the supplied entries must be contiguous (ie there can be no
     * gaps), else an exception will be thrown.
     */
    public static TempSeries of(Entry... entries) {
        return of(Arrays.asList(entries));
    }

    /**
     * Creates a series containing the supplied entries.
     * <p>
     * The dates of the supplied entries must be contiguous (ie there can be no
     * gaps), else an exception will be thrown.
     */
    public static TempSeries of(Iterable<? extends Entry> entries) {
        List<IndexSeries.Entry> index = new ArrayList<>();
        for (Entry entry : entries) {
            index.add(IndexSeries.Entry.of(entry.date, Index.of(entry.temp.toKelvin().floatValue())));
        }
        return new TempSeries(IndexSeries.of(index));
    }
}
