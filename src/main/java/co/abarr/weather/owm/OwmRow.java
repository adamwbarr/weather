package co.abarr.weather.owm;

import co.abarr.weather.temp.Temp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * An row from an OpenWeatherMap bulk download.
 * <p>
 * Created by adam on 30/11/2020.
 */
public class OwmRow {
    private final Location location;
    private final Instant time;
    private final ZoneOffset zone;
    private final Temp temp;

    private OwmRow(Location location, Instant time, ZoneOffset zone, Temp temp) {
        this.location = Objects.requireNonNull(location);
        this.time = Objects.requireNonNull(time);
        this.zone = Objects.requireNonNull(zone);
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
     * The symbolic (local) date associated with the row.
     */
    public LocalDate date() {
        return time.atOffset(zone).toLocalDate();
    }

    /**
     * The temperature that was read.
     */
    public Temp temp() {
        return temp;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]:%s", location, time, temp);
    }

    /**
     * Creates a new row.
     * <p>
     * An exception will be thrown if any parameter is null.
     */
    public static OwmRow of(Location location, Instant time, Temp temp) {
        return of(location, time, ZoneOffset.UTC, temp);
    }

    /**
     * Creates a new row.
     * <p>
     * An exception will be thrown if any parameter is null.
     */
    public static OwmRow of(Location location, Instant time, ZoneOffset zone, Temp temp) {
        return new OwmRow(location, time, zone, temp);
    }
}
