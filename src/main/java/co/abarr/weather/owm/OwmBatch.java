package co.abarr.weather.owm;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempBag;
import co.abarr.weather.temp.TempSeries;

import java.time.LocalDate;
import java.util.*;

/**
 * An immutable batch of rows from an OpenWeatherMap bulk download.
 * <p>
 * Created by adam on 01/12/2020.
 */
public class OwmBatch extends AbstractList<OwmRow> {
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
     * The daily series of temperatures for each date in the batch.
     * <p>
     * The temperature for each date will be the midpoint between that day's
     * high and low.
     */
    public TempSeries daily() {
        Map<LocalDate, List<Temp>> dates = new HashMap<>();
        for (OwmRow row : this) {
            dates.computeIfAbsent(row.date(), date -> new ArrayList<>()).add(row.temp());
        }
        return TempSeries.of(dates, temps -> TempBag.of(temps).mid().orElse(null));
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
