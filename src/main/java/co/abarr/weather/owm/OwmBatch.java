package co.abarr.weather.owm;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSeries;

import java.time.LocalDate;
import java.util.*;

/**
 * An immutable batch of rows from an OpenWeatherMap bulk download.
 * <p>
 * Created by adam on 01/12/2020.
 */
public final class OwmBatch extends AbstractList<OwmRow> {
    private final List<OwmRow> rows;

    private OwmBatch(List<OwmRow> rows) {
        this.rows = rows;
    }

    /**
     * The row at the supplied index.
     */
    @Override
    public OwmRow get(int index) {
        return rows.get(index);
    }

    /**
     * The number of rows in the batch.
     */
    @Override
    public int size() {
        return rows.size();
    }

    /**
     * The series of max temperatures for each date in the batch.
     * <p>
     * Note - this method will fail if the batch does not contain contiguous
     * dates.
     */
    public TempSeries maxs() {
        Map<LocalDate, Temp> maxs = new HashMap<>();
        for (OwmRow row : this) {
            maxs.merge(row.date(), row.temp(), OwmBatch::max);
        }
        return TempSeries.of(maxs);
    }

    private static Temp max(Temp x, Temp y) {
        if (x.compareTo(y) < 0) {
            return y;
        } else {
            return x;
        }
    }

    /**
     * Rows parsed from central_park.csv download file.
     */
    public static OwmBatch centralParkCsv() {
        return FromCsv.readFromCentralParkCsv();
    }

    /**
     * Creates a batch containing no rows.
     */
    public static OwmBatch empty() {
        return of();
    }

    /**
     * Creates a batch of the supplied rows.
     */
    public static OwmBatch of(OwmRow... rows) {
        return of(Arrays.asList(rows));
    }

    /**
     * Creates a batch of the supplied rows.
     */
    public static OwmBatch of(Iterable<? extends OwmRow> rows) {
        List<OwmRow> list = new ArrayList<>();
        rows.forEach(list::add);
        return new OwmBatch(list);
    }
}
