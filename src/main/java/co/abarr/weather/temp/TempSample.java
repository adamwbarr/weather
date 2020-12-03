package co.abarr.weather.temp;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A date and its associated temperature.
 */
public final class TempSample {
    private final LocalDate date;
    private final Temp temp;

    private TempSample(LocalDate date, Temp temp) {
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
     * Updates the temperature on the date.
     */
    public TempSample temp(Temp temp) {
        return new TempSample(date, temp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TempSample sample = (TempSample) o;
        return date.equals(sample.date) && temp.equals(sample.temp);
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
     * Creates a new sample.
     * <p>
     * An exception will be thrown if the date or temperature are null.
     */
    public static TempSample of(LocalDate date, Temp temp) {
        return new TempSample(date, temp);
    }
}
