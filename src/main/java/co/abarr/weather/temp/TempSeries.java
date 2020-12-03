package co.abarr.weather.temp;

import co.abarr.weather.time.DateRange;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.function.BiFunction;
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
     * The date range spanned by this series.
     * <p>
     * An exception will be thrown if the series is empty.
     */
    public DateRange dates() {
        if (isEmpty()) {
            throw new UnsupportedOperationException();
        } else {
            return DateRange.of(samples.get(0).date(), samples.get(size() - 1).date().plusDays(1));
        }
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
     * Groups this series into sub-series by year.
     */
    public Map<Year, TempSeries> groupByYear() {
        return groupBy(Year::from);
    }

    /**
     * Groups this series into sub-series by year-month.
     */
    public Map<Month, TempSeries> groupByMonth() {
        return groupBy(Month::from);
    }

    private <G> Map<G, TempSeries> groupBy(Function<LocalDate, G> grouper) {
        Map<G, List<TempSample>> lists = new LinkedHashMap<>();
        for (TempSample sample : this) {
            G group = grouper.apply(sample.date());
            lists.computeIfAbsent(group, key -> new ArrayList<>()).add(sample);
        }
        Map<G, TempSeries> groups = new LinkedHashMap<>();
        for (Map.Entry<G, List<TempSample>> entry : lists.entrySet()) {
            groups.put(entry.getKey(), new TempSeries(entry.getValue()));
        }
        return groups;
    }

    /**
     * Transforms the samples in this series.
     * <p>
     * The resulting series will contain for the same dates as this one.
     */
    public TempSeries map(BiFunction<LocalDate, Temp, Temp> transform) {
        List<TempSample> mapped = new ArrayList<>(size());
        for (TempSample sample : samples) {
            mapped.add(sample.temp(transform.apply(sample.date(), sample.temp())));
        }
        return new TempSeries(mapped);
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
     * The mean of the series.
     * <p>
     * An exception will be thrown if the series is empty.
     */
    public Temp mean() {
        if (isEmpty()) {
            throw new UnsupportedOperationException();
        } else {
            return sum().divideBy(size());
        }
    }

    /**
     * The quadratic variation of the series.
     * <p>
     * An exception will be thrown if the series contains less than 2 elements.
     */
    public Temp qvar() {
        if (size() < 2) {
            throw new UnsupportedOperationException();
        } else {
            Temp dsum = Temp.zero(get(0).temp().units());
            for (int i = 1; i < size(); i++) {

            }
            return null;
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
