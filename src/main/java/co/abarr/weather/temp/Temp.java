package co.abarr.weather.temp;

import java.util.Objects;

/**
 * A temperature (with associated units).
 * <p>
 * Created by adam on 30/11/2020.
 */
public final class Temp extends Number implements Comparable<Temp> {
    private final float value;
    private final TempUnits units;

    private Temp(float value, TempUnits units) {
        this.value = value;
        this.units = Objects.requireNonNull(units);
        if (!Float.isFinite(value)) {
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
        return value;
    }

    /**
     * The temperature as a 32-bit double.
     */
    @Override
    public double doubleValue() {
        return value;
    }

    /**
     * The units of this temperature.
     */
    public TempUnits units() {
        return units;
    }

    /**
     * Converts this temperature to Kelvins.
     */
    public Temp toKelvin() {
        return to(TempUnits.KELVIN);
    }

    /**
     * Converts this temperature to Fahrenheit.
     */
    public Temp toFahrenheit() {
        return to(TempUnits.FAHRENHEIT);
    }

    /**
     * Converts this temperature to Celsius.
     */
    public Temp toCelsius() {
        return to(TempUnits.CELSIUS);
    }

    /**
     * Converts this temperature to the units of the supplied temperature.
     */
    public Temp toUnitsOf(Temp o) {
        return to(o.units);
    }

    /**
     * Converts this temperature to the supplied units.
     */
    public Temp to(TempUnits units) {
        if (this.units == units) {
            return this;
        } else {
            return fromKelvin(kelvin(), units);
        }
    }

    private float kelvin() {
        return switch (units) {
            case KELVIN -> value;
            case FAHRENHEIT -> (value - 32f) * 5 / 9f + 273.15f;
            case CELSIUS -> value + 273.15f;
        };
    }

    private static Temp fromKelvin(float kelvin, TempUnits unit) {
        return switch (unit) {
            case KELVIN -> kelvin(kelvin);
            case FAHRENHEIT -> fahrenheit((kelvin - 273.15f) * 9 / 5f + 32);
            case CELSIUS -> celsius(kelvin - 273.15f);
        };
    }

    @Override
    public int compareTo(Temp o) {
        return Float.compare(toKelvin().value, o.toKelvin().value);
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
        return Float.compare(temp.value, value) == 0 && units == temp.units;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, units);
    }

    /**
     * Creates a new Fahrenheit temperature.
     * <p>
     * An exception will be thrown if the value is NaN or infinite.
     */
    public static Temp fahrenheit(double value) {
        return fahrenheit((float) value);
    }

    /**
     * Creates a new Celsius temperature.
     * <p>
     * An exception will be thrown if the value is NaN or infinite.
     */
    public static Temp celsius(double value) {
        return of(value, TempUnits.CELSIUS);
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
        return of((float) value, units);
    }

    /**
     * Creates a new Fahrenheit temperature.
     * <p>
     * An exception will be thrown if the value is NaN or infinite.
     */
    public static Temp fahrenheit(float value) {
        return of(value, TempUnits.FAHRENHEIT);
    }

    /**
     * Creates a new Celsius temperature.
     * <p>
     * An exception will be thrown if the value is NaN or infinite.
     */
    public static Temp celsius(float value) {
        return of(value, TempUnits.CELSIUS);
    }

    /**
     * Creates a new Kelvin temperature.
     * <p>
     * An exception will be thrown if the value is NaN or infinite.
     */
    public static Temp kelvin(float value) {
        return of(value, TempUnits.KELVIN);
    }

    /**
     * Creates a new temperature.
     * <p>
     * An exception will be thrown if the value is NaN or infinite, or the
     * units are null.
     */
    public static Temp of(float value, TempUnits units) {
        return new Temp(value, units);
    }
}
