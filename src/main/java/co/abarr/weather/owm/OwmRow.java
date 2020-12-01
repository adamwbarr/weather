package co.abarr.weather.owm;

import co.abarr.weather.location.Location;
import co.abarr.weather.temp.Temp;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * An entry from an OpenWeatherMap bulk download.
 * <p>
 * Created by adam on 30/11/2020.
 */
public final class OwmRow {
    private final Location location;
    private final Instant time;
    private final Temp temp;

    private OwmRow(Location location, Instant time, Temp temp) {
        this.location = Objects.requireNonNull(location);
        this.time = Objects.requireNonNull(time);
        this.temp = Objects.requireNonNull(temp);
    }

    /**
     * Where the temperature was read.
     */
    public Location location() {
        return location;
    }

    /**
     * When the temperature was read.
     */
    public Instant time() {
        return time;
    }

    /**
     * The temperature that was read.
     */
    public Temp temp() {
        return temp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwmRow that = (OwmRow) o;
        return location.equals(that.location) && time.equals(that.time) && temp.equals(that.temp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, time, temp);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]:%s", location, time, temp);
    }

    /**
     * Rows parsed from central_park.csv download file.
     */
    public static List<OwmRow> centralParkCsv() {
        return FromCsv.readFromCentralParkCsv();
    }

    /**
     * Creates a new row.
     * <p>
     * An exception will be thrown if any parameter is null.
     */
    public static OwmRow of(Location location, Instant time, Temp temp) {
        return new OwmRow(location, time, temp);
    }
}
