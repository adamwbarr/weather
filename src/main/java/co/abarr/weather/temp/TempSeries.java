package co.abarr.weather.temp;

import co.abarr.weather.time.DateRange;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

/**
 * A ordered list of temperature samples.
 * <p>
 * A series is guaranteed to contain at most one sample per date.
 * <p>
 * Created by adam on 01/12/2020.
 */
public final class TempSeries extends AbstractList<TempSample> {
    private final List<TempSample> samples;

    private TempSeries(List<TempSample> samples) {
        this.samples = samples;
    }

    /**
     * The sample at the supplied index.
     */
    @Override
    public TempSample get(int index) {
        return samples.get(index);
    }

    /**
     * The number of samples in the series.
     */
    @Override
    public int size() {
        return samples.size();
    }

    /**
     * Calculates an index from this series.
     * <p>
     * The resulting index value is the sum of applying the indexer to each
     * sample in this series.
     */
    public Temp index(TempIndexer indexer) {
        if (isEmpty()) {
            return Temp.zero(indexer.indexFor(Temp.kelvin(0)).units());
        } else {
            Temp sum = indexer.indexFor(get(0).temp());
            for (int i = 1; i < size(); i++) {
                sum = sum.plus(indexer.indexFor(get(i).temp()));
            }
            return sum;
        }
    }

    /**
     * The sum of the series.
     */
    public Temp sum() {
        if (isEmpty()) {
            return Temp.kelvin(0);
        } else {
            Temp sum = get(0).temp();
            for (int i = 1; i < size(); i++) {
                sum = sum.plus(get(i).temp());
            }
            return sum;
        }
    }

    /**
     * The mean of the series, if there is one.
     */
    public Optional<Temp> mean() {
        if (isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(sum().divideBy(size()));
        }
    }

    /**
     * Creates a series containing no samples.
     */
    public static TempSeries empty() {
        return of();
    }

    /**
     * Creates a series from a factory function.
     */
    public static TempSeries of(DateRange dates, Function<LocalDate, Temp> temp) {
        List<TempSample> samples = new ArrayList<>();
        for (LocalDate date : dates.all()) {
            samples.add(temp.apply(date).on(date));
        }
        return new TempSeries(samples);
    }

    /**
     * Creates a series containing the supplied samples.
     * <p>
     * If the units of the supplied temperatures are mismatched, a single
     */
    public static TempSeries of(TempSample... samples) {
        return of(Arrays.asList(samples));
    }

    /**
     * Creates a series containing the supplied samples.
     */
    public static TempSeries of(Iterable<? extends TempSample> samples) {
        Map<LocalDate, Temp> map = new HashMap<>();
        for (TempSample sample : samples) {
            map.put(sample.date(), sample.temp());
        }
        return of(map);
    }

    /**
     * Creates a series containing the supplied samples.
     */
    public static TempSeries of(Map<LocalDate, Temp> map) {
        List<TempSample> samples = new ArrayList<>(map.size());
        for (Map.Entry<LocalDate, Temp> entry : map.entrySet()) {
            samples.add(entry.getValue().on(entry.getKey()));
        }
        samples.sort(Comparator.comparing(TempSample::date));
        return new TempSeries(samples);
    }
}
