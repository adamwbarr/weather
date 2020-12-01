package co.abarr.weather.temp;

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
    private final LocalDate start;
    private final float[] temps;

    private TempSeries(LocalDate start, float[] temps) {
        this.start = start;
        this.temps = temps;
    }

    /**
     * The entry at the supplied index.
     */
    @Override
    public Entry get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(index);
        } else {
            LocalDate date = start.plusDays(index);
            Temp temp = Temp.kelvin(temps[index]);
            return Entry.of(date, temp);
        }
    }

    /**
     * The number of entries in the series.
     */
    @Override
    public int size() {
        return temps.length;
    }

    /**
     * The mean of the series, if there is one.
     */
    public Optional<Temp> mean() {
        if (isEmpty()) {
            return Optional.empty();
        } else {
            float sum = 0;
            for (float temp : temps) {
                sum += temp;
            }
            return Optional.of(Temp.kelvin(sum / temps.length));
        }
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
        List<Entry> list = new ArrayList<>();
        entries.forEach(list::add);
        list.sort(Comparator.comparing(Entry::date));
        if (list.isEmpty()) {
            return new TempSeries(null, new float[0]);
        } else {
            float[] temps = new float[list.size()];
            LocalDate start = list.get(0).date;
            LocalDate expected = start;
            for (int i = 0; i < list.size(); i++) {
                Entry entry = list.get(i);
                if (!entry.date.equals(expected)) {
                    throw new IllegalArgumentException(String.format("Invalid dates: expected %s, found %s", expected, entry.date));
                }
                temps[i] = entry.temp.toKelvin().floatValue();
                expected = expected.plusDays(1);
            }
            return new TempSeries(start, temps);
        }
    }
}
