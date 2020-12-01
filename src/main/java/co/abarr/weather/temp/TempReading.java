package co.abarr.weather.temp;

import co.abarr.weather.location.Location;

import java.time.Instant;
import java.util.Objects;

/**
 * A temperature recorded at a particular place and time.
 * <p>
 * Created by adam on 30/11/2020.
 */
public final class TempReading {
    private final Location location;
    private final Instant time;
    private final Temp temp;

    private TempReading(Location location, Instant time, Temp temp) {
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
        TempReading that = (TempReading) o;
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
     * Creates a new temperature reading.
     * <p>
     * An exception will be thrown if any parameter is null.
     */
    public static TempReading of(Location location, Instant time, Temp temp) {
        return new TempReading(location, time, temp);
    }
}
