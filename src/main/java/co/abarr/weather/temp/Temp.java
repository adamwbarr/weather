package co.abarr.weather.temp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * A temperature (with associated units).
 * <p>
 * Created by adam on 30/11/2020.
 */
public class Temp extends Number implements Comparable<Temp>, TempUnits.Having<Temp> {
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

    /**
     * The units of this temperature.
     */
    @Override
    public TempUnits units() {
        return units;
    }

    /**
     * Converts this temperature to the supplied units.
     */
    @Override
    public Temp to(TempUnits units) {
        return Temp.of(units.convert(value, this.units), units);
    }

    /**
     * Adds this and the supplied temperatures.
     * <p>
     * The resulting temperature will be in the units of this temperature.
     */
    public Temp plus(Temp o) {
        return Temp.of(value + o.toUnitsOf(this).value, units);
    }

    /**
     * Subtracts the supplied temperature from this one.
     * <p>
     * The resulting temperature will be in the units of this temperature.
     */
    public Temp minus(Temp o) {
        return Temp.of(value - o.toUnitsOf(this).value, units);
    }

    /**
     * Divides this temperature by a scalar.
     * <p>
     * The resulting temperature will be in the units of this temperature. An
     * exception will be thrown if the scalar is zero.
     */
    public Temp divideBy(int scalar) {
        if (scalar == 0) {
            throw new ArithmeticException("Divide by zero");
        } else {
            return of(value / scalar, units);
        }
    }

    /**
     * Rounds this temperatures to some number of decimal places.
     */
    public Temp round(int places) {
        BigDecimal decimal = BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP);
        return new Temp(decimal.doubleValue(), units);
    }

    @Override
    public int compareTo(Temp o) {
        return Double.compare(toKelvin().value, o.toKelvin().value);
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
     * Creates a new Fahrenheit temperature.
     * <p>
     * An exception will be thrown if the value is NaN or infinite.
     */
    public static Temp fahrenheit(double value) {
        return of(value, TempUnits.FAHRENHEIT);
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
     * Creates a new zero temperature.
     */
    public static Temp zero(TempUnits units) {
        return of(0, units);
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
