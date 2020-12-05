package co.abarr.weather.math;

/**
 * A number representing a fraction.
 * <p>
 * Created by adam on 04/12/2020.
 */
public class Fraction extends Number implements Comparable<Fraction>{
    private final double value;

    private Fraction(double value) {
        this.value = value;
        if (Double.isNaN(value) || value < 0 || value > 1) {
            throw new IllegalArgumentException("Invalid probability: " + this);
        }
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fraction that = (Fraction) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int compareTo(Fraction o) {
        return Double.compare(value, o.value);
    }

    /**
     * Creates a new fraction.
     * <p>
     * An exception will be thrown if the value is NaN.
     */
    public static Fraction of(double value) {
        return new Fraction(value);
    }
}
