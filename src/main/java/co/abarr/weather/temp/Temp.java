package co.abarr.weather.temp;

import java.util.Objects;

/**
 * Created by adam on 30/11/2020.
 */
public final class Temp extends Number {
    private final double value;
    private final TempUnits units;

    private Temp(double value, TempUnits units) {
        this.value = value;
        this.units = Objects.requireNonNull(units);
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid temp: " + this);
        }
    }

    /**
     * The temperature as a 16-bit integer.
     */
    @Override
    public int intValue() {
        return (int) value;
    }

    /**
     * The temperature as an 32-bit long.
     */
    @Override
    public long longValue() {
        return (long) value;
    }

    /**
     * The temperature as an 16-bit float.
     */
    @Override
    public float floatValue() {
        return (float) value;
    }

    /**
     * The temperature as a 32-bit double.
     */
    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s%s", value, units.shortCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Temp temp = (Temp) o;
        return Double.compare(temp.value, value) == 0 && units == temp.units;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, units);
    }

    /**
     * Creates a new Kelvin temperature.
     * <p>
     * An exception will be thrown if the value is NaN or infinite.
     */
    public static Temp kelvin(double value) {
        return of(value, TempUnits.KELVIN);
    }

    /**
     * Creates a new temperature.
     * <p>
     * An exception will be thrown if the value is NaN or infinite, or the
     * units are null.
     */
    public static Temp of(double value, TempUnits units) {
        return new Temp(value, units);
    }
}
