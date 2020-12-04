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
 * A series is guaranteed to contain at most one sample per date, and all
 * temperatures in the series are guaranteed to have the same units.
 * <p>
 * Created by adam on 01/12/2020.
 */
public final class TempSeries extends AbstractList<TempSample> {
    private final List<LocalDate> dates;
    private final double[] temps;
    private final TempUnits units;

    private TempSeries(List<LocalDate> dates, double[] temps, TempUnits units) {
        this.dates = dates;
        this.temps = temps;
        this.units = units;
    }

    /**
     * The sample at the supplied index.
     */
    @Override
    public TempSample get(int index) {
        return TempSample.of(dates.get(index), tempAt(index));
    }

    private Temp tempAt(int index) {
        return Temp.of(temps[index], units);
    }

    /**
     * The number of samples in the series.
     */
    @Override
    public int size() {
        return dates.size();
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
            groups.put(entry.getKey(), of(entry.getValue()));
        }
        return groups;
    }

    /**
     * Transforms the samples in this series.
     * <p>
     * The resulting series will contain for the same dates as this one.
     */
    public TempSeries map(BiFunction<LocalDate, Temp, Temp> transform) {
        double[] mapped = new double[size()];
        for (int i = 0; i < size(); i++) {
            LocalDate date = dates.get(i);
            Temp temp = tempAt(i);
            mapped[i] = transform.apply(date, temp).to(units).doubleValue();
        }
        return new TempSeries(dates, mapped, units);
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
            Temp sum = indexer.indexFor(tempAt(0));
            for (int i = 0; i < size(); i++) {
                sum = sum.plus(indexer.indexFor(tempAt(i)));
            }
            return sum;
        }
    }

    /**
     * The sum of the series.
     */
    public Temp sum() {
        double sum = 0;
        for (int i = 0; i < size(); i++) {
            sum += temps[i];
        }
        return Temp.of(sum, units);
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
     * The quadratic variation of the series.
     * <p>
     * No result will be returned if there are fewer than two samples in the series.
     */
    public Optional<Temp> qvar() {
        if (size() < 2) {
            return Optional.empty();
        } else {
            double sum = 0;
            for (int i = 1; i < size(); i++) {
                sum += Math.pow(temps[i] - temps[i - 1], 2);
            }
            return Optional.of(Temp.of(sum, units));
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
        Map<LocalDate, Temp> map = new HashMap<>();
        for (LocalDate date : dates.all()) {
            map.put(date, temp.apply(date));
        }
        return of(map);
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
        List<LocalDate> dates = new ArrayList<>(map.size());
        dates.addAll(map.keySet());
        dates.sort(Comparator.naturalOrder());
        TempUnits units;
        if (dates.isEmpty()) {
            units = TempUnits.KELVIN;
        } else {
            units = map.get(dates.get(0)).units();
        }
        double[] temps = new double[dates.size()];
        for (int i = 0; i < dates.size(); i++) {
            temps[i] = map.get(dates.get(i)).to(units).doubleValue();
        }
        return new TempSeries(dates, temps, units);
    }
}
